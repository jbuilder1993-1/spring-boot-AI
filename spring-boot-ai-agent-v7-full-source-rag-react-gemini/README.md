# V7 Full Source Enterprise RAG + ReAct Gemini Agent

This ZIP contains the complete source project, combining:

- Spring Boot 4.1.0
- Java 25
- Spring AI 2.0.0 Gemini chat integration
- Manual ReAct harness loop
- File-based skills
- PDF ingestion using PDFBox
- Chunking with overlap
- JSON vector storage at `data/knowledge.json`
- Hybrid retrieval: local hash-vector cosine + keyword boost
- Audit endpoints

## Why local hash vectors?

This version intentionally uses a deterministic local hash embedding service so the project is portable in restricted enterprise VMs and does not require a separate vector database or embedding endpoint. You can later replace `LocalHashEmbeddingService` with a Spring AI `EmbeddingModel` implementation.

## Run

```powershell
$env:GEMINI_API_KEY="your-key"
mvn spring-boot:run
```

or:

```bash
export GEMINI_API_KEY=your-key
mvn spring-boot:run
```

## RAG APIs

### Ingest text
```bash
curl -X POST "http://localhost:8080/api/v7/rag/text" \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"COBOL Copybook Notes\",\"content\":\"CUSTOMER-ID is PIC X(10). ACCOUNT-BALANCE is PIC S9(9)V99 COMP-3.\",\"source\":\"manual\"}"
```

### Ingest PDF
```bash
curl -X POST "http://localhost:8080/api/v7/rag/pdf" \
  -F "file=@/path/to/report.pdf"
```

### Search
```bash
curl "http://localhost:8080/api/v7/rag/search?q=customer id copybook"
```

## Agent API

```bash
curl -X POST "http://localhost:8080/api/v7/agent/run" \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"Use RAG and COBOL migration skill to explain CUSTOMER-ID mapping. Create one follow-up task.\",\"includeTrace\":true,\"includeEvaluation\":true}"
```

## Key classes

- `HybridRagJsonStore` — JSON vector storage + hybrid search
- `LocalHashEmbeddingService` — deterministic local vectorization
- `PdfIngestionService` — PDF text extraction
- `TextChunker` — chunking with overlap
- `ReActLoopService` — Think/Act/Observe loop
- `ReActToolDispatcher` — real Java tool execution
- `SkillRegistryService` — file-based SKILL.md loading
