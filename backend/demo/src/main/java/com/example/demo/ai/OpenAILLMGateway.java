package com.example.demo.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenAILLMGateway implements LLMGateway {
    
    private final RestClient restClient;
    private final String baseUrl;
    private final String model;
    
    public OpenAILLMGateway(
            @Value("${llm.base-url}") String baseUrl,
            @Value("${llm.model}") String model,
            @Value("${LLM_API_KEY:}") String apiKey) {
        this.baseUrl = baseUrl;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("product", "PC1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }
    
    @Override
    public String chatCompletion(String prompt) {
        try {
            String requestBody = """
                {
                    "model": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ]
                }
                """.formatted(model, prompt.replace("\"", "\\\""));
            
            log.debug("Sending request to LLM: {}", requestBody);
            
            String response = restClient.post()
                    .uri("/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
            
            log.debug("Received response from LLM: {}", response);
            return response;
        } catch (RestClientResponseException e) {
            log.error("LLM API error: {}", e.getMessage());
            throw new RuntimeException("Failed to get response from LLM: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error when calling LLM: {}", e.getMessage());
            throw new RuntimeException("Unexpected error when calling LLM: " + e.getMessage(), e);
        }
    }
}