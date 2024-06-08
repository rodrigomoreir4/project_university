package com.rodrigomoreira.api_univesity.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.domain.users.UserType;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    
    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;
    
    @Test
    @DisplayName("Should get user sucessfully from DB")
    void testFindUserByDocumentCase1() {
        String document = "12345678910";
        User user = new User(1L,"Rodrigo", "rodrigo@gmail.com", document, UserType.TEACHER);
        this.createUser(user);

        Optional<User> result = this.userRepository.findUserByDocument(document);
        
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not get user from DB when user not exists")
    void testFindUserByDocumentCase2() {
        String document = "12345678910";

        Optional<User> result = this.userRepository.findUserByDocument(document);
        
        assertThat(result.isEmpty()).isTrue();
    }

    private User createUser(User user){
        this.entityManager.persist(user);
        return user;
    }
        
}
