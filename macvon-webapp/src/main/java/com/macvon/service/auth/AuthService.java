package com.macvon.service.auth;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.macvon.dao.user.UserDAO;
import com.macvon.domain.auth.AuthUserProfile;
import com.macvon.domain.auth.Login;
import com.macvon.domain.email.Mail;
import com.macvon.domain.user.Membership;
import com.macvon.domain.user.Subscribe;
import com.macvon.domain.user.User;
import com.macvon.domain.user.UserMembership;
import com.macvon.domain.user.UserSubscribe;
import com.macvon.form.WebData;
import com.macvon.form.WebMessage;
import com.macvon.form.WebResponse;
import com.macvon.form.auth.ConfirmForgotPassword;
import com.macvon.form.auth.GetUserForm;
import com.macvon.form.auth.PwdResetReqForm;
import com.macvon.form.auth.SignupAuthForm;
import com.macvon.form.auth.SignupForm;
import com.macvon.form.auth.SignupMemberForm;
import com.macvon.form.auth.SubscribeForm;
import com.macvon.form.auth.UpdateGroupForm;
import com.macvon.form.auth.UpdatePwdForm;
import com.macvon.form.auth.UpdateTmpPwdForm;
import com.macvon.form.auth.UpdateUserAttributesForm;
import com.macvon.service.email.AppEmailService;
import com.macvon.utils.AppUtils;
import com.macvon.utils.DateUtils;
import com.macvon.utils.GlobalConstants;
import com.macvon.utils.RandomUtils;

/**
 * service to audit user action
 * 
 * @author brianwu
 *
 */
