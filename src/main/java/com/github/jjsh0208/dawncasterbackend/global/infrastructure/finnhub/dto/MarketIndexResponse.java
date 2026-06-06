package com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto;

public record MarketIndexResponse(
        String name,
        QuoteResponse quote
) {
}