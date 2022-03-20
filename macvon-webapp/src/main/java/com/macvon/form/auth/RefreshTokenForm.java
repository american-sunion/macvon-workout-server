package com.macvon.form.auth;

import javax.validation.constraints.NotEmpty;

public class RefreshTokenForm {
	@NotEmpty
	public String refreshToken;
	@NotEmpty
	public String userName;
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
