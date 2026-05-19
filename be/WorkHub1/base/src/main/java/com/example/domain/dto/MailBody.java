package com.example.domain.dto;

public class MailBody {

    public String to;
    public String subject;
    public String content;
    public boolean isHtml;

    public MailBody() {}

    public MailBody(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.isHtml = true;
    }

    public MailBody(String to, String subject, String content, boolean isHtml) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.isHtml = isHtml;
    }
}
