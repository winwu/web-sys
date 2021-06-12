package com.example.demo.service;

import com.example.demo.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface UserService {
    public String login(String username, String password);

    public String signup(User user);

    public void delete(String username);

    // public User search(String username);

    public User findByUsername(String username);

    public User me(HttpServletRequest req);

    public Map<String, Object> parse(HttpServletRequest req);

    public String refresh(String username);

}
