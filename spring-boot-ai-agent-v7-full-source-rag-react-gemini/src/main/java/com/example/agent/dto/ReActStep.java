package com.example.agent.dto;
import java.util.Map;
public record ReActStep(String thought, String action, Map<String,Object> actionInput, String finalAnswer) {}
