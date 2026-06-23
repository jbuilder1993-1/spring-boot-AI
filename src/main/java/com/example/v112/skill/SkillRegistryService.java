package com.example.v112.skill;

import com.example.v112.dto.SkillSummary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SkillRegistryService {
    private static final Pattern NAME = Pattern.compile("(?m)^name:\\s*([^\\n]+)");
    private static final Pattern DESC = Pattern.compile("(?m)^description:\\s*([^\\n]+)");
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public List<SkillSummary> listSkills() {
        try {
            Resource[] resources = resolver.getResources("classpath:/skills/*/SKILL.md");
            List<SkillSummary> out = new ArrayList<>();
            for (Resource r : resources) {
                String text = r.getContentAsString(StandardCharsets.UTF_8);
                out.add(new SkillSummary(extract(NAME, text, "unknown"), extract(DESC, text, "No description"), r.getURL().toString()));
            }
            return out;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to list skills", e);
        }
    }

    public String loadSkill(String name) {
        try {
            Resource resource = resolver.getResource("classpath:/skills/" + name + "/SKILL.md");
            return resource.exists() ? resource.getContentAsString(StandardCharsets.UTF_8) : "Skill not found: " + name;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load skill", e);
        }
    }

    private String extract(Pattern p, String text, String fallback) {
        var m = p.matcher(text);
        return m.find() ? m.group(1).replace("\"", "").trim() : fallback;
    }
}
