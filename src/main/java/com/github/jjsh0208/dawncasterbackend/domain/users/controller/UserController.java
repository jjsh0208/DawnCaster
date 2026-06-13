package com.github.jjsh0208.dawncasterbackend.domain.users.controller;

import com.github.jjsh0208.dawncasterbackend.domain.subscription.service.SubscriptionService;
import com.github.jjsh0208.dawncasterbackend.domain.users.controller.dto.SubscriptionReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/subscription")
    public ResponseEntity<?> subscription(@Valid @RequestBody SubscriptionReqDto dto){
        subscriptionService.subscribeCategories(dto.email(), dto.categoryIds());
        return ResponseEntity.ok("구독 성공");
    }


}
