package com.rodrigomoreira.api_univesity.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodrigomoreira.api_univesity.domain.courses.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findCourseById(Long id);
    Optional<Course> findCourseByName(String name);
    
}
