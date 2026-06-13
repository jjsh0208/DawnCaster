package com.github.jjsh0208.dawncasterbackend.domain.users.entity;

import com.github.jjsh0208.dawncasterbackend.domain.subscription.entity.Subscription;
import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "user")
    private List<Subscription> subscriptions = new ArrayList<>();

    @Builder
    public User(String email) {
        this.email = email;
    }

    public void addSubscription(Subscription subscription) {
        this.subscriptions.add(subscription);
    }

    // 비즈니스 헬퍼 메서드: 활성화된 구독의 카테고리 ID 목록 추출
    public List<Long> getSubscribedCategoryIds() {
        return this.subscriptions.stream()
                .filter(Subscription::isActive)
                .map(sub -> sub.getCategory().getId())
                .toList();
    }
}