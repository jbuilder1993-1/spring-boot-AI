package com.example.agent.react;

import com.example.agent.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReActLoopService {
    private final ChatClient client;
    private final ReActToolDispatcher dispatcher;
    private final ObjectMapper mapper;
    private final int maxIterations;

    public ReActLoopService(@Qualifier("reactReasoningClient") ChatClient client, ReActToolDispatcher dispatcher, ObjectMapper mapper, @Value("${agent.react.max-iterations:6}") int maxIterations) {
        this.client = client; this.dispatcher = dispatcher; this.mapper = mapper; this.maxIterations = maxIterations;
    }

    public LoopResult run(String request, String selectedSkill, String skillInstructions) {
        List<ReActTrace> trace = new ArrayList<>();
        StringBuilder scratch = new StringBuilder();
        for (int i=1; i<=maxIterations; i++) {
            String prompt = "User request:\n" + request + "\nSelected skill:\n" + selectedSkill + "\nSkill instructions:\n" + skillInstructions + "\n" + dispatcher.toolsText() + "\nPrevious observations:\n" + scratch + "\nReturn next strict JSON step.";
            String raw = client.prompt().user(prompt).call().content();
            ReActStep step = parse(raw);
            String action = step.action() == null ? "FINAL" : step.action();
            String inputJson = toJson(step.actionInput());
            if ("FINAL".equalsIgnoreCase(action)) {
                String answer = step.finalAnswer() == null ? raw : step.finalAnswer();
                trace.add(new ReActTrace(i, step.thought(), "FINAL", inputJson, answer));
                return new LoopResult(answer, trace);
            }
            ToolExecutionResult result = dispatcher.execute(action, step.actionInput());
            trace.add(new ReActTrace(i, step.thought(), action, inputJson, result.observation()));
            scratch.append("Iteration ").append(i).append(" Thought: ").append(step.thought()).append(" Action: ").append(action).append(" Input: ").append(inputJson).append(" Observation: ").append(result.observation()).append("\n");
        }
        return new LoopResult("Max iterations reached. Review trace.", trace);
    }

    private ReActStep parse(String raw) { try { return mapper.readValue(clean(raw), ReActStep.class); } catch(Exception e) { return new ReActStep("Model returned non-JSON", "FINAL", Map.of(), raw); } }
    private String clean(String raw) { return raw == null ? "{}" : raw.trim().replaceFirst("^```json", "").replaceFirst("^```", "").replaceFirst("```$", "").trim(); }
    private String toJson(Object v) { try { return mapper.writeValueAsString(v == null ? Map.of() : v); } catch(Exception e) { return String.valueOf(v); } }
    public record LoopResult(String finalAnswer, List<ReActTrace> trace) {}
}
