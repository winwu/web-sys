package com.example.demo.controller;

import com.example.demo.entity.AuditLog;
import com.example.demo.exception.CustomException;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.service.impl.AuditLogServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @ApiOperation(value = "Retrieve all AuditLogs", notes = "role system admin ONLY")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<Iterable<AuditLog>> getAllAudit() {
        Iterable<AuditLog> logs = auditLogRepository.findAll();
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @ApiOperation(value = "get AuditLog by id")
    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public ResponseEntity getAuditById(@PathVariable("id") String id, HttpServletRequest request) {
        String userName = request.getUserPrincipal().getName();
        Optional<AuditLog> log = auditLogRepository.findById(id);
        if (log.isEmpty()) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }
        if (!log.get().getUserName().equals(userName)) {
            throw new CustomException("Unauthorized log", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(log, HttpStatus.OK);
    }

    @ApiOperation(value = "get AuditLog by eventType", notes = "role system admin ONLY")
    @ApiImplicitParam(name="eventType", allowableValues = "auth, news, product")
    @RequestMapping(value = "/eventType/{eventType}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<?> getAuditByEventType(@PathVariable("eventType") String eventType) {
        List<AuditLog> logs = auditLogRepository.findByEventTypeOrderByCreatedAtDesc(eventType);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @ApiOperation(value = "get AuditLog by username", notes = "role system admin ONLY")
    @RequestMapping(value = "/username/{name}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity getAuditByUserName(@PathVariable("name") String name) {
        List<AuditLog> logs = auditLogRepository.findByUserNameOrderByCreatedAtDesc(name);
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    @ApiOperation(value = "get AuditLog by search")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "start from which page, start from 0", defaultValue = "0"),
            @ApiImplicitParam(name = "limit", value = "how many records after page", defaultValue = "10"),
            @ApiImplicitParam(name = "query", value = "search text from oldContent, newContent and eventType", defaultValue = "")
    })
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity getSearchResult(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "query", defaultValue = "") String query,
            HttpServletRequest request) {

        String userName = request.getUserPrincipal().getName();

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        if (!query.isEmpty()) {
            nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(query, "oldContent", "newContent", "eventType", "message"));
        }

        // users can only search for their own audit logs
        // @TODO determine by user role, if user role is "ROLE ROLE_SYSTEM_ADMIN" can see all of user audits, otherwise can only see logs by their own.
        nativeSearchQueryBuilder.withFilter(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("userName", userName)));
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

    @ApiOperation(value = "test AuditLog api", notes = "try other manipulation in this controller. role system admin ONLY")
    @RequestMapping(value = "test", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void testCreate() {
        auditLogService.create("old content", "new content", "testing", "testing msg");
    }
}
