package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.service.impl.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {

        Map<String, Object> response = new HashMap<>();

        try {
            String token = userService.login(username, password);
            response.put("code", HttpStatus.OK.value());
            response.put("token", token);
            response.put("message", "登入成功");
            return new ResponseEntity(response, HttpStatus.OK);

        } catch (CustomException e) {
            response.put("code", e.getHttpStatus().value());
            response.put("message", e.getMessage());
            return new ResponseEntity(response, e.getHttpStatus());
        }
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = userService.signup(user);
            response.put("code", HttpStatus.OK.value());
            response.put("token", token);
            response.put("message", "註冊成功");
            return new ResponseEntity(response, HttpStatus.OK);
        } catch (CustomException e) {
            response.put("code", e.getHttpStatus().value());
            response.put("message", e.getMessage());
            return new ResponseEntity(response, e.getHttpStatus());
        }
    }

    @ApiOperation(value = "delete", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(@PathVariable String username) {
        userService.delete(username);
        return username;
    }

    // search

    @ApiOperation(value = "me", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public User me(HttpServletRequest req) {
        return userService.me(req);
    }

    @ApiOperation(value = "parse", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/parse", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity<Map<String, Object>> parseJWT(HttpServletRequest req) {
        Map<String, Object> response = userService.parse(req);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @ApiOperation(value = "refresh", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    public ResponseEntity refresh(HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        String token = userService.refresh(req.getRemoteUser());
        response.put("code", HttpStatus.OK.value());
        response.put("token", token);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
