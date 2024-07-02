package com.rodrigomoreira.api_univesity.repositories;

import static com.rodrigomoreira.api_univesity.commons.UserConstants.INVALID_USER;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.rodrigomoreira.api_univesity.domain.courses.Course;
import com.rodrigomoreira.api_univesity.domain.users.User;

@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void BeforeEach (){

        USER_WITH_ID.getCourses().clear();
        Course course = new Course("Course");
        Course persistedCourse = testEntityManager.persistFlushFind(course);
        USER_WITH_ID.getCourses().add(persistedCourse);
        
    }

    @AfterEach
    void afterEach(){
        USER_WITH_ID.setId(1L);
    }
    
    @Test
    void createUser_WithValidData_ReturnsUser(){
        userRepository.save(USER_WITH_ID);
        User newUser = testEntityManager.find(User.class, USER_WITH_ID.getId());
        
        assertThat(newUser).isNotNull();
        assertThat(newUser).isEqualTo(USER_WITH_ID);
    }

    @Test
    void createUser_WithInvalidData_ThrowsException() {
        User emptyUser = new User();
        
        assertThatThrownBy(() -> userRepository.save(emptyUser)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userRepository.save(INVALID_USER)).isInstanceOf(RuntimeException.class);
    }

    @Test // --------------
    void createUser_WithExistingDocument_ThrowsException(){
        
        User user = testEntityManager.persistFlushFind(USER_WITH_ID);
        testEntityManager.detach(user);
        user.setId(null);

        assertThatThrownBy(() -> userRepository.save(user))
            .isInstanceOf(RuntimeException.class);

    }

    @Test // -----------
    void getUser_ByExistingId_ReturnUser() {
        testEntityManager.persistAndFlush(USER_WITH_ID);

        Optional<User> userOpt = userRepository.findById(USER_WITH_ID.getId());

        assertThat(userOpt).isNotEmpty();
        assertThat(userOpt.get()).isEqualTo(USER_WITH_ID);
    }

    @Test
    void getUser_ByUnexistingId_ReturnsEmpty() {
        
        Optional<User> userOpt = userRepository.findById(1L);
        
        assertThat(userOpt).isEmpty();
    }

    @Test // --------------
    void findAll_WithExistingUsers_ReturnsUsers(){
        testEntityManager.persistAndFlush(USER_WITH_ID);

        List<User> users = userRepository.findAll();
        
        assertThat(users).isNotEmpty();
        assertThat(users).hasSize(1);
        assertThat(users).contains(USER_WITH_ID);
    }

    @Test
    void findAll_WithNoUsers_ReturnEmptyList(){
        List<User> users = userRepository.findAll();

        assertThat(users).isEmpty();
    }

    @Test // -----------
    void removeUser_WithExistingId_RemovesUserFromDatabase() {
        testEntityManager.persistAndFlush(USER_WITH_ID);

        userRepository.deleteById(USER_WITH_ID.getId());

        User removedUser = testEntityManager.find(User.class, USER_WITH_ID.getId());
        assertThat(removedUser).isNull();

    }

}
