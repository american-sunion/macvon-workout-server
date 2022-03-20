package com.macvon.domain.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * auth profile will explore to WEB UI
 * @author brian
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthUserProfile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6539090712543109938L;
	private int statusCode;
	private int expiresIn;
	private String accessToken;
	private String idToken;
	private String tokenType;
	private String refreshToken;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public int getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
}

