package com.example.agent.dto;
import java.time.OffsetDateTime;
public record AuditEvent(String auditId, OffsetDateTime timestamp, String phase, String detail) {}
