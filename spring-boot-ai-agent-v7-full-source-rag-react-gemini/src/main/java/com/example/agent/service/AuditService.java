package com.example.agent.service;

import com.example.agent.dto.AuditEvent;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class AuditService {
    private final List<AuditEvent> events = new ArrayList<>();
    public String start(String detail) { String id = UUID.randomUUID().toString(); record(id, "START", detail); return id; }
    public void record(String auditId, String phase, String detail) { events.add(new AuditEvent(auditId, OffsetDateTime.now(), phase, detail)); }
    public List<AuditEvent> all() { return List.copyOf(events); }
    public List<AuditEvent> byAuditId(String auditId) { return events.stream().filter(e -> e.auditId().equals(auditId)).toList(); }
}
