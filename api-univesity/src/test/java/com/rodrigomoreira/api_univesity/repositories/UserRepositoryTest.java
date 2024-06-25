package com.rodrigomoreira.api_univesity.repositories;

import static com.rodrigomoreira.api_univesity.commons.UserConstants.INVALID_USER;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.rodrigomoreira.api_univesity.domain.users.User;

@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    void afterEach(){
        USER_WITH_ID.setId(null);
    }
    
    @Test
    void createUser_WithValidData_ReturnsUser(){
        User user = userRepository.save(USER_WITH_ID);
        User newUser = testEntityManager.find(User.class, user.getId());
        
        assertThat(newUser).isNotNull();
        assertThat(newUser).isEqualTo(user);
    }

    @Test
    void createUser_WithInvalidData_TrrowsException() {
        User emptyUser = new User();
        
        assertThatThrownBy(() -> userRepository.save(emptyUser)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> userRepository.save(INVALID_USER)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void createUser_WithExistingDocument_ThrowsException(){
        User user = testEntityManager.persistAndFlush(USER_WITH_ID);
        testEntityManager.detach(user);
        user.setId(null);

        assertThatThrownBy(() -> userRepository.save(user))
            .isInstanceOf(RuntimeException.class);

    }

}
