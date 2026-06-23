package com.example.v112.dto;

import java.util.List;

public record AskResponse(String route, String provider, String answer, List<String> trace) {
}
