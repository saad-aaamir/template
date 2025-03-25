package com.application.config.mail;

import lombok.Getter;

@Getter
public enum EmailTemplate {

    ACTIVATE_USER(
            "App User Activation",
            "<html>" +
                    "<body>" +
                    "<p>Dear User,</p>" +
                    "<p>Please click the button below to activate your account:</p>" +
                    "<a href='%s' style='display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #007bff; text-decoration: none; border-radius: 5px;'>Activate Account</a>" +
                    "<p>If you did not request this activation, please ignore this email.</p>" +
                    "<p>Thank you!</p>" +
                    "</body>" +
                    "</html>"
    );

    private final String name;
    private final String bodyTemplate;

    EmailTemplate(String name, String bodyTemplate) {
        this.name = name;
        this.bodyTemplate = bodyTemplate;
    }
}
