package com.example.v112.dto;

public record RagDocument(String id, String title, String content, String source, int chunkIndex, double[] embedding) {
}
