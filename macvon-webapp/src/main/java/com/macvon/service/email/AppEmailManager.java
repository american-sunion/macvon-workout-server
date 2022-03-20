package com.macvon.service.email;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.macvon.domain.email.Mail;

@Service
public class AppEmailManager {
    @Autowired
    private EmailService emailService;
    @Async
    public void sendMessage(Mail mail) {
    	emailService.sendMessage(mail);
    }
}
