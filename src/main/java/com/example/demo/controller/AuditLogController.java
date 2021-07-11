package com.example.demo.controller;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.service.impl.AuditLogServiceImpl;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity getSearchResult(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "query", defaultValue = "") String query) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        // @TODO filter by userName
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(query, "oldContent", "newContent", "eventType", "message"));
        nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("createdAt").order(SortOrder.DESC));
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page, limit));
        Page<AuditLog> logs = auditLogRepository.search(nativeSearchQueryBuilder.build());

        Map<String, Object> response = new HashMap<>();
        response.put("code", "SUCCESS");
        response.put("data", logs.getContent());
        response.put("currentPage", logs.getNumber());
        response.put("totalRecords", logs.getTotalElements());
        response.put("totalPages", logs.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public void testCreate() {
        auditLogService.create("old content", "new content", "testing", "testing msg");
    }
}
