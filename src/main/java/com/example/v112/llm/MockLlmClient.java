package com.example.v112.llm;

import org.springframework.stereotype.Component;

@Component
public class MockLlmClient implements LlmClient {
    @Override
    public LlmResponse generate(LlmRequest request) {
        return new LlmResponse("mock", "MOCK_LLM_RESPONSE: " + request.userPrompt());
    }
}
