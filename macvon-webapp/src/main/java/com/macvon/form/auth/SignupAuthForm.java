package com.macvon.form.auth;

import javax.validation.constraints.NotEmpty;

import com.macvon.utils.DateUtils;
import com.macvon.utils.GlobalConstants;

public class SignupAuthForm {
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
	@NotEmpty
	private String firstname;
	@NotEmpty
	private String lastname;
	@NotEmpty
	private String email;
	@NotEmpty
	private String gender;
	@NotEmpty
	private String nickname;
	@NotEmpty
	private String phonenumber;
	private String scope;
	private String status ;
	private String notification;
	private String effDate;
	private String expDate;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNotification() {
		return notification;
	}
	public void setNotification(String notification) {
		this.notification = notification;
	}
	public String getEffDate() {
		return effDate;
	}
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}
	public String getExpDate() {
		return expDate;
	}
	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}
	public static SignupAuthForm newInstance(SignupForm signupForm) {
		SignupAuthForm signupAuthForm = new SignupAuthForm();
		signupAuthForm.setFirstname(signupForm.getFirstname());
		signupAuthForm.setEffDate(DateUtils.getYYYYMMDDNow());
		signupAuthForm.setExpDate(DateUtils.getFutureYYYYMMDD());
		signupAuthForm.setEmail(signupForm.getEmail());
		if(signupForm.getGender().equalsIgnoreCase("male")) {
			signupAuthForm.setGender("M");
		} else if(signupForm.getGender().equalsIgnoreCase("female")) {
			signupAuthForm.setGender("F");
		} else {
			signupAuthForm.setGender(signupForm.getGender());
		}
		signupAuthForm.setLastname(signupForm.getLastname());
		signupAuthForm.setNickname(signupForm.getNickname());
		signupAuthForm.setNotification("true");
		signupAuthForm.setScope("guest");
		signupAuthForm.setPassword(signupForm.getPassword());
		signupAuthForm.setPhonenumber(signupForm.getPhonenumber());
		signupAuthForm.setStatus(GlobalConstants.STATUS.CREATED.name());
		signupAuthForm.setUsername(signupForm.getUsername());
		return signupAuthForm;

	}
	public static SignupAuthForm newInstance(SignupMemberForm signupForm) {
		SignupAuthForm signupAuthForm = new SignupAuthForm();
		signupAuthForm.setFirstname(signupForm.getFirstname());
		signupAuthForm.setEffDate(DateUtils.getYYYYMMDDNow());
		signupAuthForm.setExpDate(DateUtils.getFutureYYYYMMDD());
		signupAuthForm.setEmail(signupForm.getEmail());
		if(signupForm.getGender().equalsIgnoreCase("male")) {
			signupAuthForm.setGender("M");
		} else if(signupForm.getGender().equalsIgnoreCase("female")) {
			signupAuthForm.setGender("F");
		} else {
			signupAuthForm.setGender(signupForm.getGender());
		}
		signupAuthForm.setLastname(signupForm.getLastname());
		signupAuthForm.setNickname(signupForm.getNickname());
		signupAuthForm.setNotification("true");
		signupAuthForm.setScope("guest");
		signupAuthForm.setPassword(signupForm.getPassword());
		signupAuthForm.setPhonenumber(signupForm.getPhonenumber());
		signupAuthForm.setStatus(GlobalConstants.STATUS.CREATED.name());
		signupAuthForm.setUsername(signupForm.getUsername());
		return signupAuthForm;

	}
}
