package com.macvon.service.email;

import org.springframework.beans.factory.annotation.Autowired;

import com.macvon.domain.email.Mail;

public enum AppEmailService {
	INSTANCE;
	@Autowired
	private AppEmailManager appEmailManager;
	
    public void sendMessage(Mail mail) {
    	appEmailManager.sendMessage(mail);
    }

}