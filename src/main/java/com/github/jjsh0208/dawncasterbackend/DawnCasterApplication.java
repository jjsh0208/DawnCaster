package com.github.jjsh0208.dawncasterbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DawnCasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(DawnCasterApplication.class, args);
    }

}
