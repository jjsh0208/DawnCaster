package com.github.jjsh0208.dawncasterbackend.domain.category.entity;

import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 한글 이름

    @Column(name = "en_name", nullable = false, length = 100)
    private String enName; // 내부 식별용 영어 이름

    @Column(name = "search_keywords", length = 500)
    private String searchKeywords; // 검색 카테고리

    public List<String> getSymbolList() {
        if (this.searchKeywords == null || this.searchKeywords.isBlank()) {
            return List.of();
        }
        return Arrays.stream(this.searchKeywords.split(","))
                .map(String::trim) // 공백 제거 방어 로직
                .collect(Collectors.toList());
    }
}