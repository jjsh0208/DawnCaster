package com.github.jjsh0208.dawncasterbackend.domain.subscription.repository;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.subscription.entity.Subscription;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // 회원이 이미 구독 중인 카테고리 ID 목록을 한 번에 조회
    @Query("SELECT s.category.id FROM Subscription s WHERE s.user = :user AND s.category.id IN :categoryIds")
    List<Long> findExistingCategoryIds(@Param("user") User user, @Param("categoryIds") List<Long> categoryIds);
}
