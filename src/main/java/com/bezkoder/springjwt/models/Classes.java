package com.bezkoder.springjwt.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    private String classCode;

    @ManyToOne
    @JoinColumn(name = "t_id", nullable = false)
    User teacher;
}
