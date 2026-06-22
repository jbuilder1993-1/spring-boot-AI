package com.example.agent.dto;
import java.util.List;
public record AgentRunResponse(String auditId, String selectedSkill, String finalAnswer, String evaluation, List<ReActTrace> trace) {}
