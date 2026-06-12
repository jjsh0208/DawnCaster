package com.github.jjsh0208.dawncasterbackend.domain.subscription.entity;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.domain.users.entity.User;
import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Builder
    public Subscription(User user, Category category, boolean isActive){
        this.user = user;
        this.category =category;
        this.isActive = isActive;
    }
}