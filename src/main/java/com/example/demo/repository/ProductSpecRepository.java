package com.example.demo.repository;

import com.example.demo.entity.ProductSpecs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSpecRepository extends JpaRepository<ProductSpecs, Long> {
}
