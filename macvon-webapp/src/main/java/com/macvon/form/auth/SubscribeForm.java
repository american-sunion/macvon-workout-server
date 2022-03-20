package com.macvon.form.auth;

import javax.validation.constraints.NotEmpty;

public class SubscribeForm {
	@NotEmpty
	public String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
