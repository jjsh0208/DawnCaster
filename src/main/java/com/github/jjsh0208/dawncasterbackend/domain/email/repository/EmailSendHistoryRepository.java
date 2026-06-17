package com.github.jjsh0208.dawncasterbackend.domain.email.repository;

import com.github.jjsh0208.dawncasterbackend.domain.email.entity.EmailSendHistory;
import com.github.jjsh0208.dawncasterbackend.domain.email.enums.SendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmailSendHistoryRepository extends JpaRepository<EmailSendHistory, Long> {

    List<EmailSendHistory> findBySendStatusAndSendDate(SendStatus sendStatus, LocalDate sendDate);
}
