package com.macvon.form.auth;

import java.util.ArrayList;
import java.util.List;

public class UpdateUserAttributesForm {
	private String username;
	private List<Attr> attrs = new ArrayList<>();
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<Attr> getAttrs() {
		return attrs;
	}
	public void setAttrs(List<Attr> attrs) {
		this.attrs = attrs;
	}
	
}