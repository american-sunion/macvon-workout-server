package com.macvon.form.auth;

import javax.validation.constraints.NotNull;

public class GetUserForm {
	@NotNull
	private String type;
	@NotNull
	private String value;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
