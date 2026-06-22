package com.example.agent.controller;

import com.example.agent.dto.*;
import com.example.agent.service.OrchestratorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v7/agent")
public class AgentController {
    private final OrchestratorService orchestrator;
    public AgentController(OrchestratorService orchestrator) { this.orchestrator = orchestrator; }
    @PostMapping("/run") public AgentRunResponse run(@Valid @RequestBody AgentRunRequest request) { return orchestrator.run(request.message(), request.includeTrace(), request.includeEvaluation()); }
    @GetMapping("/run") public AgentRunResponse runGet(@RequestParam String q) { return orchestrator.run(q, true, true); }
    @GetMapping("/health") public String health() { return "V7 full-source RAG ReAct agent running"; }
}
