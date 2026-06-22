package com.example.agent.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Service
public class LocalHashEmbeddingService {
    private final int dimensions;

    public LocalHashEmbeddingService(@Value("${agent.rag.vector-dimensions:256}") int dimensions) {
        this.dimensions = dimensions;
    }

    public double[] embed(String text) {
        double[] vector = new double[dimensions];
        if (text == null || text.isBlank()) return vector;
        String normalized = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
        for (String token : normalized.split("\\s+")) {
            if (token.isBlank()) continue;
            int index = Math.floorMod(stableHash(token), dimensions);
            vector[index] += 1.0;
        }
        normalize(vector);
        return vector;
    }

    private int stableHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return ((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
        } catch (Exception e) {
            return token.hashCode();
        }
    }

    private void normalize(double[] vector) {
        double sum = 0;
        for (double v : vector) sum += v * v;
        if (sum == 0) return;
        double norm = Math.sqrt(sum);
        for (int i = 0; i < vector.length; i++) vector[i] = vector[i] / norm;
    }
}
