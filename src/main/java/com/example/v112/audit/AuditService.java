package com.example.v112.audit;

import com.example.v112.dto.AuditEvent;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {
    private final List<AuditEvent> events = new ArrayList<>();

    public String start(String detail) {
        String id = UUID.randomUUID().toString();
        record(id, "START", detail);
        return id;
    }

    public void record(String id, String phase, String detail) {
        events.add(new AuditEvent(id, OffsetDateTime.now(), phase, detail));
    }

    public List<AuditEvent> all() {
        return List.copyOf(events);
    }
}
