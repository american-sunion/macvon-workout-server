package com.macvon.form.auth;

public class AttrJson {
	
	private Object name;
	private Object value;
	public Object getName() {
		return name;
	}
	//@JsonProperty("Name")
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	//@JsonProperty("Value")
	public void setValue(Object value) {
		this.value = value;
	}
}
