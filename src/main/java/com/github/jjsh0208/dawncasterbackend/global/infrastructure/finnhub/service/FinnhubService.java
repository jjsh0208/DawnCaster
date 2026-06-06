package com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.service;

import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.QuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class FinnhubService {

    private final RestClient finnhubRestClient;


    @Value("${finnhub.api-key}")
    private String apiKey;

    public QuoteResponse getQuote(String symbol) {

        return finnhubRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .body(QuoteResponse.class);
    }
}
