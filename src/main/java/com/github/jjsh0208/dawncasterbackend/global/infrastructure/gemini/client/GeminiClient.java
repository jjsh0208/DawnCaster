package com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.Service.PromptGeneratorService;
import com.github.jjsh0208.dawncasterbackend.global.infrastructure.gemini.dto.AiAnalysisResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final ChatModel chatModel;

    private final PromptGeneratorService promptGeneratorService;
    private final ObjectMapper objectMapper;

    public AiAnalysisResultDto summarizeSectorNews(String crawledNewsText){

        String systemPrompt = promptGeneratorService.getSystemPrompt();

        String userPrompt = promptGeneratorService.generateUserPrompt(crawledNewsText);


        SystemMessage systemMessage = SystemMessage.builder()
                .text(systemPrompt)
                .build();

        UserMessage userMessage = UserMessage.builder()
                .text(userPrompt)
                .build();

        ChatOptions chatOptions = ChatOptions.builder()
                .maxTokens(8192)
                .build();

        Prompt prompt =Prompt.builder()
                .messages(systemMessage, userMessage)
                .chatOptions(chatOptions)
                .build();

        try {
            ChatResponse chatResponse = chatModel.call(prompt);
            String responseText = chatResponse.getResult().getOutput().getText();

            // String 형태의 JSON을 DTO 객체로 변환
            return objectMapper.readValue(responseText.trim(), AiAnalysisResultDto.class);

        } catch (Exception e) {
            log.error("[GeminiClient] JSON 파싱 또는 API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("AI 분석 결과를 생성할 수 없습니다.", e);
        }
    }
}
