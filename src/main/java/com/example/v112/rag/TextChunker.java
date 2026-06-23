package com.example.v112.rag;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunker {
    public List<String> chunk(String text, int size, int overlap) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) return result;
        String clean = text.replaceAll("\\s+", " ").trim();
        int start = 0;
        while (start < clean.length()) {
            int end = Math.min(clean.length(), start + size);
            result.add(clean.substring(start, end));
            if (end == clean.length()) break;
            start = Math.max(0, end - overlap);
        }
        return result;
    }
}
