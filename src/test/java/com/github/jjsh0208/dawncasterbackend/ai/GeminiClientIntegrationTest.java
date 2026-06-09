package com.github.jjsh0208.dawncasterbackend.ai;

import com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.client.GeminiClient;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.dto.AiAnalysisResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class GeminiClientIntegrationTest {

    @Autowired
    private GeminiClient geminiClient;

    @Test
    @DisplayName("실제 Gemini API 연동 및 3줄 요약 응답 확인")
    void callRealGeminiApi() {
        // Given
        String question = "엔비디아의 1분기 매출이 시장 예상치를 20% 상회하며 어닝 서프라이즈를 기록했습니다. " +
                "이에 따라 TSMC, SK하이닉스 등 관련 밸류체인 기업들의 주가도 시간외 거래에서 급등 중입니다.";

        // When
        System.out.println("API 요청을 전송합니다. (약 2~5초 소요 예상)...");
        long startTime = System.currentTimeMillis();

        // 작성하신 test() 메서드 실제 호출
        AiAnalysisResultDto actualAnswer = geminiClient.summarizeSectorNews(question);

        long endTime = System.currentTimeMillis();

        // Then
        System.out.println("\n=== [실제 Gemini API 응답 결과] ===");
        System.out.println(actualAnswer.toString());
        System.out.println("===================================");
        System.out.println("소요 시간: " + (endTime - startTime) + "ms\n");

        // 응답이 정상적으로 돌아왔는지 최소한의 검증
        assertThat(actualAnswer);
    }
}