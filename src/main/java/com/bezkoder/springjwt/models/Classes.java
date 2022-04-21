package com.bezkoder.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;

    private String classCode;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "t_id", nullable = false)
    User teacher;

    @ManyToMany(mappedBy = "classes")
    private Set<Students> students;
}
