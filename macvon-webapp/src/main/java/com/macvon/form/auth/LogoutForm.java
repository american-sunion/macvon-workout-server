package com.macvon.form.auth;

import javax.validation.constraints.NotEmpty;

public class LogoutForm {
	@NotEmpty
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
