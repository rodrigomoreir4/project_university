package com.rodrigomoreira.api_univesity.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.infra.UpdateRequest;
import com.rodrigomoreira.api_univesity.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/document") // ex: localhost:8080/users/document?document=12345678910
    public User getUser(@RequestParam String document) throws Exception{
        return userService.findUserByDocument(document);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/addcourse")
    public ResponseEntity<User> addCourse(@RequestBody UpdateRequest update) throws Exception{
        User user = userService.addCourse(update.getDocument(), update.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/removecourse")
    public ResponseEntity<User> removeCourse(@RequestBody UpdateRequest update) throws Exception {
        User user = userService.removeCourse(update.getDocument(), update.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws Exception{
        userService.deleteUserById(id);
        return ResponseEntity.ok("User deleted successfully");
    }
    
}
