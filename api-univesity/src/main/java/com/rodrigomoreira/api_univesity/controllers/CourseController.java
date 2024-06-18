package com.rodrigomoreira.api_univesity.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigomoreira.api_univesity.domain.courses.Course;
import com.rodrigomoreira.api_univesity.services.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/name") // ex: localhost:8080/courses/name?name=ADS
    public Course getCourseByName(@RequestParam String name) throws Exception{
        String lowerCaseName = name.toLowerCase();
        return courseService.findCourseByName(lowerCaseName);
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) throws Exception{ 
        return courseService.findCourseById(id);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() throws Exception{
        List<Course> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody @Valid Course course) throws Exception {
        Course newCourse = courseService.createCourse(course);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) throws Exception{
        courseService.deleteCourseById(id);
        return ResponseEntity.noContent().build();
    }
    
}
