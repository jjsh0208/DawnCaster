package com.github.jjsh0208.dawncasterbackend.global.infrastructure.finnhub.dto;

/**
 * Finnhub 실시간 주가 응답 DTO
 *
 * c  : 현재가(Current Price)
 * d  : 전일 대비 가격 변화(Change)
 * dp : 전일 대비 변동률(Percent Change)
 * h  : 당일 최고가(High Price of the Day)
 * l  : 당일 최저가(Low Price of the Day)
 * o  : 시가(Open Price of the Day)
 * pc : 전일 종가(Previous Close Price)
 */
public record QuoteResponse(
        // 현재 주가
        double c,

        // 전일 대비 상승/하락 가격
        double d,

        // 전일 대비 등락률 (%)
        double dp,

        // 당일 최고가
        double h,

        // 당일 최저가
        double l,

        // 당일 시가
        double o,

        // 전일 종가
        double pc
) {
}