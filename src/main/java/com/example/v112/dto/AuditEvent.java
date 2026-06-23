package com.example.v112.dto;

import java.time.OffsetDateTime;

public record AuditEvent(String auditId, OffsetDateTime timestamp, String phase, String detail) {
}
