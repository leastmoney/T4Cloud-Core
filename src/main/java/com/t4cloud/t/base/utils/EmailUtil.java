package com.t4cloud.t.base.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;

@Component
public class EmailUtil {

    public static String from;
    @Autowired
    private Environment env;

    public static void SendMsg(String receiver, String title, String content) throws Exception {
        JavaMailSender mailSender = (JavaMailSender) SpringContextUtil.getBean("mailSender");
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = null;
        //解决乱码问题
        message = new MimeMessageHelper(mimeMessage, true, "GBK");
        // 设置发送方邮箱地址
        message.setFrom(from);
        message.setTo(receiver);
        message.setSubject(title);
        message.setText(content, true);
        mailSender.send(mimeMessage);
    }

    @PostConstruct
    public void readConfig() {
        from = env.getProperty("spring.mail.nickname");
    }
}
