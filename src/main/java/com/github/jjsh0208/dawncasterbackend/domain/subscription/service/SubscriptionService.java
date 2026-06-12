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

import java.util.ArrayList;
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

        // 2. 이미 구독 중인 카테고리 ID 목록을 DB에서 한 번에 조회 (단 1번의 SELECT)
        List<Long> existingCategoryIds = subscriptionRepository.findExistingCategoryIds(user, categoryIds);

        // 3. 새로 저장할 구독 엔티티를 모을 리스트 생성
        List<Subscription> newSubscriptions = new ArrayList<>();

        // 4. 반복문에서는 DB 조회 없이 메모리에서 중복 필터링
        for (Long categoryId : categoryIds) {
            if (!existingCategoryIds.contains(categoryId)) {
                Category categoryRef = categoryRepository.getReferenceById(categoryId);

                newSubscriptions.add(Subscription.builder()
                        .user(user)
                        .category(categoryRef)
                        .isActive(true)
                        .build());
            }
        }

        // 5. 모아둔 엔티티들을 한 번에 저장 (saveAll)
        if (!newSubscriptions.isEmpty()) {
            subscriptionRepository.saveAll(newSubscriptions);
        }
    }
}