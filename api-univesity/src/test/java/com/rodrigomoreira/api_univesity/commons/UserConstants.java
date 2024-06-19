package com.rodrigomoreira.api_univesity.commons;

import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.domain.users.UserType;

public class UserConstants {
    public static final User USER_WITH_ID = new User(1L,"Rodrigo", "rodrigo@gmail.com", "12345678910", UserType.TEACHER);
    public static final User USER_WITHOUT_ID = new User("Rodrigo", "rodrigo@gmail.com", "12345678910", UserType.TEACHER);
    public static final User INVALID_USER = new User("", "", "");
}
