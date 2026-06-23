package com.example.v112.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Service
public class LocalHashEmbeddingService {
    private final int dimensions;

    public LocalHashEmbeddingService(@Value("${rag.vector-dimensions:256}") int dimensions) {
        this.dimensions = dimensions;
    }

    public double[] embed(String text) {
        double[] v = new double[dimensions];
        if (text == null || text.isBlank()) return v;
        String normalized = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
        for (String token : normalized.split("\\s+")) {
            if (!token.isBlank()) v[Math.floorMod(hash(token), dimensions)] += 1.0;
        }
        normalize(v);
        return v;
    }

    private int hash(String token) {
        try {
            byte[] b = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return ((b[0] & 255) << 24) | ((b[1] & 255) << 16) | ((b[2] & 255) << 8) | (b[3] & 255);
        } catch (Exception e) {
            return token.hashCode();
        }
    }

    private void normalize(double[] v) {
        double s = 0;
        for (double x : v) s += x * x;
        if (s == 0) return;
        double n = Math.sqrt(s);
        for (int i = 0; i < v.length; i++) v[i] /= n;
    }
}
