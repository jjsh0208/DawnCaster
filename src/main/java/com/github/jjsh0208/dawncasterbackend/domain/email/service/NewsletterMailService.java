package com.github.jjsh0208.dawncasterbackend.domain.email.service;

import com.github.jjsh0208.dawncasterbackend.domain.email.entity.EmailSendHistory;
import com.github.jjsh0208.dawncasterbackend.domain.email.enums.SendStatus;
import com.github.jjsh0208.dawncasterbackend.domain.email.repository.EmailSendHistoryRepository;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsletterMailService {

    private final JavaMailSender javaMailSender;
    private final EmailSendHistoryRepository emailSendHistoryRepository;

    @Async("mailExecutor")
    public void sendAsync(User user, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true 설정으로 HTML 렌더링 활성화
            helper.setFrom("DawnCaster <jjsh0208@gmail.com>");

            javaMailSender.send(message);
            saveHistory(user, SendStatus.SUCCESS, null);
            log.debug("[Mail Success] 수신자: {}", user.getEmail());
        } catch (Exception e) {
            saveHistory(user, SendStatus.FAIL, e.getMessage());
            log.error("[Mail Failure] 수신자: {}, 원인: {}", user.getEmail(), e.getMessage());
        }
    }

    private void saveHistory(User user, SendStatus status, String failReason) {
        // 최대 길이 조절
        String safeReason = failReason != null && failReason.length() > 490
                ? failReason.substring(0, 490) : failReason;

        EmailSendHistory history = EmailSendHistory.builder()
                .user(user)
                .sendDate(LocalDate.now())
                .sendStatus(status)
                .failReason(safeReason)
                .build();
        emailSendHistoryRepository.save(history);
    }
}
