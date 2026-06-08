package com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor
public class FinnhubNewsResponse {
    // 뉴스 기사의 제목 (예: "엔비디아, 새로운 AI 칩 발표")
    private String headline;

    // 뉴스 기사의 본문 요약 내용 (1~2줄 분량)
    private String summary;

    // 뉴스 원본 기사로 이동할 수 있는 URL 링크
    private String url;

    // 뉴스를 제공한 원본 출처 언론사 (예: "Yahoo", "Bloomberg")
    private String source;

    // 뉴스가 발행된 시각의 유닉스 타임스탬프 (초 단위, 1970년 1월 1일 기준)
    private long datetime;

    /**
     * 유닉스 타임스탬프(datetime)를 엔티티 저장에 용이한 LocalDateTime 객체로 변환합니다.
     * * @return 시스템 기본 시간대(예: Asia/Seoul)가 적용된 발행 일시
     */
    public LocalDateTime getPublishedAt() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(this.datetime), ZoneId.systemDefault());
    }
}
