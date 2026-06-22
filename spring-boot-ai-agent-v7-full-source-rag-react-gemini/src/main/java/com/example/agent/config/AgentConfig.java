package com.example.agent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {
    @Bean
    ChatClient reactReasoningClient(ChatClient.Builder builder) {
        return builder.defaultSystem("""
                You are a ReAct reasoning agent.
                Return STRICT JSON only.
                JSON schema:
                {"thought":"reasoning","action":"tool name or FINAL","actionInput":{"key":"value"},"finalAnswer":"only if FINAL"}
                Available actions are provided in the user prompt.
                Use one action at a time. Use FINAL only when answer is complete.
                """).build();
    }

    @Bean
    ChatClient evaluatorClient(ChatClient.Builder builder) {
        return builder.defaultSystem("""
                You are an enterprise evaluator agent.
                Review the final answer and trace for completeness, grounding, risks, and recommended improvements.
                """).build();
    }
}
