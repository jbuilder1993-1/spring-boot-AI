package com.example.v112.routing;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class SmartRouterService {
    public RouteType route(String input) {
        String t = input == null ? "" : input.toLowerCase(Locale.ROOT);
        if (any(t, "pdf", "document", "docs", "knowledge", "search", "uploaded")) return RouteType.RAG;
        if (any(t, "analyze", "analysis", "migration", "plan", "workflow", "compare", "review code"))
            return RouteType.AGENT;
        return RouteType.DIRECT;
    }

    private boolean any(String text, String... keys) {
        for (String k : keys) if (text.contains(k)) return true;
        return false;
    }
}
