package com.example.demo.service;

import com.example.demo.entity.AuditLog;

public interface AuditLogService {
    public void create(String oldContent, String newContent, String eventType, String message);
}
