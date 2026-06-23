package com.example.v112.llm;

public interface LlmClient {
    LlmResponse generate(LlmRequest request);
}
