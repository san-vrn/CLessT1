package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.aspect.LogThrowing;
import org.example.exception.email.EmailServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailService {

    private JavaMailSender emailSender;
    @Value("${email_to}")
    private String emailTo;
    @Value("${mail_username}")
    private String userFrom;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @LogThrowing
    public void sendSimpleMessage(String messageBody, String messageSubject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(userFrom);
            message.setTo(emailTo);
            message.setSubject(messageSubject);
            message.setText(messageBody);
            emailSender.send(message);
            logger.info("Email sent successfully :{}", message);
        } catch (MailSendException e) {
            throw new MailSendException("SMTP error while sending email", e);
        } catch (Exception e) {
            throw new EmailServiceException("Unexpected error while sending email", e);
        }
    }
}