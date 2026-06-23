package com.example.v112.rag;

import com.example.v112.dto.RagDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class JsonVectorStore {
    private final ObjectMapper mapper;
    private final LocalHashEmbeddingService embeddings;
    private final TextChunker chunker;
    private final File file;
    private final int chunkSize;
    private final int overlap;
    private final int topK;

    public JsonVectorStore(ObjectMapper mapper,
                           LocalHashEmbeddingService embeddings,
                           TextChunker chunker,
                           @Value("${rag.store-file:data/knowledge.json}") String storeFile,
                           @Value("${rag.chunk-size:900}") int chunkSize,
                           @Value("${rag.chunk-overlap:120}") int overlap,
                           @Value("${rag.top-k:5}") int topK) {
        this.mapper = mapper;
        this.embeddings = embeddings;
        this.chunker = chunker;
        this.file = new File(storeFile);
        this.chunkSize = chunkSize;
        this.overlap = overlap;
        this.topK = topK;
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
    }

    public List<RagDocument> ingest(String title, String content, String source) {
        List<RagDocument> docs = loadAll();
        List<RagDocument> added = new ArrayList<>();
        int i = 0;
        for (String chunk : chunker.chunk(content, chunkSize, overlap)) {
            RagDocument d = new RagDocument(UUID.randomUUID().toString(), title, chunk, source == null ? "manual" : source, i++, embeddings.embed(chunk));
            docs.add(d);
            added.add(d);
        }
        save(docs);
        return added;
    }

    public List<String> search(String query) {
        double[] q = embeddings.embed(query);
        return loadAll().stream()
                .sorted(Comparator.comparingDouble((RagDocument d) -> score(query, q, d)).reversed())
                .limit(topK)
                .map(d -> "[" + d.title() + " | " + d.source() + " | chunk " + d.chunkIndex() + "] " + d.content())
                .toList();
    }

    public List<RagDocument> loadAll() {
        try {
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<List<RagDocument>>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load JSON vector store", e);
        }
    }

    public void clear() {
        save(new ArrayList<>());
    }

    private void save(List<RagDocument> docs) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, docs);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to write JSON vector store", e);
        }
    }

    private double score(String query, double[] q, RagDocument d) {
        return cosine(q, d.embedding()) + keyword(query, d.content());
    }

    private double cosine(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length) return 0;
        double dot = 0, n1 = 0, n2 = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            n1 += a[i] * a[i];
            n2 += b[i] * b[i];
        }
        return n1 == 0 || n2 == 0 ? 0 : dot / (Math.sqrt(n1) * Math.sqrt(n2));
    }

    private double keyword(String query, String content) {
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);
        String c = content == null ? "" : content.toLowerCase(Locale.ROOT);
        double s = 0;
        for (String token : q.split("\\s+")) if (!token.isBlank() && c.contains(token)) s += 0.15;
        return Math.min(s, 1.0);
    }
}
