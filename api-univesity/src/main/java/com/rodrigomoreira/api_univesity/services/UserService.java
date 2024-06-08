package com.rodrigomoreira.api_univesity.services;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodrigomoreira.api_univesity.domain.courses.Course;
import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AtomicLong idCounter;

    @Autowired
    private CourseService courseService;

    public User findUserById(Long id) throws Exception{
        return userRepository.findUserById(id).orElseThrow(() -> new Exception("User not found"));
    }

    public User findUserByDocument(String document) throws Exception{
        return userRepository.findUserByDocument(document).orElseThrow(() -> new Exception("User not found"));
    }

    public User createUser(User user){
        user.setId(idCounter.getAndIncrement());
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User addCourse(String document, String name) throws Exception{
        User user = findUserByDocument(document);
        Course course = courseService.findCourseByName(name.toLowerCase());
        user.getCourses().add(course);
        return userRepository.save(user);
    }

    public User removeCourse(String document, String name) throws Exception{
        User user = findUserByDocument(document);
        Course course = courseService.findCourseByName(name.toLowerCase());
        if (user.getCourses().contains(course)){
            user.getCourses().remove(course);
        } else {
            throw new Exception("User does not have this course");
        }
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) throws Exception{
        User user = findUserById(id);
        userRepository.deleteById(user.getId());
    }
    
}
