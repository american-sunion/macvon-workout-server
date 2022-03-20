package com.macvon.handler.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.macvon.domain.auth.AuthUser;
import com.macvon.domain.auth.AuthUserProfile;
import com.macvon.domain.auth.BasicUserInfo;
import com.macvon.service.auth.SecurityApiService;
import com.macvon.utils.TokenUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

/**
 * provider connect to micro-auth service to verify auth info (via cognito)
 * 
 * @author brian
 *
 */
@SuppressWarnings("rawtypes")
public class LoginAuthServiceProvider extends AbstractUserDetailsAuthenticationProvider {

	private final String authUrl;
	private UserDetailsService userDetailsService;
	private String charToDel = "\"[]";
	private String pat = "[" + Pattern.quote(charToDel) + "]";
	private SecurityApiService securityApiService;
	private ConfigurableJWTProcessor configurableJWTProcessor;

	public LoginAuthServiceProvider(SecurityApiService securityApiService, String authUrl,
			ConfigurableJWTProcessor configurableJWTProcessor) {
		this.securityApiService = securityApiService;
		this.authUrl = authUrl;
		this.configurableJWTProcessor = configurableJWTProcessor;
	}

	@Override
	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		try {
			securityApiService.checkIfAccountLocked(username);
			final String password = authentication.getCredentials().toString();
			Map<String, String> paramMaps = Maps.newHashMap();
			paramMaps.put("username", username);
			paramMaps.put("password", password);

			AuthUserProfile authUserProfile = securityApiService.callApiGateWay(paramMaps, this.authUrl, AuthUserProfile.class);
			if (authUserProfile != null && authUserProfile.getStatusCode() == 200) {
				BasicUserInfo userInfo = new BasicUserInfo();
				List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<SimpleGrantedAuthority>();
				JWTClaimsSet claimsSet = TokenUtils.getJWTClaimsSet(authUserProfile.getIdToken(),
						configurableJWTProcessor);
				final Object statusValue = TokenUtils.getClaim(claimsSet, "custom:status");
				if (statusValue != null) {
					userInfo.setStatus(statusValue.toString());
				}
				if(statusValue==null || userInfo.getStatus().equalsIgnoreCase("EXPIRED") || userInfo.getStatus().equalsIgnoreCase("INACTIVE")) {
					throw new BadCredentialsException(
							messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "User account expired"));
				}
				final Object rolesValue = TokenUtils.getClaim(claimsSet, "cognito:groups");
				if (rolesValue != null) {
					String[] roles = rolesValue.toString().split(",");
					if (roles != null) {
						for (String rolestr : roles) {
							int index = rolestr.indexOf("/");
							String role = (rolestr.substring(index + 1, rolestr.length() - 1)).replaceAll(pat, "")
									.replace("ROLE_", "");
							SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
							updatedAuthorities.add(authority);
							userInfo.addRole(role);
						}
					}

				}
				final Object unameValue = TokenUtils.getClaim(claimsSet, "cognito:username");
				if (unameValue != null) {
					String userName = unameValue.toString();
					userInfo.setUserName(userName);
				}
				final Object lnValue = TokenUtils.getClaim(claimsSet, "family_name");
				if (lnValue != null) {
					userInfo.setLastName(lnValue.toString());
				}
				final Object fnValue = TokenUtils.getClaim(claimsSet, "given_name");
				if (fnValue != null) {
					userInfo.setFirstName(fnValue.toString());
				}
				final Object phoneValue = TokenUtils.getClaim(claimsSet, "phone_number");
				if (phoneValue != null) {
					userInfo.setPhoneNumber(phoneValue.toString());
				}
				final Object emailValue = TokenUtils.getClaim(claimsSet, "email");
				if (emailValue != null) {
					userInfo.setEmail(emailValue.toString());
				}
				final Object nickerNameValue = TokenUtils.getClaim(claimsSet, "nickname");
				if (nickerNameValue != null) {
					userInfo.setNickname(nickerNameValue.toString());
				}
				securityApiService.saveLoginSuccess(username);
				return new AuthUser(username, updatedAuthorities, authUserProfile, userInfo);
			}
		} catch (Exception ex) {
			logger.error("Authentication failed: " + ex.getMessage());
		}
		securityApiService.handleLoginFailure(username);
		throw new BadCredentialsException(
				messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

	}

}