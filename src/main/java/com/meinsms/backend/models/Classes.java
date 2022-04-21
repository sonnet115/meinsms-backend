package com.meinsms.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
