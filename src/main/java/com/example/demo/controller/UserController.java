package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.impl.UserServiceImpl;
import io.swagger.annotations.*;
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
@Api(tags = "users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong, maybe missing RequestParams"),
            @ApiResponse(code = 422, message = "Incorrect or missing username or password")
    })
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        String token = userService.login(username, password);
        response.put("code", HttpStatus.OK.value());
        response.put("token", token);
        response.put("message", "登入成功");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody User user) {
        System.out.println(user);
        Map<String, Object> response = new HashMap<>();
        String token = userService.signup(user);
        response.put("code", HttpStatus.OK.value());
        response.put("token", token);
        response.put("message", "註冊成功");
        return new ResponseEntity(response, HttpStatus.OK);
    }


    @ApiOperation(value = "delete", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    @PreAuthorize("hasPermission('', 'admin-users-delete') or hasPermission('', 'admin-users-all')")
    public String delete(@PathVariable String username) {
        userService.delete(username);
        return username;
    }

    // search
    @ApiOperation(value = "me", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public User me(HttpServletRequest req) {
        return userService.me(req);
    }

    @ApiOperation(value = "parse", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/parse", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> parseJWT(HttpServletRequest req) {
        Map<String, Object> response = userService.parse(req);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @ApiOperation(value = "refresh", authorizations = {@Authorization(value = "apiKey")})
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity refresh(HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        String token = userService.refresh(req.getRemoteUser());
        response.put("code", HttpStatus.OK.value());
        response.put("token", token);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
