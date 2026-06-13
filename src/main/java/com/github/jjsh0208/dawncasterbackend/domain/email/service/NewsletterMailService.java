package com.github.jjsh0208.dawncasterbackend.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterMailService {

    private final JavaMailSender javaMailSender;

    public void sendNewsletter(String toEmail, String subject, String htmlContent) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(toEmail); // 전달 받을 이메일
            helper.setSubject(subject); // 메일 제목
            helper.setText(htmlContent, true); // 첨부 내용 , html
            helper.setFrom("DawnCaster <jjsh0208@gmail.com>");


            javaMailSender.send(mimeMessage);
            log.info("뉴스레터 전송 성공: {}", toEmail);

        } catch (MessagingException e) {
            log.error("뉴스레터 전송 실패: 수신자={}, 원인={}", toEmail, e.getMessage());
            // 필요한 경우 커스텀 예외로 전환하여 재시도(Retry) 큐로 전송하는 로직 추가
            throw new RuntimeException("메일 전송 중 오류가 발생했습니다.", e);
        }
    }

}
