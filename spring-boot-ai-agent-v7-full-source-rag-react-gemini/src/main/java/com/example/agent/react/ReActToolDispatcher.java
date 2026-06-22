package com.example.agent.react;

import com.example.agent.dto.ToolExecutionResult;
import com.example.agent.rag.HybridRagJsonStore;
import com.example.agent.service.SkillRegistryService;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ReActToolDispatcher {
    private final SkillRegistryService skills;
    private final HybridRagJsonStore rag;
    private final List<String> tasks = new ArrayList<>();

    public ReActToolDispatcher(SkillRegistryService skills, HybridRagJsonStore rag) { this.skills = skills; this.rag = rag; }

    public String toolsText() { return "Tools: listAvailableSkills {}, loadSkillInstructions {skillName}, selectBestSkill {request}, searchKnowledge {query}, currentTorontoDateTime {}, createMigrationChecklist {legacyTechnology,targetTechnology}, createFollowUpTask {title,detail}, listFollowUpTasks {}, FINAL"; }

    public ToolExecutionResult execute(String action, Map<String,Object> input) {
        try {
            String obs = switch(action) {
                case "listAvailableSkills" -> skills.listSkills().toString();
                case "loadSkillInstructions" -> skills.loadSkillInstructions(str(input,"skillName"));
                case "selectBestSkill" -> skills.selectBestSkill(str(input,"request"));
                case "searchKnowledge" -> rag.hybridSearch(str(input,"query")).toString();
                case "currentTorontoDateTime" -> OffsetDateTime.now(ZoneId.of("America/Toronto")).toString();
                case "createMigrationChecklist" -> checklist(str(input,"legacyTechnology"), str(input,"targetTechnology")).toString();
                case "createFollowUpTask" -> addTask(str(input,"title"), str(input,"detail"));
                case "listFollowUpTasks" -> tasks.toString();
                default -> "Unknown tool: " + action + ". " + toolsText();
            };
            return new ToolExecutionResult(action, true, obs);
        } catch (Exception e) { return new ToolExecutionResult(action, false, e.getMessage()); }
    }

    private List<String> checklist(String legacy, String target) { return List.of("Inventory " + legacy, "Map fields to " + target, "Create DTO/API", "Build regression tests", "Validate output", "Prepare rollout plan"); }
    private String addTask(String title, String detail) { String t = "TASK-" + (tasks.size()+1) + ": " + title + " - " + detail; tasks.add(t); return t; }
    private String str(Map<String,Object> input, String key) { Object v = input == null ? null : input.get(key); return v == null ? "" : v.toString(); }
}
