package com.example.demo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {
    private static final String PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;


    // @TODO: add debug in application.properties, and change the ErrorAttributeOptions accordingly
    // private boolean debug = true;

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorFromAttr(HttpServletRequest req, WebRequest webRequest) {
        RequestAttributes reqAttributes = new ServletRequestAttributes(req);
        // @TODO: remove path, timestamp in response
        return errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STACK_TRACE, ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.BINDING_ERRORS));
    }

    @RequestMapping(value = PATH)
    public ResponseEntity errorResponse(HttpServletRequest request, WebRequest webRequest, HttpServletResponse response) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", response.getStatus());
        error.put("error", getErrorFromAttr(request, webRequest));
        return new ResponseEntity(error, HttpStatus.valueOf(response.getStatus()));
    }

}
