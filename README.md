# V11.2 Full Enterprise Config Platform — Spring Boot 4.1.0

This package includes the full V11.2 source code plus the enterprise configuration layer requested.

## Key additions in this fixed package
- `config/JacksonConfig.java` explicitly defines the `ObjectMapper` bean.
- `config/HttpClientConfig.java` explicitly defines the `RestClient.Builder` bean.
- `config/PlatformConfig.java` centralizes platform-level config access.
- Full UI under `src/main/resources/static/`.
- RAG, PDF ingestion, Gemini, Ollama, mock LLM, agent loop, skills, audit, CI, Postman collection.

## Requirements
- Java 21
- Maven

## Run tests
```bash
mvn clean test
```

## Run with mock provider
```bash
mvn spring-boot:run
```

## Run with Gemini
```bash
export LLM_PROVIDER=gemini
export GEMINI_API_KEY=your-key
mvn spring-boot:run
```

## Run with Ollama
```bash
ollama serve
ollama pull llama3
LLM_PROVIDER=ollama mvn spring-boot:run
```

## UI
Open:
```text
http://localhost:8080/
```

## APIs
- `GET /api/v112/health`
- `POST /api/v112/ask`
- `POST /api/v112/rag/text`
- `POST /api/v112/rag/pdf`
- `GET /api/v112/rag/search?q=...`
- `GET /api/v112/admin/audit`
- `GET /api/v112/admin/skills`
