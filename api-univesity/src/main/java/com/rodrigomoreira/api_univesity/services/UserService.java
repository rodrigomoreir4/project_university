package com.rodrigomoreira.api_univesity.services;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AtomicLong idCounter;

    public User createUser(User user){
        user.setId(idCounter.getAndIncrement());
        return userRepository.save(user);
    }

    public Optional<User> getUser(Long id){
        return userRepository.findById(id);
    }

    public Optional<User> findUserByDocument(String document) throws Exception{
        return userRepository.findUserByDocument(document);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void removeUser(Long id){
        userRepository.deleteById(id);
    }
    
}
