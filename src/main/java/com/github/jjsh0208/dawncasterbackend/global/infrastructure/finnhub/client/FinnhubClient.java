package com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.client;

import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.FinnhubNewsResponse;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.QuoteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinnhubClient {

    private final RestClient finnhubRestClient;

    @Value("${finnhub.api-key}")
    private String apiKey;

    // 미국 3대 증시 정보
    public QuoteResponse getQuote(String symbol) {

        try {
            return finnhubRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/quote")
                            .queryParam("symbol", symbol)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .body(QuoteResponse.class);
        } catch (RestClientException e) {
            log.error("[Finnhub API Error] 주가 조회 실패 (심볼: {}): {}", symbol, e.getMessage());
            return null;
        }
    }

    // 2. 특정 심볼의 뉴스 조회 (카테고리 매핑용)
    public List<FinnhubNewsResponse> getCompanyNews(String symbol, LocalDate from, LocalDate to) {
        try {
            FinnhubNewsResponse[] response = finnhubRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/company-news")
                            .queryParam("symbol", symbol)
                            .queryParam("from", from.toString())
                            .queryParam("to", to.toString())
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .body(FinnhubNewsResponse[].class);

            // null safe 처리 후 리스트로 반환
            return response != null ? Arrays.asList(response) : Collections.emptyList();

        } catch (RestClientException e) {
            log.error("[Finnhub API Error] 뉴스 조회 실패 (심볼: {}): {}", symbol, e.getMessage());
            return Collections.emptyList();
        }
    }


}
