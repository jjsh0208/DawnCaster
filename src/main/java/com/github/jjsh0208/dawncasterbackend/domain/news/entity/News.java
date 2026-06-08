package com.github.jjsh0208.dawncasterbackend.domain.news.entity;

import com.github.jjsh0208.dawncasterbackend.domain.category.entity.Category;
import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "news", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_news_category_url",
                columnNames = {"category_id", "url"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String url;

    @Column(length = 100)
    private String source;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;


    @Builder
    public News(Category category, String title, String content, String url, String source, LocalDateTime publishedAt) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.url = url;
        this.source = source;
        this.publishedAt = publishedAt;
    }
}