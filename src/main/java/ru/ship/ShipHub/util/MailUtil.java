package ru.ship.ShipHub.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

    private final JavaMailSender mailSender;
    private final String mailAddress;
    private final Logger log;

    public MailUtil(JavaMailSender mailSender, @Value("mail.address") String mailAddress) {
        this.mailSender = mailSender;
        this.mailAddress = mailAddress;
        this.log = LoggerFactory.getLogger(MailUtil.class);
    }

    public void sendMessage(String receiver, String sender, String text){
        log.info("Mail send");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailAddress);
        message.setTo(receiver);
//        message.setSubject(sender);
        message.setText(text);
        mailSender.send(message);
    }
}
