package com.github.jjsh0208.dawncasterbackend.mail;

import com.github.jjsh0208.dawncasterbackend.domain.email.service.NewsletterMailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NewsletterMailServiceTest {

    @Autowired
    private NewsletterMailService newsletterMailService;

    @Test
    @DisplayName("HTML 형식 뉴스레터 전송 테스트")
    void testSendNewsletter() {
        // Given
        String toEmail = "jjssh0208@naver.com"; // 본인의 다른 이메일 입력
        String subject = "[DawnCaster] 테스트 이메일 발송";

        String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                    <body><p>테스트 메시지 전달 </p></body>
                </html>
                """;

        // When & Then
        newsletterMailService.sendNewsletter(toEmail, subject, htmlContent);
    }
}