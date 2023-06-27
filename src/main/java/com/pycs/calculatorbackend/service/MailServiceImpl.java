package com.pycs.calculatorbackend.service;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.ForkJoinPool;
/**
 * @author njagi
 * @Date 26/06/2023
 */

@Service
public class MailServiceImpl {
    private final JavaMailSender mailSender;
    private final Environment environment;

    public MailServiceImpl(JavaMailSender mailSender, Environment environment) {
        this.mailSender = mailSender;
        this.environment = environment;
    }

    public void sendMail(String to, String subject, String text, MultipartFile attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(environment.getProperty("spring.mail.username", "dev.cosmus@gmail.com"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            if (attachment != null && !attachment.isEmpty()) {
                helper.addAttachment(attachment.getOriginalFilename(), new ByteArrayResource(attachment.getBytes()));
            }

            ForkJoinPool.commonPool().execute(() -> {
                try {
                    mailSender.send(message);
                    System.out.println("Mail sent successfully ...");
                } catch (Exception e) {
                    System.out.println("Error sending email: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.out.println("Error executing email sending task: " + e.getMessage());
            e.printStackTrace();
        }
    }

}