package com.example.agent.dto;
import jakarta.validation.constraints.NotBlank;
public record AgentRunRequest(@NotBlank String message, boolean includeTrace, boolean includeEvaluation) {}
