package com.macvon.domain.email;

import java.util.HashMap;
import java.util.Map;

/**
 * mail domain for email information.
 * 
 * @author xunwu
 *
 */
public class Mail {
	private String from;
	private String to;
	private String cc;
	private String subject;
	private String template;
	private Map<String, Object> model = new HashMap<>();
	private Map<String, String> attachments = new HashMap<String, String>();

	public Mail() {
	}

	public Mail(String from, String to, String subject) {
		this.from = from;
		this.to = to;
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void addModel(String key, Object value) {
		this.model.put(key, value);
	}

	public void addAllModel(Map<String, String> model) {
		this.model.putAll(model);
	}

	public Map<String, String> getAttachments() {
		return attachments;
	}

	public void addAttachment(String key, String value) {
		this.attachments.put(key, value);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return "Mail [from=" + from + ", to=" + to + ", subject=" + subject + ", getTo()=" + getTo() + ", getSubject()="
				+ getSubject() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
