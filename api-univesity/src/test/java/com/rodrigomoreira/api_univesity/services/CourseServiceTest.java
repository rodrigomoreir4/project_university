package com.rodrigomoreira.api_univesity.services;

import static com.rodrigomoreira.api_univesity.commons.CourseConstants.COURSE;
import static com.rodrigomoreira.api_univesity.commons.CourseConstants.INVALID_COURSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigomoreira.api_univesity.domain.courses.Course;
import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.repositories.CourseRepository;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Test
    void testCreateCourse_SuccessfulCase() throws Exception{
        when(courseRepository.save(COURSE)).thenReturn(COURSE);
        Course course = courseService.createCourse(COURSE);
        assertThat(course).isEqualTo(COURSE);
    }

    @Test
    void testCreateCourse_ErrorCase(){
        assertThatThrownBy(() -> courseService.createCourse(INVALID_COURSE))
            .isInstanceOf(Exception.class)
            .hasMessage("The course must have a name");
    }

    @Test
    void testFindCourseById_SuccessfulCase() throws Exception{
        when(courseRepository.findCourseById(1L)).thenReturn(Optional.of(COURSE));
        Course course = courseService.findCourseById(1L);
        assertThat(course).isEqualTo(COURSE);
    }

    @Test
    void testFindCourseById_ErrorCase() throws Exception{
        when(courseRepository.findCourseById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> courseService.findCourseById(1L))
            .isInstanceOf(Exception.class)
            .hasMessage("Course not found");
    }

    @Test
    void testFindCourseByName_SuccessfulCase() throws Exception{
        when(courseRepository.findCourseByName("name")).thenReturn(Optional.of(COURSE));
        Course course = courseService.findCourseByName("name");
        assertThat(course).isEqualTo(COURSE);
    }

    @Test
    void testFindCourseByName_ErrorCase() throws Exception{
        when(courseRepository.findCourseByName("name")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> courseService.findCourseByName("name"))
            .isInstanceOf(Exception.class)
            .hasMessage("Course not found");
    }
    
    @Test
    void testGetAllCourses_SuccessfulCase() throws Exception{
        List <Course> courses = Arrays.asList(COURSE);
        when(courseRepository.findAll()).thenReturn(courses);
        List<Course> result = courseService.getAllCourses();
        assertThat(result).isEqualTo(courses);
    }

    @Test
    void testGetAllCourses_ErrorCase() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> courseService.getAllCourses())
            .isInstanceOf(Exception.class)
            .hasMessage("No courses found");
    }

    @Test
    void testDeleteCourseById_SuccessfulCase() throws Exception{
        testFindCourseById_SuccessfulCase();
        doNothing().when(courseRepository).deleteById(any());

        User user1 = new User(1L, "User 1", "user1@email.com", "12345678910", com.rodrigomoreira.api_univesity.domain.users.UserType.TEACHER);
        User user2 = new User(2L, "User 2", "user2@email.com", "12345678911", com.rodrigomoreira.api_univesity.domain.users.UserType.STUDENT);
        COURSE.getUsers().add(user1);
        COURSE.getUsers().add(user2);
        COURSE.setId(1L);

        courseService.deleteCourseById(1L);

        verify(courseRepository).deleteById(1L);

        assertThat(user1.getCourses()).doesNotContain(COURSE);
        assertThat(user2.getCourses()).doesNotContain(COURSE);

    }

    @Test
    void testDeleteCourseById_ErrorCase() {
        when(courseRepository.findCourseById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourseById(1L))
                            .isInstanceOf(Exception.class)
                            .hasMessage("Course not found");

        verify(courseRepository, never()).deleteById(anyLong());
    }
}
