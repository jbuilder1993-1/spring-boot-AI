package com.example.v112.controller;

import com.example.v112.dto.RagDocument;
import com.example.v112.dto.RagIngestRequest;
import com.example.v112.rag.JsonVectorStore;
import com.example.v112.rag.PdfTextService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v112/rag")
public class RagController {
    private final JsonVectorStore store;
    private final PdfTextService pdf;

    public RagController(JsonVectorStore store, PdfTextService pdf) {
        this.store = store;
        this.pdf = pdf;
    }

    @PostMapping("/text")
    public List<RagDocument> ingest(@Valid @RequestBody RagIngestRequest request) {
        return store.ingest(request.title(), request.content(), request.source());
    }

    @PostMapping("/pdf")
    public List<RagDocument> pdf(@RequestParam("file") MultipartFile file) {
        return store.ingest(file.getOriginalFilename(), pdf.extract(file), "pdf-upload");
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String q) {
        return store.search(q);
    }

    @GetMapping("/documents")
    public List<RagDocument> documents() {
        return store.loadAll();
    }

    @DeleteMapping("/documents")
    public void clear() {
        store.clear();
    }
}
