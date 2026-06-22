package com.example.agent.controller;

import com.example.agent.dto.*;
import com.example.agent.rag.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v7/rag")
public class RagController {
    private final HybridRagJsonStore store;
    private final PdfIngestionService pdf;

    public RagController(HybridRagJsonStore store, PdfIngestionService pdf) { this.store = store; this.pdf = pdf; }

    
    @PostMapping("/text") public List<RagDocument> ingestText(@Valid @RequestBody RagIngestRequest request) { return store.ingest(request.title(), request.content(), request.source()); }
    @PostMapping("/pdf") public List<RagDocument> ingestPdf(@RequestParam("file") MultipartFile file) { return store.ingest(file.getOriginalFilename(), pdf.extractText(file), "pdf-upload"); }
    @GetMapping("/search") public List<String> search(@RequestParam String q) { return store.hybridSearch(q); }
    @GetMapping("/documents") public List<RagDocument> documents() { return store.loadAll(); }
    @DeleteMapping("/documents") public void clear() { store.clear(); }
}
