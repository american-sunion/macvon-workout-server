package com.macvon.form;

public class WebResponse {
	protected String message;
	private boolean success = true;
	public WebResponse() {
	}
	public WebResponse(String message, boolean success) {
		this.message = message;
		this.success = success;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
