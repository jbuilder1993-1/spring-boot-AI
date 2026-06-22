package com.example.agent.service;

import com.example.agent.dto.AgentRunResponse;
import com.example.agent.react.ReActLoopService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrchestratorService {
    private final SkillRegistryService skills;
    private final ReActLoopService loop;
    private final EvaluatorService evaluator;
    private final AuditService audit;

    public OrchestratorService(SkillRegistryService skills, ReActLoopService loop, EvaluatorService evaluator, AuditService audit) { this.skills = skills; this.loop = loop; this.evaluator = evaluator; this.audit = audit; }

    public AgentRunResponse run(String request, boolean includeTrace, boolean includeEvaluation) {
        String auditId = audit.start(request);
        String skill = skills.selectBestSkill(request);
        audit.record(auditId, "SKILL_SELECTED", skill);
        String instructions = skills.loadSkillInstructions(skill);
        ReActLoopService.LoopResult result = loop.run(request, skill, instructions);
        audit.record(auditId, "FINAL", result.finalAnswer());
        String eval = includeEvaluation ? evaluator.evaluate(request, result.finalAnswer(), result.trace()) : "Evaluation skipped";
        return new AgentRunResponse(auditId, skill, result.finalAnswer(), eval, includeTrace ? result.trace() : List.of());
    }
}
