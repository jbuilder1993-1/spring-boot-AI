package com.example.v112.agent;

import com.example.v112.audit.AuditService;
import com.example.v112.dto.AskResponse;
import com.example.v112.llm.LlmResponse;
import com.example.v112.routing.RouteType;
import com.example.v112.routing.SmartRouterService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoutingOrchestrator {
    private final SmartRouterService router;
    private final DirectService direct;
    private final RagService rag;
    private final AgentLoopService agent;
    private final AuditService audit;

    public RoutingOrchestrator(SmartRouterService router, DirectService direct, RagService rag, AgentLoopService agent, AuditService audit) {
        this.router = router;
        this.direct = direct;
        this.rag = rag;
        this.agent = agent;
        this.audit = audit;
    }

    public AskResponse process(String input, boolean includeTrace) {
        String auditId = audit.start(input);
        RouteType route = router.route(input);
        audit.record(auditId, "ROUTE", route.name());
        if (route == RouteType.RAG) {
            LlmResponse r = rag.run(input);
            return new AskResponse(route.name(), r.provider(), r.content(), includeTrace ? List.of("auditId=" + auditId, "ROUTE:RAG", "RAG_SEARCH", "LLM_GENERATE") : List.of());
        }
        if (route == RouteType.AGENT) {
            var r = agent.run(input);
            List<String> trace = new ArrayList<>();
            trace.add("auditId=" + auditId);
            trace.addAll(r.trace());
            return new AskResponse(route.name(), r.response().provider(), r.response().content(), includeTrace ? trace : List.of());
        }
        LlmResponse r = direct.run(input);
        return new AskResponse(route.name(), r.provider(), r.content(), includeTrace ? List.of("auditId=" + auditId, "ROUTE:DIRECT", "LLM_GENERATE") : List.of());
    }
}
