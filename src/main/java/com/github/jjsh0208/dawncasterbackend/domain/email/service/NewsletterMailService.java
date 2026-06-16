package com.github.jjsh0208.dawncasterbackend.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterMailService {

    private final JavaMailSender javaMailSender;

    @Async("mailExecutor")
    public void sendAsync(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true 설정으로 HTML 렌더링 활성화
            helper.setFrom("DawnCaster <jjsh0208@gmail.com>");

            javaMailSender.send(message);
            log.debug("[Mail Success] 수신자: {}", toEmail);
        } catch (Exception e) {
            log.error("[Mail Failure] 수신자: {}, 원인: {}", toEmail, e.getMessage());
        }
    }

}
