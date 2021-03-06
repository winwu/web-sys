package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // boolean existsByUsername(String username);

    boolean existsByUsernameOrEmail(String username, String email);

    User findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

    // handle active status
}
