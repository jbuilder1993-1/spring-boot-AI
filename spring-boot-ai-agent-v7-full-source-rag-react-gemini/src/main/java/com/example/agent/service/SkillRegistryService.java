package com.example.agent.service;

import com.example.agent.dto.SkillSummary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SkillRegistryService {
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private static final Pattern NAME = Pattern.compile("(?m)^name:\\s*([^\\n]+)");
    private static final Pattern DESC = Pattern.compile("(?m)^description:\\s*([^\\n]+)");

    public List<SkillSummary> listSkills() {
        try {
            Resource[] resources = resolver.getResources("classpath:/skills/*/SKILL.md");
            List<SkillSummary> result = new ArrayList<>();
            for (Resource r : resources) {
                String text = r.getContentAsString(StandardCharsets.UTF_8);
                result.add(new SkillSummary(extract(NAME, text, "unknown"), extract(DESC, text, "No description"), r.getURL().toString()));
            }
            return result;
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    public String loadSkillInstructions(String skill) {
        try {
            String normalized = skill.trim().toLowerCase(Locale.ROOT).replace(' ', '-');
            Resource resource = resolver.getResource("classpath:/skills/" + normalized + "/SKILL.md");
            if (!resource.exists()) return "Skill not found: " + skill;
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    public String selectBestSkill(String request) {
        String lower = request.toLowerCase(Locale.ROOT);
        if (lower.contains("cobol") || lower.contains("copybook")) return "cobol-migration";
        if (lower.contains("perl") || lower.contains("cgi")) return "perl-cgi-modernization";
        if (lower.contains("review") || lower.contains("controller") || lower.contains("service")) return "spring-boot-code-review";
        if (lower.contains("document") || lower.contains("rag") || lower.contains("pdf")) return "rag-analysis";
        return "report-generation";
    }

    private String extract(Pattern pattern, String text, String fallback) {
        var m = pattern.matcher(text);
        return m.find() ? m.group(1).replace("\"", "").trim() : fallback;
    }
}
