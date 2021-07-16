package com.example.demo.service;

import com.example.demo.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {
    String login(String username, String password);

    String signup(User user);

    void delete(String username);

    // public User search(String username);

    User findByUsername(String username);

    User me(HttpServletRequest req);

    Map<String, Object> parse(HttpServletRequest req);

    String refresh(String username);

}
