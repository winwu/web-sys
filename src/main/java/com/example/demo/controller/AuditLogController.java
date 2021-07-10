package com.example.demo.controller;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.service.impl.AuditLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/audit")
public class AuditLogController {
    @Autowired
    AuditLogRepository auditLogRepository;

    @Autowired
    AuditLogServiceImpl auditLogService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Iterable<AuditLog>> getAllAudit() {
        Iterable<AuditLog> logs = auditLogRepository.findAll();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity getAuditById(@PathVariable("id") String id) {
        Optional<AuditLog> log = auditLogRepository.findById(id);
        return new ResponseEntity<>(log, HttpStatus.OK);
    }

    @RequestMapping(value = "/event/{eventType}", method = RequestMethod.GET)
    public ResponseEntity getAuditByEventType(@PathVariable("eventType") String eventType) {
        List<AuditLog> logs = auditLogRepository.findByEventType(eventType);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @RequestMapping(value = "/username/{name}", method = RequestMethod.GET)
    public ResponseEntity getAuditByUserName(@PathVariable("name") String name) {
        List<AuditLog> logs = auditLogRepository.findByUserName(name);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public void testCreate() {
        auditLogService.create("old content", "new content", "testing", "testing msg");
    }
}
