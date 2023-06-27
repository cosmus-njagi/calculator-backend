package com.pycs.calculatorbackend.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author njagi
 * @Date 27/06/2023
 */
public class MailBody {
    String to;
    String subject;
    String text;
    MultipartFile attachment;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }
}
