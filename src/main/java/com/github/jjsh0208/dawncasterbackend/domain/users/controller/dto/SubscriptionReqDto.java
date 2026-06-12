package com.github.jjsh0208.dawncasterbackend.domain.users.controller.dto;


import java.util.List;

public record SubscriptionReqDto(
        String email,
        List<Long> categoryIds
){
}
