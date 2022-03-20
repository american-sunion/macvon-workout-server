package com.macvon.form;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * web message return to UI
 * @author brian
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebMessage  implements Serializable{

	private static final long serialVersionUID = 6878142281651468240L;
	private int statusCode;
	private String message;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}