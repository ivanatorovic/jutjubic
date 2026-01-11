package com.example.jutjubic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.mail.MailException;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendActivationEmail(String to, String activationLink) throws MailException {

        System.out.println(
                "Async email thread id: " + Thread.currentThread().getId()
        );

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("Aktivacija naloga - Jutjubic");
        msg.setText(
                "Zdravo!\n\n" +
                        "Da aktiviraš nalog, klikni na link:\n" +
                        activationLink + "\n\n" +
                        "Ako nisi ti pravio/la nalog, ignoriši ovaj email."
        );

        mailSender.send(msg);

        System.out.println("Aktivacioni email poslat!");
    }

}
