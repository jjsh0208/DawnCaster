package com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PromptGeneratorService {

    // resources 하위의 파일 경로 매핑
    @Value("classpath:prompts/system-prompt.txt")
    private Resource systemPromptResource;

    @Value("classpath:prompts/user-prompt.txt")
    private Resource userPromptResource;

    private String systemPromptTemplate;
    private String userPromptTemplate;

    // 서버 시작 시 파일을 한 번만 읽어서 메모리에 캐싱
    @PostConstruct
    public void init() throws IOException {
        this.systemPromptTemplate = StreamUtils.copyToString(
                systemPromptResource.getInputStream(), StandardCharsets.UTF_8);
        this.userPromptTemplate = StreamUtils.copyToString(
                userPromptResource.getInputStream(), StandardCharsets.UTF_8);
    }

    public String getSystemPrompt() {
        return this.systemPromptTemplate;
    }

    // {{news_data}} 변수를 실제 데이터로 치환하여 반환
    public String generateUserPrompt(String actualNewsData) {
        return this.userPromptTemplate.replace("{{news_data}}", actualNewsData);
    }
}
