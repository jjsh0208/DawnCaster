package com.github.jjsh0208.dawncasterbackend.domain.email.entity;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.email.enums.SendStatus;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_send_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailSendHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "send_date", nullable = false)
    private LocalDate sendDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "send_status", nullable = false, length = 30)
    private SendStatus sendStatus;

    @Column(name = "fail_reason", length = 500)
    private String failReason;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Builder
    public EmailSendHistory(User user, LocalDate sendDate, SendStatus sendStatus, String failReason){
        this.user = user;
        this.sendDate = sendDate;
        this.sendStatus = sendStatus;
        this.failReason = failReason;
    }
}