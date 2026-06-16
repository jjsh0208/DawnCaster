package com.github.jjsh0208.dawncasterbackend.domain.email.repository;

import com.github.jjsh0208.dawncasterbackend.domain.email.entity.EmailSendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSendHistoryRepository extends JpaRepository<EmailSendHistory, Long> {
}
