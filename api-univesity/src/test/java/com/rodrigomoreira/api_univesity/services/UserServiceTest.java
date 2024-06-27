package com.rodrigomoreira.api_univesity.services;

import static com.rodrigomoreira.api_univesity.commons.CourseConstants.COURSE;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.INVALID_USER;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITHOUT_ID;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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

    @Mock
    private CourseService courseService;

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

        User user = userService.getUser(1L);

        assertThat(user).isEqualTo(USER_WITH_ID);
    }

    @Test
    void getUser_ByUnexistingId_ReturnsEmpty() {
        when(userRepository.findById(USER_WITH_ID.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(USER_WITH_ID.getId()))
                .isInstanceOf(Exception.class)
                .hasMessage("User not found");
    }

    @Test
    void getUser_ByExistingName_ReturnsUser() throws Exception {
        when(userRepository.findUserByDocument(USER_WITH_ID.getDocument())).thenReturn(Optional.of(USER_WITH_ID));

        User user = userService.findUserByDocument(USER_WITH_ID.getDocument());

        assertThat(user).isEqualTo(USER_WITH_ID);
    }

    @Test
    void getUser_ByUnexistingName_ReturnsEmpty() throws Exception{
        when(userRepository.findUserByDocument(USER_WITH_ID.getDocument())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserByDocument(USER_WITH_ID.getDocument()))
                .isInstanceOf(Exception.class)
                .hasMessage("User not found");
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
        assertThatThrownBy(() -> userService.getAllUsers())
                .isInstanceOf(Exception.class)
                .hasMessage("No users found");
    }

    @Test
    void testAddCourse_SuccessfulCase() throws Exception{
        when(userRepository.findUserByDocument(anyString())).thenReturn(Optional.of(USER_WITH_ID));
        when(courseService.findCourseByName(anyString())).thenReturn(COURSE);
        when(userRepository.save(USER_WITH_ID)).thenReturn(USER_WITH_ID);

        User updateUser = userService.addCourse(USER_WITH_ID.getDocument(), COURSE.getName());

        verify(userRepository).findUserByDocument(USER_WITH_ID.getDocument());
        verify(courseService).findCourseByName(COURSE.getName());
        verify(userRepository).save(USER_WITH_ID);

        assert updateUser.getCourses().contains(COURSE);
    }

    @Test
    void testRemoveCourse_SuccessfulCase() throws Exception {
        USER_WITH_ID.getCourses().add(COURSE);

        when(userRepository.findUserByDocument(anyString())).thenReturn(Optional.of(USER_WITH_ID));
        when(courseService.findCourseByName(anyString())).thenReturn(COURSE);
        when(userRepository.save(USER_WITH_ID)).thenReturn(USER_WITH_ID);

        User updateUser = userService.removeCourse(USER_WITH_ID.getDocument(), COURSE.getName());

        assertFalse(updateUser.getCourses().contains(COURSE));
    }

    @Test
    void testUpdateUser__ErrorCase1() throws Exception{
        when(userRepository.findUserByDocument(USER_WITH_ID.getDocument())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addCourse(USER_WITH_ID.getDocument(), COURSE.getName()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void testUpdateUser__ErrorCase2() throws Exception{ 
        when(userRepository.findUserByDocument(USER_WITH_ID.getDocument())).thenReturn(Optional.of(USER_WITH_ID));
        when(courseService.findCourseByName(anyString())).thenThrow(new RuntimeException("Course not found"));

        assertThatThrownBy(() -> userService.addCourse(USER_WITH_ID.getDocument(), COURSE.getName()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found");
    }

    @Test
    void testUpdateUser__ErrorCase3() throws Exception{
        assertThatThrownBy(() -> userService.addCourse(USER_WITH_ID.getDocument(), COURSE.getName())).isInstanceOf(RuntimeException.class);
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
