package com.github.jjsh0208.dawncasterbackend.domain.subscription.repository;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.subscription.entity.Subscription;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByUserAndCategory(User user, Category category);
}
