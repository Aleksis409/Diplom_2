package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserCreateTest {

    private String userAccessToken;
    private boolean userCreateSuccess;

    User user;
    UserSpec userSpec;

    @After //удаление учетной записи пользователя
    public void tearDown() throws Exception {
        if (userCreateSuccess) {
            userSpec.getResponseUserDeleted(userAccessToken, 202);
        }
    }

    @Test //Тест успешного создания учетной записи пользователя
    @DisplayName("Successful user creation of /api/auth/register")
    public void successfulCreateUserTestOk() throws JsonProcessingException {
        user = user.getRandomUser();                                                           //создание пользователя
        UserSpec response = userSpec.getResponseCreateUser(user,200);                 //создание "учетки" пользователя
        userAccessToken = response.accessToken;
        userCreateSuccess = response.success;
        assertThat(userAccessToken, notNullValue());
        assertTrue(userCreateSuccess);
    }

    @Test //Тест неуспешного создания учетной записи пользователя без пароля
    @DisplayName("Unable to create a user without password of /api/auth/register")
    public void failCreateUserWithOutPasswordTestOk() throws JsonProcessingException {
        user = user.getRandomUserWithoutPassword();                                              //создание пользователя без пароля
        UserSpec response = userSpec.getResponseCreateUser(user, 403);                 //создание "учетки" пользователя
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields",response.message);
    }

    @Test //Тест неуспешного создания учетной записи пользователя без имени
    @DisplayName("Unable to create a user without name of /api/auth/register")
    public void failCreateUserWithOutNameTestOk() throws JsonProcessingException {
        user = user.getRandomUserWithoutName();                                                  //создание пользователя без имени
        UserSpec response = userSpec.getResponseCreateUser(user, 403);                 //создание "учетки" пользователя
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields",response.message);
    }

    @Test //Тест неуспешного создания учетной записи пользователя без email
    @DisplayName("Unable to create a user without email of /api/auth/register")
    public void failCreateUserWithOutEmailTestOk() throws JsonProcessingException {
        user = user.getRandomUserWithoutEmail();                                                  //создание пользователя без email
        UserSpec response = userSpec.getResponseCreateUser(user, 403);                  //создание "учетки" пользователя
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields",response.message);
    }

    @Test //Тест неуспешного создания учетной записи пользователя который уже зарегистрирован (с повторяющимся email)
    @DisplayName("Unable to create a user with a duplicate email of /api/auth/register")
    public void failCreateCourierRecurringEmailTestOk() throws JsonProcessingException {
        user = user.getRandomUser();                                                              //создание пользователя
        UserSpec initResponse = userSpec.getResponseCreateUser(user,200);               //создание "учетки" пользователя
        userAccessToken = initResponse.accessToken;
        userCreateSuccess = initResponse.success;
        UserSpec response = userSpec.getResponseCreateUser(user, 403);                  //создание "учетки" пользователя который уже зарегистрирован
        assertFalse(response.success);
        assertEquals("User already exists",response.message);
    }
}