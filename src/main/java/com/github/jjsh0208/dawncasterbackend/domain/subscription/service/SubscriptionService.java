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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final CategoryRepository categoryRepository; // 필요 시 CategoryService로 대체

    // 1. 메인 파이프라인 (흐름 제어)
    @Transactional
    public void subscribeCategories(String email, List<Long> categoryIds) {

        // 유효성 검증
        List<Category> validCategories = validateCategoryIds(categoryIds);

        // 사용자 조회 또는 생성
        User user = userService.findOrCreateUser(email);

        // 이미 구독 중인 카테고리 ID 목록 조회
        List<Long> existingCategoryIds = subscriptionRepository.findExistingCategoryIds(user, categoryIds);

        // 신규 구독 객체 필터링 및 생성
        List<Subscription> newSubscriptions = filterNewSubscriptions(user, validCategories, existingCategoryIds);

        // DB 일괄 저장
        saveSubscriptions(newSubscriptions);
    }

    // 2. 사용자가 전달한 카테고리 유효성 검사
    private List<Category> validateCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("구독할 카테고리를 최소 1개 이상 선택해야 합니다.");
        }

        List<Category> validCategories = categoryRepository.findAllById(categoryIds);
        if (validCategories.size() != categoryIds.size()) {
            throw new IllegalArgumentException("요청한 카테고리 중 존재하지 않는 카테고리가 포함되어 있습니다.");
        }

        return validCategories;
    }

    // 3. 신규 구독 필터링 및 엔티티 생성 순수 가공 로직
    private List<Subscription> filterNewSubscriptions(User user, List<Category> validCategories, List<Long> existingCategoryIds) {
        List<Subscription> newSubscriptions = new ArrayList<>();

        // 빠른 탐색을 위해 List를 Set으로 변환 ( O(1) 이라서 )
        Set<Long> existingCategoryIdSet = new HashSet<>(existingCategoryIds);

        for (Category category : validCategories) {
            if (!existingCategoryIdSet.contains(category.getId())) {
                Subscription newSubscription = Subscription.builder()
                        .user(user)
                        .category(category)
                        .isActive(true)
                        .build();

                user.addSubscription(newSubscription);
                newSubscriptions.add(newSubscription);
            }
        }

        return newSubscriptions;
    }

    // 4. 저장 위임 로직
    private void saveSubscriptions(List<Subscription> newSubscriptions) {
        if (!newSubscriptions.isEmpty()) {
            subscriptionRepository.saveAll(newSubscriptions);
        }
    }
}