package com.github.jjsh0208.dawncasterbackend.domain.stock.service;

import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.MarketIndexResponse;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.client.FinnhubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final FinnhubClient finnhubClient;

    /** 3대 증시 심볼
     *  SPY , QQQ , DIA
     */
    public List<MarketIndexResponse> getMajorMarkets() {
        return List.of(
                new MarketIndexResponse(
                        "S&P 500",
                        finnhubClient.getQuote("SPY")
                ),
                new MarketIndexResponse(
                        "NASDAQ",
                        finnhubClient.getQuote("QQQ")
                ),
                new MarketIndexResponse(
                        "DOW JONES",
                        finnhubClient.getQuote("DIA")
                )
        );
    }
}