@Service
@Transactional
public class AuthService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SecurityApiService securityApiService;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private Environment env;

	public ResponseEntity<WebResponse> resetPasssword(PwdResetReqForm pwdResetReqForm) {
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("username", pwdResetReqForm.getUsername());
		try {
			String resetPasswordUrl = AppUtils.getAuthApiUrl("resetpwd", env);
			WebData res = securityApiService.callApiGateWay(paramMaps, resetPasswordUrl, WebData.class);
			if (res != null
					&& (res.getStatusCode() == 200 || res.getStatusCode() == 200 || res.getStatusCode() == 500)) {
				return ResponseEntity.ok(new WebResponse("Password has been reset successfully", true));
			}
		} catch (Exception ex) {
			logger.error("RESET PASSWORD AUTH FAILURE, ", ex);
		}
		return ResponseEntity.badRequest()
				.body(new WebResponse("user : " + pwdResetReqForm.getUsername() + " reset password error", false));
	}

	public ResponseEntity<WebResponse> confirmForgotPassword(ConfirmForgotPassword confirmForgotPassword) {
		if (!AppUtils.metPasswordRules(confirmForgotPassword.getPassword())) {
			return ResponseEntity.badRequest().body(new WebResponse("Password didn't meet the requirements", false));
		}
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("username", confirmForgotPassword.getUsername());
		paramMaps.put("password", confirmForgotPassword.getPassword());
		paramMaps.put("confirmationCode", confirmForgotPassword.getConfirmationCode());
		try {
			String confirmForgotPasswordUrl = AppUtils.getAuthApiUrl("confirmforgotpassword", env);
			WebData res = securityApiService.callApiGateWay(paramMaps, confirmForgotPasswordUrl, WebData.class);
			if (res != null
					&& (res.getStatusCode() == 200 || res.getStatusCode() == 200 || res.getStatusCode() == 500)) {
				return ResponseEntity.ok(new WebResponse("Password has been reset successfully", true));
			}
		} catch (Exception ex) {
			logger.error("AUTH FAILURE, ", ex);
		}
		return ResponseEntity.badRequest().body(new WebResponse("Not authorized to change the password", false));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<WebResponse> signup(SignupForm signupForm) {
		if (AppUtils.isValidPhoneFormat2(signupForm.getPhonenumber())) {
			String phonenumber = AppUtils.convertPhone(signupForm.getPhonenumber());
			signupForm.setPhonenumber(phonenumber);
		}
		boolean isValidPhone = AppUtils.isValidPhone(signupForm.getPhonenumber());
		if (!isValidPhone) {
			return ResponseEntity.badRequest().body(new WebResponse(
					" Phone number " + signupForm.getPhonenumber() + " invalid, expected format: +1xxxxxxxxxx", false));
		}
		boolean isValidPassword = AppUtils.metPasswordRules(signupForm.getPassword());
		if (!isValidPassword) {
			return ResponseEntity.badRequest().body(new WebResponse(
					" Password is invalid, At least 8 characters, A mixture of letters and numbers. Inclusion of at least one special character, e.g., ! @ # ? ] ",
					false));
		}
		SignupAuthForm signupAuthForm = SignupAuthForm.newInstance(signupForm);
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("username", signupAuthForm.getUsername());
		paramMaps.put("password", signupAuthForm.getPassword());
		paramMaps.put("firstname", signupAuthForm.getFirstname());
		paramMaps.put("lastname", signupAuthForm.getLastname());
		paramMaps.put("gender", signupAuthForm.getGender());
		paramMaps.put("email", signupAuthForm.getEmail());
		paramMaps.put("nickname", signupAuthForm.getNickname());
		paramMaps.put("phonenumber", signupAuthForm.getPhonenumber());
		paramMaps.put("scope", signupAuthForm.getScope());
		paramMaps.put("status", signupAuthForm.getStatus());
		paramMaps.put("effDate", signupAuthForm.getEffDate());
		paramMaps.put("expDate", signupAuthForm.getExpDate());
		paramMaps.put("notification", "true");

		try {
			Date createDate = DateUtils.getCurrentDateTS();
			Date futureDate = DateUtils.getFutureDateTs();
			Date currentEfftive = new Date();
			String phone = signupForm.getPhonenumber();
			User anyExistuser = (User) userDAO.findEntityObj(
					"from User where phoneNumber= ?1 or username =?2 or email=?3", phone, signupAuthForm.getUsername(),
					signupForm.getEmail());
			if (anyExistuser != null) {
				return ResponseEntity.badRequest().body(new WebResponse(
						"The phone number, username or email is already associated with existing account", false));
			}
			String signupUrl = AppUtils.getAuthApiUrl("signup", env);
			WebMessage res = securityApiService.callApiGateWay(paramMaps, signupUrl, WebMessage.class);
			logger.info("securityApiService.callApiGateWay status code: " + res.getStatusCode() + " message:"
					+ res.getMessage());
			if (res != null && (res.getStatusCode() == 200 || res.getStatusCode() == 0)) {
				User user = new User();
				long id = RandomUtils.getId();
				user.setId(id);
				user.setEmail(signupForm.getEmail());
				user.setFirstName(signupAuthForm.getFirstname());
				user.setLastName(signupAuthForm.getLastname());
				user.setStatus(GlobalConstants.STATUS.CREATED.name());
				user.setGender(signupAuthForm.getGender());
				user.setNickname(signupAuthForm.getNickname());
				user.setNotification("T");
				user.setUsername(signupAuthForm.getUsername());
				user.setEffectiveDate(currentEfftive);
				user.setExpirationDate(futureDate);
				user.setCreatedDate(createDate);
				user.setLastUpdateDate(createDate);
				user.setPhoneNumber(phone);
				userDAO.saveWithFlush(user);
				UserSubscribe userSubscribe = new UserSubscribe();
				userSubscribe.setEmail(signupForm.getEmail());
				userSubscribe.setStatus(GlobalConstants.STATUS.ACTIVED.name());
				userSubscribe.setUserId(id);
				userSubscribe.setCreatedDate(createDate);
				userDAO.save(userSubscribe);
				// send email
				String isEmailEnable = AppUtils.getConfigVal("email.enable", env);
				if ("true".equalsIgnoreCase(isEmailEnable)) {
					String from = env.getProperty(GlobalConstants.MAIL.MAIL_FROM);
					String subject = env.getProperty(GlobalConstants.MAIL.MAIL_WELCOME_SUBJECT);
					Mail mail = new Mail(from, signupForm.getEmail(), subject);
					mail.setTemplate(env.getProperty(GlobalConstants.MAIL.MAIL_NEW_USER_TEMPLATE));
					mail.setCc(env.getProperty(GlobalConstants.MAIL.MAIL_CC));
					mail.addModel("username", signupForm.getUsername());
					AppEmailService.INSTANCE.sendMessage(mail);
				}
				return ResponseEntity
						.ok(new WebResponse("User " + signupAuthForm.getUsername() + " signup successfully", true));
			} else {
				logger.error("can't create user in central repo for user: {} ", signupAuthForm.getUsername());
			}
		} catch (Exception ex) {
			logger.error("SIGNUP FAILURE, ", ex);
			throw new RuntimeException(ex);
		}
		return ResponseEntity.badRequest()
				.body(new WebResponse(" user " + signupAuthForm.getUsername() + " signup error", false));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<WebResponse> updateUserPassword(UpdatePwdForm updatePwdForm, String accessToken) {
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("accessToken", accessToken);
		paramMaps.put("previousPassword", updatePwdForm.getPreviousPassword());
		paramMaps.put("proposedPassword", updatePwdForm.getProposedPassword());
		try {
			String updatepasswordUrl = AppUtils.getApiUrl("updatepassword", env);
			WebData res = securityApiService.callApiGateWay(paramMaps, updatepasswordUrl, WebData.class);
			if (res != null
					&& (res.getStatusCode() == 200 || res.getStatusCode() == 200 || res.getStatusCode() == 500)) {
				return ResponseEntity.ok(new WebResponse("Password has been updated successfully", true));
			}
		} catch (Exception ex) {
			logger.error("Failed to update the Password, ", ex);
		}
		return ResponseEntity.badRequest().body(new WebResponse("Failed to update the Password", false));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public ResponseEntity<WebResponse> updateUserGroup(UpdateGroupForm updateGroupForm) {
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("newGroup", updateGroupForm.getNewGroup());
		paramMaps.put("username", updateGroupForm.getUsername());
		try {
			String updateusergroupUrl = AppUtils.getApiUrl("updateusergroup", env);
			WebData res = securityApiService.callApiGateWay(paramMaps, updateusergroupUrl, WebData.class);
			if (res != null && (res.getStatusCode() == 200 || res.getStatusCode() == 500)) {
				return ResponseEntity.ok(new WebResponse("UserGroup has been updated successfully", true));
			}
		} catch (Exception ex) {
			logger.error("Failed to change the UserGroup, ", ex);
		}
		return ResponseEntity.badRequest().body(new WebResponse("Failed to change the UserGroup", false));
	}

	public ResponseEntity<WebResponse> updateUserAttributes(UpdateUserAttributesForm updateUserAttributesForm) {
		logger.info("updateUserAttributes");
		try {
			String json = AppUtils.convertToJson(updateUserAttributesForm);
			String updateuserattributesUrl = AppUtils.getApiUrl("updateuserattributes", env);
			WebData res = securityApiService.callApiGateWay(json, updateuserattributesUrl, WebData.class);
			if (res != null && (res.getStatusCode() == 200 || res.getStatusCode() == 500)) {
				return ResponseEntity.ok(new WebResponse("UserAttributes has been updated successfully", true));
			}
		} catch (Exception ex) {
			logger.error("Macvon AUTH FAILURE, ", ex);
			throw new RuntimeException(ex);
		}
		return ResponseEntity.badRequest().body(new WebResponse("Failed to change the UserGroup", false));
	}

	public AuthUserProfile refreshtoken(String refreshToken) {
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("refreshToken", refreshToken);
		try {
			String refreshtokenUrl = AppUtils.getApiUrl("refreshtoken", env);
			AuthUserProfile authUserProfile = securityApiService.callApiGateWay(paramMaps, refreshtokenUrl,
					AuthUserProfile.class);
			if (authUserProfile != null
					&& (authUserProfile.getStatusCode() == 200 || authUserProfile.getStatusCode() == 500)) {
				return authUserProfile;
			}
		} catch (Exception ex) {
			logger.error("Failed to refresh token, ", ex);
		}
		throw new RuntimeException("can't refresh token");
	}

	public ResponseEntity<WebResponse> logout(String userName, String accessToken) {
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("accessToken", accessToken);
		try {
			String logoutUrl = AppUtils.getApiUrl("logout", env);
			WebData res = securityApiService.callApiGateWay(paramMaps, logoutUrl, WebData.class);
			if (res != null && (res.getStatusCode() == 200 || res.getStatusCode() == 500)) {
				saveLogoutSuccess(userName);
				return ResponseEntity.ok(new WebResponse("user logout successfully", true));
			}
		} catch (Exception ex) {
			logger.error("RESET PASSWORD AUTH FAILURE, ", ex);
		}
		return ResponseEntity.badRequest().body(new WebResponse("user : " + userName + " logout error", false));
	}

	public WebResponse getUser(GetUserForm getUserForm) {
		WebResponse res = new WebResponse();

		return res;
	}

	@Async
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveLogoutSuccess(String userName) {
		User user = (User) userDAO.findEntityObj("from User u where u.username= ?1 ", userName);
		if (user != null) {
			Login login = userDAO.findLatestActiveLogin(user.getId());
			login.setLogoutTime(new Date());
			userDAO.save(login);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public WebResponse signupPrimary(SignupForm signupForm) {
		if (AppUtils.isValidPhoneFormat2(signupForm.getPhonenumber())) {
			String phonenumber = AppUtils.convertPhone(signupForm.getPhonenumber());
			signupForm.setPhonenumber(phonenumber);
		}
		boolean isValidPhone = AppUtils.isValidPhone(signupForm.getPhonenumber());
		if (!isValidPhone) {
			return new WebResponse(
					" Phone number " + signupForm.getPhonenumber() + " invalid, expected format: +1xxxxxxxxxx", false);
		}
		boolean isValidPassword = AppUtils.metPasswordRules(signupForm.getPassword());
		if (!isValidPassword) {
			return new WebResponse(
					" Password is invalid, At least 8 characters, A mixture of letters and numbers. Inclusion of at least one special character, e.g., ! @ # ? ] ",
					false);
		}
		SignupAuthForm signupAuthForm = SignupAuthForm.newInstance(signupForm);
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("username", signupAuthForm.getUsername());
		paramMaps.put("password", signupAuthForm.getPassword());
		paramMaps.put("firstname", signupAuthForm.getFirstname());
		paramMaps.put("lastname", signupAuthForm.getLastname());
		paramMaps.put("gender", signupAuthForm.getGender());
		paramMaps.put("email", signupAuthForm.getEmail());
		paramMaps.put("nickname", signupAuthForm.getNickname());
		paramMaps.put("phonenumber", signupAuthForm.getPhonenumber());
		paramMaps.put("scope", signupAuthForm.getScope());
		paramMaps.put("status", signupAuthForm.getStatus());
		paramMaps.put("effDate", signupAuthForm.getEffDate());
		paramMaps.put("expDate", signupAuthForm.getExpDate());
		paramMaps.put("notification", "true");

		try {
			Date createDate = DateUtils.getCurrentDateTS();
			Date futureDate = DateUtils.getFutureDateTs();
			Date currentEfftive = new Date();
			String phone = signupForm.getPhonenumber();
			User anyExistuser = (User) userDAO.findEntityObj("from User where username =?1 or email=?2",
					signupForm.getUsername(), signupForm.getEmail());
			if (anyExistuser != null) {
				return new WebResponse("The username or email is already associated with existing account", false);
			}
			String signupUrl = AppUtils.getAuthApiUrl("signup", env);
			WebMessage res = securityApiService.callApiGateWay(paramMaps, signupUrl, WebMessage.class);
			logger.info("securityApiService.callApiGateWay status code: " + res.getStatusCode() + " message:"
					+ res.getMessage());
			if (res != null && (res.getStatusCode() == 200 || res.getStatusCode() == 0)) {
				User user = new User();
				long id = RandomUtils.getId();
				user.setId(id);
				user.setEmail(signupForm.getEmail());
				user.setFirstName(signupAuthForm.getFirstname());
				user.setLastName(signupAuthForm.getLastname());
				user.setStatus(GlobalConstants.STATUS.CREATED.name());
				user.setGender(signupAuthForm.getGender());
				user.setNickname(signupAuthForm.getNickname());
				user.setNotification("T");
				user.setUsername(signupAuthForm.getUsername());
				user.setEffectiveDate(currentEfftive);
				user.setExpirationDate(futureDate);
				user.setCreatedDate(createDate);
				user.setLastUpdateDate(createDate);
				user.setPhoneNumber(phone);
				userDAO.saveWithFlush(user);
				UserMembership userMembership = new UserMembership();
				userMembership.setIsPrimary("T");
				userMembership.setCreatedDate(createDate);
				userMembership.setLastUpdateDate(createDate);
				userMembership.setEffectiveDate(createDate);
				userMembership.setExpirationDate(futureDate);
				userMembership.setStatus(GlobalConstants.STATUS.PENDING.name());
				userMembership.setPrimaryUserId(id);
				userMembership.setUserId(id);
				Membership membership = (Membership) userDAO
						.findEntityObj("from Membership where membershipType= 'MS_BASIC'");
				userMembership.setMembershipId(membership.getId());
				userDAO.save(userMembership);
				UserSubscribe userSubscribe = new UserSubscribe();
				userSubscribe.setEmail(signupForm.getEmail());
				userSubscribe.setStatus(GlobalConstants.STATUS.ACTIVED.name());
				userSubscribe.setUserId(id);
				userSubscribe.setCreatedDate(createDate);
				userDAO.save(userSubscribe);
				return new WebResponse("User " + signupAuthForm.getUsername() + " signup successfully", true);
			}
			return new WebResponse(res != null ? res.getMessage() : "sign up error", false);
		} catch (Exception ex) {
			logger.error("SIGNUP FAILURE, ", ex);
			// throw new RuntimeException(ex);
			return new WebResponse(ex.getMessage(), false);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public WebResponse signupMember(SignupMemberForm signupForm) {
		if (AppUtils.isValidPhoneFormat2(signupForm.getPhonenumber())) {
			String phonenumber = AppUtils.convertPhone(signupForm.getPhonenumber());
			signupForm.setPhonenumber(phonenumber);
		}
		boolean isValidPhone = AppUtils.isValidPhone(signupForm.getPhonenumber());
		if (!isValidPhone) {
			return new WebResponse(
					" Phone number " + signupForm.getPhonenumber() + " invalid, expected format: +1xxxxxxxxxx", false);
		}
		boolean isValidPassword = AppUtils.metPasswordRules(signupForm.getPassword());
		if (!isValidPassword) {
			return new WebResponse(
					" Password is invalid, At least 8 characters, A mixture of letters and numbers. Inclusion of at least one special character, e.g., ! @ # ? ] ",
					false);
		}
		SignupAuthForm signupAuthForm = SignupAuthForm.newInstance(signupForm);
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("username", signupAuthForm.getUsername());
		paramMaps.put("password", signupAuthForm.getPassword());
		paramMaps.put("firstname", signupAuthForm.getFirstname());
		paramMaps.put("lastname", signupAuthForm.getLastname());
		paramMaps.put("gender", signupAuthForm.getGender());
		paramMaps.put("email", signupAuthForm.getEmail());
		paramMaps.put("nickname", signupAuthForm.getNickname());
		paramMaps.put("phonenumber", signupAuthForm.getPhonenumber());
		paramMaps.put("scope", signupAuthForm.getScope());
		paramMaps.put("status", signupAuthForm.getStatus());
		paramMaps.put("effDate", signupAuthForm.getEffDate());
		paramMaps.put("expDate", signupAuthForm.getExpDate());
		paramMaps.put("notification", "true");

		try {
			Date createDate = DateUtils.getCurrentDateTS();
			Date futureDate = DateUtils.getFutureDateTs();
			Date currentEfftive = new Date();
			String phone = signupForm.getPhonenumber();
			User anyExistuser = (User) userDAO.findEntityObj("from User where username =?1 or email=?2",
					signupForm.getUsername(), signupForm.getEmail());
			if (anyExistuser != null) {
				return new WebResponse("The username or email is already associated with existing account", false);
			}
			String signupUrl = AppUtils.getAuthApiUrl("signup", env);
			WebMessage res = securityApiService.callApiGateWay(paramMaps, signupUrl, WebMessage.class);
			logger.info("securityApiService.callApiGateWay status code: " + res.getStatusCode() + " message:"
					+ res.getMessage());
			if (res != null && (res.getStatusCode() == 200 || res.getStatusCode() == 0)) {
				User user = new User();
				long id = RandomUtils.getId();
				user.setId(id);
				user.setEmail(signupForm.getEmail());
				user.setFirstName(signupAuthForm.getFirstname());
				user.setLastName(signupAuthForm.getLastname());
				user.setStatus(GlobalConstants.STATUS.CREATED.name());
				user.setGender(signupAuthForm.getGender());
				user.setNickname(signupAuthForm.getNickname());
				user.setNotification("T");
				user.setUsername(signupAuthForm.getUsername());
				user.setEffectiveDate(currentEfftive);
				user.setExpirationDate(futureDate);
				user.setCreatedDate(createDate);
				user.setLastUpdateDate(createDate);
				user.setPhoneNumber(phone);
				userDAO.saveWithFlush(user);
				UserMembership userMembership = new UserMembership();
				userMembership.setIsPrimary("F");
				userMembership.setCreatedDate(createDate);
				userMembership.setLastUpdateDate(createDate);
				userMembership.setEffectiveDate(createDate);
				userMembership.setExpirationDate(futureDate);
				userMembership.setStatus("ACTIVE");
				userMembership.setPrimaryUserId(signupForm.getPrimaryUserId());
				userMembership.setUserId(id);
				Membership membership = (Membership) userDAO
						.findEntityObj("from Membership where membershipType= 'MS_HOUSE_HOLD'");
				userMembership.setMembershipId(membership.getId());
				userDAO.save(userMembership);
				UserSubscribe userSubscribe = new UserSubscribe();
				userSubscribe.setEmail(signupForm.getEmail());
				userSubscribe.setStatus(GlobalConstants.STATUS.ACTIVED.name());
				userSubscribe.setUserId(id);
				userSubscribe.setCreatedDate(createDate);
				userDAO.save(userSubscribe);
				return new WebResponse("User " + signupAuthForm.getUsername() + " signup successfully", true);
			}
			return new WebResponse(res != null ? res.getMessage() : "sign up error", false);
		} catch (Exception ex) {
			logger.error("SIGNUP FAILURE, ", ex);
			// throw new RuntimeException(ex);
			return new WebResponse(ex.getMessage(), false);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public WebResponse subscribe(SubscribeForm subscribeForm) {
		if (AppUtils.isValidEmailAddress(subscribeForm.getEmail())) {
			Subscribe subscribe = new Subscribe();
			Date createDate = DateUtils.getCurrentDate();
			subscribe.setEmail(subscribeForm.getEmail());
			subscribe.setCreatedDate(createDate);
			subscribe.setLastUpdateDate(createDate);
			userDAO.save(subscribe);
			return new WebResponse("submitted", true);
		} else {
			return new WebResponse("invalid email address", false);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public @ResponseBody WebResponse updateTmpPwd(UpdateTmpPwdForm updateTmpPwdForm) {
		String updateTmpPwdUrl = AppUtils.getAuthApiUrl("updatetmppwd", env);
		Map<String, String> paramMaps = Maps.newHashMap();
		paramMaps.put("username", updateTmpPwdForm.getUsername());
		paramMaps.put("password", updateTmpPwdForm.getPassword());
		paramMaps.put("proposedPassword", updateTmpPwdForm.getProposedPassword());
		try {
			WebData res = securityApiService.callApiGateWay(paramMaps, updateTmpPwdUrl, WebData.class);
			if (res != null
					&& (res.getStatusCode() == 200 || res.getStatusCode() == 200 || res.getStatusCode() == 500)) {
				return new WebResponse("Password has been updated successfully", true);
			}
		} catch (Exception ex) {
			logger.error("Failed to update the Password, ", ex);
		}
		return new WebResponse("Failed to update the Password", false);

	}

}
