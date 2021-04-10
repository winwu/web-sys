package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "product_specs")
@Data
public class ProductSpecs extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "is_published", nullable = true, columnDefinition = "TINYINT(1) default 1")
    private Integer isPublished = 1;

    @Column(columnDefinition = "TEXT")
    private String content;
}
