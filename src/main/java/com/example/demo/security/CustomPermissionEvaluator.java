package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;

// @Component 這個 annotation 是必要的，表示這個 class 要給 Spring IoC 管理
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetResourceObject, Object permission) {
        // 一般的 targetDomain 判斷
        logger.info("hasPermission 第一種 targetResourceObject [{}] ; permission [{}]", targetResourceObject, permission);

        boolean isGranted = false;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        try {
            // @TODO get permission by cache
            User user = userRepository.findByUsername(userDetails.getUsername());
            isGranted = Arrays.stream(user.getPermissionsNames()).anyMatch(permission::equals);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return isGranted;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // 使用於 ACL
        // @TODO
        logger.info("hasPermission 第二種 targetId [{}] ; targetType [{}] ; permission [{}]", targetId, targetType, permission);
        return false;
    }
}
