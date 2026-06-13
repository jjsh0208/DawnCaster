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

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("구독할 카테고리를 최소 1개 이상 선택해야 합니다.");
        }

        List<Category> validCategories = categoryRepository.findAllById(categoryIds);
        if (validCategories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("요청한 카테고리 중 존재하지 않는 카테고리가 포함되어 있습니다.");
        }

        // 1. 사용자 조회 또는 생성
        User user = userService.findOrCreateUser(email);
        // 2. 이미 구독 중인 카테고리 ID 목록을 DB에서 한 번에 조회 (단 1번의 SELECT)
        List<Long> existingCategoryIds = subscriptionRepository.findExistingCategoryIds(user, categoryIds);
        // 3. 새로 저장할 구독 엔티티를 모을 리스트 생성
        List<Subscription> newSubscriptions = new ArrayList<>();

        // 4. 반복문에서는 DB 조회 없이 메모리에서 중복 필터링
        for (Category category : validCategories) {
            if (!existingCategoryIds.contains(category.getId())) {

                // 3. 엔티티 생성
                Subscription newSubscription = Subscription.builder()
                        .user(user)
                        .category(category)
                        .isActive(true)
                        .build();

                user.addSubscription(newSubscription);
                newSubscriptions.add(newSubscription);
            }
        }

        // 5. 모아둔 엔티티들을 한 번에 저장 (saveAll)
        if (!newSubscriptions.isEmpty()) {
            subscriptionRepository.saveAll(newSubscriptions);
        }
    }
}