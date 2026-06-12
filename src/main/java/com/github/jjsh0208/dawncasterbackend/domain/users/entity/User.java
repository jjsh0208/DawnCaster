package com.github.jjsh0208.dawncasterbackend.domain.users.entity;

import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public User(String email) {
        this.email = email;
    }
}