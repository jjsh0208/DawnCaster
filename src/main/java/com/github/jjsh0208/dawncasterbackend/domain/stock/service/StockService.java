package com.github.jjsh0208.dawncasterbackend.domain.stock.service;

import com.github.jjsh0208.dawncasterbackend.domain.stock.entity.StockMarketSummary;
import com.github.jjsh0208.dawncasterbackend.domain.stock.repository.StockRepository;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.MarketIndexResponse;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.client.FinnhubClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final FinnhubClient finnhubClient;
    private final StockRepository stockRepository;

    /** 3대 증시 심볼
     *  SPY , QQQ , DIA
     */

    /**
     * 3대 증시 실시간 데이터 조회
     */
    private List<MarketIndexResponse> getMajorMarkets() {
        return List.of(
                new MarketIndexResponse("S&P 500", finnhubClient.getQuote("SPY")),
                new MarketIndexResponse("NASDAQ", finnhubClient.getQuote("QQQ")),
                new MarketIndexResponse("DOW JONES", finnhubClient.getQuote("DIA"))
        );
    }

    /**
     * 3대 증시 마감 데이터 수집 및 저장 파이프라인
     */
    @Transactional
    public void saveDailyMarketSummary() {
        // 미국 뉴욕 시간 기준 날짜 계산 (장 마감 시점의 현지 날짜를 정확히 동기화)
        LocalDate usMarketDate = LocalDate.now(ZoneId.of("America/New_York"));

        List<MarketIndexResponse> markets = getMajorMarkets();

        for (MarketIndexResponse market : markets) {
            // 외부 API 통신 실패 등으로 quote 데이터가 누락된 경우 예외 처리
            if (market.quote() == null) {
                log.warn("[Stock Ingestion] {} 데이터가 누락되어 저장을 건너뜁니다.", market.name());
                continue;
            }

            // 1. 중복 저장 방지 (멱등성 검증)
            if (stockRepository.existsByMarketDateAndIndexName(usMarketDate, market.name())) {
                log.info("[Stock Ingestion] 이미 존재하는 데이터입니다. (날짜: {}, 지수: {})", usMarketDate, market.name());
                continue;
            }

            // 2. DTO -> Entity 변환 및 소수점 정밀도 처리 (double -> BigDecimal)
            StockMarketSummary summary = StockMarketSummary.builder()
                    .marketDate(usMarketDate)
                    .indexName(market.name())
                    .closePrice(BigDecimal.valueOf(market.quote().c()))
                    .changeRate(BigDecimal.valueOf(market.quote().dp()))
                    .build();

            // 3. 데이터베이스 적재
            stockRepository.save(summary);
            log.info("[Stock Ingestion] 성공적으로 저장되었습니다. (지수: {}, 종가: {})", market.name(), market.quote().c());
        }
    }




}
