package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserLoginTest {

    private boolean userAuthorisationSuccess;
    private String userAccessToken;

    User user;
    UserSpec userSpec;

    @Before //создание учетной записи пользователя
    public void tearUp() throws Exception {
        user = user.getRandomUser();
        userAccessToken = userSpec.getResponseCreateUser(user,200).accessToken;
    }

    @After //удаление учетной записи пользователя
    public void tearDown() throws Exception {
        userSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test //Тест успешной авторизации под существующим пользователем
    @DisplayName("Successful user authorization of /api/auth/login")
    public void successfulAuthorizationUserTestOk() throws JsonProcessingException {
        User сreatedUser = new User(user.getEmail(), user.getPassword());                                    //данные для авторизации существующего пользователя
        UserSpec response = userSpec.getResponseUserAuthorization(сreatedUser, 200);                //авторизация пользователя
        userAccessToken = response.accessToken;
        userAuthorisationSuccess = response.success;
        assertTrue(userAuthorisationSuccess);
    }

    @Test //Тест неуспешной авторизации под существующим пользователем с неверным логином (email)
    @DisplayName("Fail authorization with an invalid email of /api/auth/login")
    public void failAuthorizationUserWithInvalidEmailTestOk() throws JsonProcessingException {
        String invalidEmail = "Invalid" + user.getEmail();
        User сreatedUser = new User(invalidEmail, user.getPassword());                                       //данные для авторизации существующего пользователя
        userAuthorisationSuccess = userSpec.getResponseUserAuthorization(сreatedUser, 401).success; //авторизация пользователя
        assertFalse(userAuthorisationSuccess);
    }

    @Test //Тест неуспешной авторизации под существующим пользователем с неверным паролем
    @DisplayName("Fail authorization with an invalid password of /api/auth/login")
    public void failAuthorizationUserWithInvalidPasswordTestOk() throws JsonProcessingException {
        String invalidPassword = "Invalid" + user.getPassword();
        User сreatedUser = new User(user.getEmail(), invalidPassword);                                         //данные для авторизации существующего пользователя
        userAuthorisationSuccess =  userSpec.getResponseUserAuthorization(сreatedUser, 401).success; //авторизация пользователя
        assertFalse(userAuthorisationSuccess);
    }
}
