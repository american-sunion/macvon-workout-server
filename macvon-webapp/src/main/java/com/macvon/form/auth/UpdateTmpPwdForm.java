package com.macvon.form.auth;

public class UpdateTmpPwdForm {
	private String username;
	private String password;
	private String proposedPassword;
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
	public String getProposedPassword() {
		return proposedPassword;
	}
	public void setProposedPassword(String proposedPassword) {
		this.proposedPassword = proposedPassword;
	}
	
}
