package com.application.config.mail;

import org.springframework.stereotype.Component;

@Component
public interface EmailService {

    void sendMail(EmailDetails emailDetails);
    void sendMailWithAttachments(EmailDetails emailDetails);

}
