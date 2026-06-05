package com.github.jjsh0208.dawncasterbackend.domain.stock.entity;

import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_market_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMarketSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_date", nullable = false)
    private LocalDate marketDate;

    @Column(name = "index_name", nullable = false, length = 100)
    private String indexName;

    @Column(name = "close_price", precision = 15, scale = 2)
    private BigDecimal closePrice;

    @Column(name = "change_rate", precision = 8, scale = 2)
    private BigDecimal changeRate;
}