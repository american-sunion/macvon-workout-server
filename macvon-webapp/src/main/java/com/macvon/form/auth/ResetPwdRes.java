package com.macvon.form.auth;

public class ResetPwdRes {
	private String phoneCode;
	private String ptoken;
	private boolean isSuccess;

	public String getPhoneCode() {
		return phoneCode;
	}
	public void setPhoneCode(String phoneCode) {
		this.phoneCode = phoneCode;
	}
	public String getPtoken() {
		return ptoken;
	}
	public void setPtoken(String ptoken) {
		this.ptoken = ptoken;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
}
