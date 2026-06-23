package com.example.v112.dto;

import jakarta.validation.constraints.NotBlank;

public record RagIngestRequest(@NotBlank String title, @NotBlank String content, String source) {
}
