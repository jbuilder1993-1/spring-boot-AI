package com.example.v112.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LlmGateway {
    private final String provider;
    private final MockLlmClient mock;
    private final GeminiLlmClient gemini;
    private final OllamaLlmClient ollama;

    public LlmGateway(@Value("${llm.provider:mock}") String provider,
                      MockLlmClient mock,
                      GeminiLlmClient gemini,
                      OllamaLlmClient ollama) {
        this.provider = provider;
        this.mock = mock;
        this.gemini = gemini;
        this.ollama = ollama;
    }

    public LlmResponse generate(String systemPrompt, String userPrompt) {
        LlmRequest request = new LlmRequest(systemPrompt, userPrompt);
        return switch (provider.toLowerCase()) {
            case "gemini" -> gemini.generate(request);
            case "ollama" -> ollama.generate(request);
            default -> mock.generate(request);
        };
    }

    public String activeProvider() {
        return provider;
    }
}
