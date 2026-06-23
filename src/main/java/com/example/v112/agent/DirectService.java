package com.example.v112.agent;

import com.example.v112.llm.LlmGateway;
import com.example.v112.llm.LlmResponse;
import org.springframework.stereotype.Service;

@Service
public class DirectService {
    private final LlmGateway llm;

    public DirectService(LlmGateway llm) {
        this.llm = llm;
    }

    public LlmResponse run(String input) {
        return llm.generate("Answer directly and concisely.", input);
    }
}
