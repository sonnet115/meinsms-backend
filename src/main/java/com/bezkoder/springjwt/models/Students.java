package com.bezkoder.springjwt.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table
public class Students {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "student_classes",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "classes_id"))
    private Set<Classes> classes;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    User parent;

    @OneToMany(mappedBy = "students")
    List<Rating> ratingList;
}
