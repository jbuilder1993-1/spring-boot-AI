package com.example.v112.agent;

import com.example.v112.llm.LlmGateway;
import com.example.v112.llm.LlmResponse;
import com.example.v112.rag.JsonVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {
    private final JsonVectorStore store;
    private final LlmGateway llm;

    public RagService(JsonVectorStore store, LlmGateway llm) {
        this.store = store;
        this.llm = llm;
    }

    public LlmResponse run(String input) {
        List<String> context = store.search(input);
        return llm.generate("Use retrieved context. If missing, state what evidence is missing.", "Question: " + input + "\nContext: " + context);
    }
}
