package com.example.agent.controller;

import com.example.agent.dto.SkillSummary;
import com.example.agent.service.SkillRegistryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v7/skills")
public class SkillController {
    private final SkillRegistryService skills;
    public SkillController(SkillRegistryService skills) { this.skills = skills; }
    @GetMapping public List<SkillSummary> list() { return skills.listSkills(); }
    @GetMapping("/{skillName}") public String load(@PathVariable String skillName) { return skills.loadSkillInstructions(skillName); }
}
