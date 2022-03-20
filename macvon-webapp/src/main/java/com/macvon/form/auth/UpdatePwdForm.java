package com.macvon.form.auth;

import javax.validation.constraints.NotEmpty;

public class UpdatePwdForm {
	@NotEmpty
	private String previousPassword;
	@NotEmpty
	private String proposedPassword;
	public String getPreviousPassword() {
		return previousPassword;
	}
	public void setPreviousPassword(String previousPassword) {
		this.previousPassword = previousPassword;
	}
	public String getProposedPassword() {
		return proposedPassword;
	}
	public void setProposedPassword(String proposedPassword) {
		this.proposedPassword = proposedPassword;
	}

}