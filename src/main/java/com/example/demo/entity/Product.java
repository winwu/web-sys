package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Entity
@Table(name = "products")
@Data

public class Product extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "varchar(30)")
    @NotEmpty(message = "Product's Title can't be null")
    private String title;

    @Column(name = "sub_title", nullable = true, columnDefinition = "varchar(120)")
    private String subTitle;

    @Column(name = "parent_id", columnDefinition = "INT(11)")
    private Integer parentId;

    @Column(name = "is_published", nullable = true, columnDefinition = "TINYINT(1) default 1")
    private Integer isPublished = 1;

    @Column(name = "is_feature", nullable = true, columnDefinition = "TINYINT(1) default 0")
    private Integer isFeature = 0;

    @Column(name = "cover_img")
    private String coverImg;

    @Column(name = "banner_img")
    private String bannerImg;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    @EqualsAndHashCode.Exclude
    private Set<ProductSpecs> specs;
}
