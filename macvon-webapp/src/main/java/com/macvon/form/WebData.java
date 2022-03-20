package com.macvon.form;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * web data return to UI
 * @author brian
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebData implements Serializable {

	private static final long serialVersionUID = 6840642282981468240L;
	private int statusCode;
	private Object data;
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
