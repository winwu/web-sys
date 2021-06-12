package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

// @Component 這個 annotation 是必要的，表示這個 class 要給 Spring IoC 管理
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

    @Autowired
    UserRepository userRepository;


    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetResourceObject, Object permission) {
        // 一般的 targetDomain 判斷
        logger.info("hasPermission 第一種 targetResourceObject [{}] ; permission [{}]", targetResourceObject, permission);

        boolean isGranted = false;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        try {
            // get permission by cache
            // List<Permission> permissions = (List<Permission>) redisTemplate.opsForValue().get("permission::" + username);
            List<String> permissionNames = redisTemplate.opsForList().range("permissionsNames::" + userDetails.getUsername(), 0, -1);
            if (permissionNames.size() > 0) {
                logger.info("get permission in cached");
                if (permissionNames.contains(permission)) {
                    isGranted = true;
                }
            } else {
                logger.info("get permission in Database");
                User user = userRepository.findByUsername(userDetails.getUsername());
                isGranted = Arrays.stream(user.getPermissionsNames()).anyMatch(permission::equals);
            }
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
