package org.eu.rainx0.raintool.core.starter.email;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * spring.mail.host=smtp.163.com #SMTP 服务器地址
 * spring.mail.username=your_username@163.com #发送者邮箱账号
 * spring.mail.password=your_password #发送者邮箱密码
 * spring.mail.port=25 #SMTP服务器端口号
 * spring.mail.protocol=smtp #邮件协议，默认为smtp
 * spring.mail.properties.mail.smtp.auth=true #SMTP 登录认证
 * spring.mail.properties.mail.smtp.starttls.enable=true #如果是使用加密类的邮件协议，需要设置此项为true
 *
 * xiaoyureed@gmail.com
 */
@Service
@Slf4j
public class EmailService {
    /**
     * 您的发件人邮箱地址
     */
    private String from = "";
    /**
     * 你的昵称
     */
    private String nickname = "";

    @Autowired
    private JavaMailSender mailSender;

    public void send(String to, String subject, String htmlContent, String attachmentPath) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        helper.setFrom(from, nickname);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // 附件
        if (StringUtils.hasText(attachmentPath)) {
            log.debug("!!! send email, adding attachment: {}", attachmentPath);
            helper.addAttachment("attachment", new FileSystemResource(new File(attachmentPath)));
        }

        mailSender.send(helper.getMimeMessage());
    }
}