package com.example.demo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${debug}")
    private boolean debug;

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorFromAttr(HttpServletRequest req, WebRequest webRequest) {
        RequestAttributes reqAttributes = new ServletRequestAttributes(req);
        return errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.STACK_TRACE, ErrorAttributeOptions.Include.MESSAGE, ErrorAttributeOptions.Include.BINDING_ERRORS));
    }

    @RequestMapping(value = PATH)
    public ResponseEntity errorResponse(HttpServletRequest request, WebRequest webRequest, HttpServletResponse response) {
        Map<String, Object> customResponse = new HashMap<>();
        customResponse.put("code", response.getStatus());

        Map<String, Object> errorAttributes = getErrorFromAttr(request, webRequest);

        if (!this.debug) {
            // use Collection.removeIf ?
            errorAttributes.remove("path");
            errorAttributes.remove("timestamp");
        }

        customResponse.put("error", errorAttributes);

        return new ResponseEntity(customResponse, HttpStatus.valueOf(response.getStatus()));
    }

}
