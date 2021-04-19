package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductSpecs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProductSpecRepository extends JpaRepository<ProductSpecs, Long> {

    @Modifying
    @Transactional
    @Query(value = "SELECT * FROM product_specs WHERE product_id = ?1", nativeQuery = true)
    List<ProductSpecs> findByProductId(Long productId);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_specs WHERE product_id = ?1", nativeQuery = true)
    void deleteByProductId(long productId);
}
