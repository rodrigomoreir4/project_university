package com.rodrigomoreira.api_univesity.domain.users;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rodrigomoreira.api_univesity.domain.courses.Course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="users")
@Table(name="users")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class User {

    @Id
    private Long id;

    private String name;

    @Column(unique=true)
    private String email;

    @Column(unique=true)
    private String document;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToMany
    @JoinTable(name = "tb_user_course", 
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "course_id"))
    @JsonIgnoreProperties("users")
    private Set<Course> courses = new HashSet<>();

    public User(Long id, String name, String email, String document, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.document = document;
        this.userType = userType;
    }

    public User(String name, String email, String document, UserType userType) {
        this.name = name;
        this.email = email;
        this.document = document;
    }

    public User(String name, String email, String document) {
        this.name = name;
        this.email = email;
        this.document = document;
    }
    
}