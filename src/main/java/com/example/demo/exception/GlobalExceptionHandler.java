package com.example.demo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 400 - Bad Request - Missing params
     * 利如: POST form data 忘記帶入相對應的 key 和 value
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Map<String, Object> handleMissingServletRequestParamException(MissingServletRequestParameterException e) {
        logger.error("缺少請求參數", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        return response;
    }

    /**
     * 400 - Bad Request - Required request body is missing
     * 例如: POST 忘記帶入 body 內容
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("缺少請求內容", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", e.getMessage());
        return response;
    }

    /**
     * 400 - Bad Request - Constrain Violation
     * 利如: 請求參數不符合 entity 限制規則
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> handleValidationException(ConstraintViolationException e) {
        logger.error("請求參數不符合規則", e);

        Map<String, Object> response = new HashMap<>();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        Map<String, String> errors = new HashMap<>();
        for (Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator(); iterator.hasNext(); ) {
            ConstraintViolation<?> next = iterator.next();
            errors.put(String.valueOf(next.getPropertyPath()), next.getMessage());
        }
        response.put("errors", errors);
        response.put("code", 400);
        response.put("message", e.getMessage());
        return response;
    }

    /**
     * 400 - Bad Request - Constraint violation
     * 利如: unique 的欄位出現重複，像是註冊重複的 email 會收到這樣的 error
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityViolationExceptionException(DataIntegrityViolationException e) {
        logger.error("請求參數有可能已經存在於 Database", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "Constraint violation occurred. Cannot insert the same record twice");
        return response;
    }

    /**
     * 403 - Forbidden
     */
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ExceptionHandler(AccessDeniedException.class)
//    public Map<String, Object> handleAccessDeniedException(AccessDeniedException e) {
//        logger.error("403 權限不足", e);
//        System.out.println(e.getClass());
//        Map<String, Object> response = new HashMap<>();
//        response.put("code", 403);
//        response.put("message", e.getMessage());
//        return response;
//    }

    /**
     * 405 - Method not allowed
     * 例如: 呼叫沒有開出來的 method
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Map<String, Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error("不允許的請求", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 405);
        response.put("message", e.getMessage());
        return response;
    }

    /**
     * Custom Exception
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(HttpServletResponse res, CustomException e) {
        logger.error("CustomException 錯誤", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", e.getHttpStatus().value());
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }
    // public void handleCustomException(HttpServletResponse res, CustomException e) throws IOException {
    //     res.sendError(e.getHttpStatus().value(), e.getMessage());
    // }

    /**
     * 500 && Others - Internal Server Error and other exceptions
     * 例如: 不在上面所列出的 Exception
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(HttpServletResponse res, Exception e) {
        logger.error("異常 Exception", e);
        Map<String, Object> response = new HashMap<>();
        response.put("code", res.getStatus());
        response.put("message", e.getMessage());
        response.put("class", e.getClass());
        return response;
    }
}
