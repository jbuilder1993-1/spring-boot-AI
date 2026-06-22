package com.example.agent.rag;

import com.example.agent.dto.RagDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class HybridRagJsonStore {
    private final ObjectMapper objectMapper;
    private final LocalHashEmbeddingService embeddingService;
    private final TextChunker chunker;
    private final File storeFile;
    private final int chunkSize;
    private final int chunkOverlap;
    private final int topK;

    public HybridRagJsonStore(ObjectMapper objectMapper,
                              LocalHashEmbeddingService embeddingService,
                              TextChunker chunker,
                              @Value("${agent.rag.store-file:data/knowledge.json}") String storePath,
                              @Value("${agent.rag.chunk-size:1000}") int chunkSize,
                              @Value("${agent.rag.chunk-overlap:150}") int chunkOverlap,
                              @Value("${agent.rag.top-k:5}") int topK) {
        this.objectMapper = objectMapper;
        this.embeddingService = embeddingService;
        this.chunker = chunker;
        this.storeFile = new File(storePath);
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.topK = topK;
        if (storeFile.getParentFile() != null) storeFile.getParentFile().mkdirs();
    }

    public List<RagDocument> ingest(String title, String content, String source) {
        List<RagDocument> existing = loadAll();
        List<RagDocument> added = new ArrayList<>();
        List<String> chunks = chunker.chunk(content, chunkSize, chunkOverlap);
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            RagDocument doc = new RagDocument(UUID.randomUUID().toString(), title, chunk, source == null ? "manual" : source, i, embeddingService.embed(chunk));
            existing.add(doc);
            added.add(doc);
        }
        save(existing);
        return added;
    }

    public List<String> hybridSearch(String query) {
        List<RagDocument> docs = loadAll();
        double[] queryEmbedding = embeddingService.embed(query);
        return docs.stream()
                .sorted((a, b) -> Double.compare(score(query, queryEmbedding, b), score(query, queryEmbedding, a)))
                .limit(topK)
                .map(d -> "[" + d.title() + " | " + d.source() + " | chunk " + d.chunkIndex() + "] " + d.content())
                .toList();
    }

    public List<RagDocument> loadAll() {
        try {
            if (!storeFile.exists()) return new ArrayList<>();
            return objectMapper.readValue(storeFile, new TypeReference<List<RagDocument>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load JSON vector store", e);
        }
    }

    public void clear() {
        save(new ArrayList<>());
    }

    private void save(List<RagDocument> docs) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storeFile, docs);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save JSON vector store", e);
        }
    }

    private double score(String query, double[] queryEmbedding, RagDocument doc) {
        return cosine(queryEmbedding, doc.embedding()) + keywordScore(query, doc.content());
    }

    private double cosine(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length) return 0;
        double dot = 0, n1 = 0, n2 = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            n1 += a[i] * a[i];
            n2 += b[i] * b[i];
        }
        if (n1 == 0 || n2 == 0) return 0;
        return dot / (Math.sqrt(n1) * Math.sqrt(n2));
    }

    private double keywordScore(String query, String content) {
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);
        String c = content == null ? "" : content.toLowerCase(Locale.ROOT);
        double hits = 0;
        for (String token : q.split("\\s+")) {
            if (!token.isBlank() && c.contains(token)) hits += 0.15;
        }
        return Math.min(hits, 1.0);
    }
}
