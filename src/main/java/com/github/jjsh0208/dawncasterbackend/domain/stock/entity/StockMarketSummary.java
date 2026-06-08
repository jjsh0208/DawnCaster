package com.github.jjsh0208.dawncasterbackend.domain.stock.entity;

import com.github.jjsh0208.dawncasterbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_market_summary", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_market_date_index",
                columnNames = {"market_date", "index_name"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockMarketSummary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "market_date", nullable = false)
    private LocalDate marketDate; //미국 장 마감 날짜

    @Column(name = "index_name", nullable = false, length = 100)
    private String indexName; // 증시 명

    @Column(name = "close_price", precision = 15, scale = 2)
    private BigDecimal closePrice; // 마감 종가

    @Column(name = "change_rate", precision = 8, scale = 2)
    private BigDecimal changeRate; // 전일 대비 등락률 (%)
}