package com.github.jjsh0208.dawncasterbackend.domain.subscription.service;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.category.repository.CategoryRepository;
import com.github.jjsh0208.dawncasterbackend.domain.subscription.entity.Subscription;
import com.github.jjsh0208.dawncasterbackend.domain.subscription.repository.SubscriptionRepository;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import com.github.jjsh0208.dawncasterbackend.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final CategoryRepository categoryRepository; // 필요 시 CategoryService로 대체

    @Transactional
    public void subscribeCategories(String email, List<Long> categoryIds) {
        // 1. 사용자 조회 또는 생성
        User user = userService.findOrCreateUser(email);

        // 2. 카테고리 ID 목록을 기반으로 구독 정보 생성
        for (Long categoryId : categoryIds) {
            // 엔티티 조회를 생략하고 프록시 객체만 가져와 쿼리 최적화 (JPA getReferenceById)
            Category categoryRef = categoryRepository.getReferenceById(categoryId);

            // 중복 구독 방지 로직 (예: 이미 해당 user와 category로 활성화된 구독이 있는지 확인)
            boolean alreadySubscribed = subscriptionRepository.existsByUserAndCategory(user, categoryRef);

            if (!alreadySubscribed) {
                Subscription subscription = Subscription.builder()
                        .user(user)
                        .category(categoryRef)
                        .isActive(true)
                        .build();

                subscriptionRepository.save(subscription);
            }
        }
    }
}