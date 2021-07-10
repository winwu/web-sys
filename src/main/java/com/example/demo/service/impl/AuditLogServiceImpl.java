package com.example.demo.service.impl;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    @Autowired
    AuditLogRepository auditLogRepository;

    @Override
    public void create(String oldContent, String newContent, String eventType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            logger.error("Unauthenticated user");
            return;
        }

        String userName = auth.getName();
        Date dNow = new Date();

        AuditLog log = new AuditLog(oldContent, newContent, eventType, userName, dNow);
        logger.info("create audit log {}", log);

        try {
            auditLogRepository.save(log);
        } catch (Exception e) {
            logger.error("create audit fail {}", e.getMessage());
        }
    }
}
