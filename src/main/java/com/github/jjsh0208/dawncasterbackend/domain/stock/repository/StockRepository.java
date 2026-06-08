package com.github.jjsh0208.dawncasterbackend.domain.stock.repository;

import com.github.jjsh0208.dawncasterbackend.domain.stock.entity.StockMarketSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface StockRepository extends JpaRepository<StockMarketSummary, Long> {
    // 날짜와 증시 명을 복합적으로 체크하여 중복 적재를 방지합니다.
    boolean existsByMarketDateAndIndexName(LocalDate marketDate, String indexName);
}
