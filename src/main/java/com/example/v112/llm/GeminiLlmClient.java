package com.example.v112.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class GeminiLlmClient implements LlmClient {
    private final RestClient restClient;
    private final String apiKey;
    private final String model;
    private final String endpoint;

    public GeminiLlmClient(RestClient.Builder restClientBuilder,
                           @Value("${llm.gemini.api-key:}") String apiKey,
                           @Value("${llm.gemini.model:gemini-1.5-flash}") String model,
                           @Value("${llm.gemini.endpoint:https://generativelanguage.googleapis.com/v1beta/models}") String endpoint) {
        this.restClient = restClientBuilder.build();
        this.apiKey = apiKey;
        this.model = model;
        this.endpoint = endpoint;
    }

    @Override
    public LlmResponse generate(LlmRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            return new LlmResponse("gemini", "Gemini API key is not configured. Set GEMINI_API_KEY or use LLM_PROVIDER=mock/ollama.");
        }
        String prompt = request.systemPrompt() + "\n\n" + request.userPrompt();
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(endpoint + "/" + model + ":generateContent?key=" + apiKey)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            return new LlmResponse("gemini", String.valueOf(response));
        } catch (Exception ex) {
            return new LlmResponse("gemini", "Gemini call failed: " + ex.getMessage());
        }
    }
}
