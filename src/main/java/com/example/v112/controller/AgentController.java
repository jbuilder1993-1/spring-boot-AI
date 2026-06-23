package com.example.v112.controller;

import com.example.v112.agent.RoutingOrchestrator;
import com.example.v112.dto.AskRequest;
import com.example.v112.dto.AskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v112")
public class AgentController {
    private final RoutingOrchestrator orchestrator;

    public AgentController(RoutingOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return orchestrator.process(request.message(), request.includeTrace());
    }
}
