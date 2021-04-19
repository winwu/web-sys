package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "product_specs")
@Data
public class ProductSpecs extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    @Column(name = "is_published", nullable = true, columnDefinition = "TINYINT(1) default 1")
    private Integer isPublished = 1;

    @Column(columnDefinition = "TEXT")
    private String content;

//    @Override
//    public String toString() {
//        return "ProductSpec{" +
//                "id=" + id +
//                ", product.getId()='" + product.getId() + '\'' +
//                ", content=" + content +
//                '}';
//    }
}
