package com.macvon.form.auth;

import javax.validation.constraints.NotEmpty;

public class ConfirmForgotPassword {
	@NotEmpty
	private String confirmationCode;
	@NotEmpty
	private String password;
	@NotEmpty
	private String username;
	public String getConfirmationCode() {
		return confirmationCode;
	}
	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
