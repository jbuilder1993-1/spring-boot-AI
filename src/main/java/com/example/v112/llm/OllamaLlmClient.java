package com.example.v112.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OllamaLlmClient implements LlmClient {
    private final RestClient restClient;
    private final String baseUrl;
    private final String model;

    public OllamaLlmClient(RestClient.Builder restClientBuilder,
                           @Value("${llm.ollama.base-url:http://localhost:11434}") String baseUrl,
                           @Value("${llm.ollama.model:llama3}") String model) {
        this.restClient = restClientBuilder.build();
        this.baseUrl = baseUrl;
        this.model = model;
    }

    @Override
    public LlmResponse generate(LlmRequest request) {
        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", request.systemPrompt() + "\n\n" + request.userPrompt(),
                "stream", false
        );
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(baseUrl + "/api/generate")
                    .body(body)
                    .retrieve()
                    .body(Map.class);
            Object text = response == null ? null : response.get("response");
            return new LlmResponse("ollama", text == null ? String.valueOf(response) : text.toString());
        } catch (Exception ex) {
            return new LlmResponse("ollama", "Ollama call failed: " + ex.getMessage());
        }
    }
}
