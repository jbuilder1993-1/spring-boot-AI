package com.example.v112.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformConfig {
    @Value("${llm.provider:mock}")
    private String llmProvider;

    public String llmProvider() {
        return llmProvider;
    }
}
