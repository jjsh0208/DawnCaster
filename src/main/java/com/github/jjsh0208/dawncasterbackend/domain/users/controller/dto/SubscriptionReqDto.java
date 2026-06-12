package com.github.jjsh0208.dawncasterbackend.domain.users.controller.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubscriptionReqDto(

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotEmpty(message = "최소 하나 이상의 카테고리를 선택해야 합니다.")
        List<Long> categoryIds
){
}
