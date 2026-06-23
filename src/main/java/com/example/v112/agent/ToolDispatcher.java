package com.example.v112.agent;

import com.example.v112.dto.ToolResult;
import com.example.v112.rag.JsonVectorStore;
import com.example.v112.skill.SkillRegistryService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ToolDispatcher {
    private final JsonVectorStore store;
    private final SkillRegistryService skills;
    private final List<String> tasks = new ArrayList<>();

    public ToolDispatcher(JsonVectorStore store, SkillRegistryService skills) {
        this.store = store;
        this.skills = skills;
    }

    public ToolResult execute(String tool, String input) {
        try {
            return switch (tool) {
                case "searchKnowledge" -> new ToolResult(tool, true, store.search(input).toString());
                case "loadSkill" -> new ToolResult(tool, true, skills.loadSkill(input));
                case "currentTime" ->
                        new ToolResult(tool, true, OffsetDateTime.now(ZoneId.of("America/Toronto")).toString());
                case "createTask" -> {
                    tasks.add(input);
                    yield new ToolResult(tool, true, "TASK-" + tasks.size() + ": " + input);
                }
                case "listTasks" -> new ToolResult(tool, true, tasks.toString());
                default -> new ToolResult(tool, false, "Unknown tool: " + tool);
            };
        } catch (Exception e) {
            return new ToolResult(tool, false, e.getMessage());
        }
    }
}
