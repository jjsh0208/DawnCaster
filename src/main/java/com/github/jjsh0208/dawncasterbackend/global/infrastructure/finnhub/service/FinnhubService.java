package com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.service;

import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.QuoteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinnhubService {

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



}
