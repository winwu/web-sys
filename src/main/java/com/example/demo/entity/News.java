package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Entity
@Table(name = "news")
@Data

public class News extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    @NotEmpty(message = "Title can't be null")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Content can't be null")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String image;

    public String getImage() {
        if (this.image != null) {
            return "TODO append folder path" + this.image;
        } else {
            return null;
        }
    }
}
