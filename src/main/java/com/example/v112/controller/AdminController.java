package com.example.v112.controller;

import com.example.v112.audit.AuditService;
import com.example.v112.dto.AuditEvent;
import com.example.v112.dto.SkillSummary;
import com.example.v112.skill.SkillRegistryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v112/admin")
public class AdminController {
    private final AuditService audit;
    private final SkillRegistryService skills;

    public AdminController(AuditService audit, SkillRegistryService skills) {
        this.audit = audit;
        this.skills = skills;
    }

    @GetMapping("/audit")
    public List<AuditEvent> audit() {
        return audit.all();
    }

    @GetMapping("/skills")
    public List<SkillSummary> skills() {
        return skills.listSkills();
    }

    @GetMapping("/skills/{name}")
    public String skill(@PathVariable String name) {
        return skills.loadSkill(name);
    }
}
