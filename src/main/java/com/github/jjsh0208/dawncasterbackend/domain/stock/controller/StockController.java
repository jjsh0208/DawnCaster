package com.github.jjsh0208.dawncasterbackend.domain.stock.controller;

import com.github.jjsh0208.dawncasterbackend.domain.stock.service.StockService;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto.MarketIndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stock")
public class StockController {

    private final StockService stockService;

    @GetMapping("/test")
    public List<MarketIndexResponse> getMarkets() {
        return stockService.getMajorMarkets();
    }

}
