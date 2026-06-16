package com.github.jjsh0208.dawncasterbackend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);       // 기본 유지할 스레드 개수
        executor.setMaxPoolSize(30);        // 최대 스레드 개수
        executor.setQueueCapacity(5000);    // 스레드가 만석일 때 대기할 작업 큐 크기
        executor.setThreadNamePrefix("MailExecutor-");
        executor.initialize();
        return executor;
    }
}
