package com.example.v112.dto;

import jakarta.validation.constraints.NotBlank;

public record AskRequest(@NotBlank String message, boolean includeTrace) {
}
