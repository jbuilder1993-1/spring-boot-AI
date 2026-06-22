package com.example.agent.controller;

import com.example.agent.dto.AuditEvent;
import com.example.agent.service.AuditService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v7/audit")
public class AuditController {
    private final AuditService audit;
    public AuditController(AuditService audit) { this.audit = audit; }
    @GetMapping public List<AuditEvent> all() { return audit.all(); }
    @GetMapping("/{auditId}") public List<AuditEvent> byAudit(@PathVariable String auditId) { return audit.byAuditId(auditId); }
}
