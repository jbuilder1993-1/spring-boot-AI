package com.example.agent.dto;
import jakarta.validation.constraints.NotBlank;
public record RagIngestRequest(@NotBlank String title, @NotBlank String content, String source) {}
