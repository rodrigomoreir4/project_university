package com.rodrigomoreira.api_univesity.services;

import static com.rodrigomoreira.api_univesity.commons.UserConstants.INVALID_USER;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITHOUT_ID;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AtomicLong idCounter;

    @Test
    void createUser_WithValidData_ReturnsUser() {
        when(userRepository.save(USER_WITHOUT_ID)).thenReturn(USER_WITHOUT_ID);
        User user = userService.createUser(USER_WITHOUT_ID);

        assertThat(user).isEqualTo(USER_WITHOUT_ID);
    }

    @Test
    void createUser_WithInvalidData_ThrowsException() {
        when(userRepository.save(INVALID_USER)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> userService.createUser(INVALID_USER))
                                    .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getUser_ByExistingId_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER_WITH_ID));

        Optional<User> user = userService.getUser(1L);

        assertThat(user).isNotEmpty();
        assertThat(user.get()).isEqualTo(USER_WITH_ID);
    }

    @Test
    void getUser_ByUnexistingId_ReturnsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> user = userService.getUser(1L);

        assertThat(user).isEmpty();
    }

    @Test
    void getUser_ByExistingName_ReturnsUser() throws Exception {
        when(userService.findUserByDocument(USER_WITH_ID.getDocument())).thenReturn(Optional.of(USER_WITH_ID));

        Optional<User> user = userService.findUserByDocument(USER_WITH_ID.getDocument());

        assertThat(user).isNotEmpty();
        assertThat(user.get()).isEqualTo(USER_WITH_ID);
    }

    @Test
    void getUser_ByUnexistingName_ReturnsEmpty() throws Exception{
        when(userService.findUserByDocument(USER_WITH_ID.getDocument())).thenReturn(Optional.empty());

        Optional<User> user = userService.findUserByDocument(USER_WITH_ID.getDocument());

        assertThat(user).isEmpty();
    }

    @Test
    void testGetAllUsers_SuccessfulCase(){
        List<User> users = Arrays.asList(USER_WITH_ID);

        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertThat(result).isEqualTo(users);
    }

    @Test
    void testGetAllUsers_ErrorCase(){
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<User> result = userService.getAllUsers();

        assertThat(result).isEmpty();
    }

    @Test
    void removeUser_WithExistingId_doesNotThrowAnyException(){
        assertThatCode(() -> userService.removeUser(1L)).doesNotThrowAnyException();
    }

    @Test
    void removeUser_WithUnexistingId_ThrowsException(){
        doThrow(new RuntimeException()).when(userRepository).deleteById(1L);

        assertThatThrownBy(() -> userService.removeUser(1L)).isInstanceOf(RuntimeException.class);
    }
}
