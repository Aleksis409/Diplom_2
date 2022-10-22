package user;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class UserDataUpdateTest {

    private String userAccessToken;

    User user;
    UserSpec userSpec;

    @Before
    public void tearUp() throws Exception {
        user = user.getRandomUser();                                                                   //создание пользователя
        userAccessToken = userSpec.getResponseCreateUser(user,200).accessToken;               //создание учетной записи пользователя
    }

    @After //удаление учетной записи пользователя
    public void tearDown() throws Exception {
        userSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test //Тест успешного изменения данных (пароля) авторизованного пользователя
    @DisplayName("Successfully changing the password of the authorized user of /api/auth/user")
    public void changePasswordOfTheAuthorizationUserTestOk() throws JsonProcessingException {
        User сreatedUser = new User(user.getEmail(), user.getPassword());
        userSpec.getResponseUserAuthorization(сreatedUser, 200);                              //авторизация пользователя
        String updatedPassword = "New" + user.getPassword();                                            //изменение пароля пользователя
        User updatedUser = new User(user.getEmail(), updatedPassword, user.getName());
        userSpec.getResponseUpdateUserData(updatedUser, userAccessToken, 200);                //изменение данных пользователя
        userAccessToken = userSpec.getResponseUserAuthorization(updatedUser, 200).accessToken;//авторизация с измененным паролем
        assertThat(userAccessToken, notNullValue());
    }

    @Test //Тест успешного изменения данных (имени) авторизованного пользователя
    @DisplayName("Successfully changing the name of the authorized user of /api/auth/user")
    public void successfullChangeNameOfTheAuthorizationUserTestOk() throws JsonProcessingException {
        User сreatedUser = new User(user.getEmail(), user.getPassword());
        userAccessToken = userSpec.getResponseUserAuthorization(сreatedUser, 200).accessToken;//авторизация пользователя
        String updatedName = "New" + user.getName();                                                    //изменение имени пользователя
        User updatedUser = new User(user.getEmail(), user.getPassword(), updatedName);
        userSpec.getResponseUpdateUserData(updatedUser, userAccessToken, 200)                 //изменение данных пользователя
                .body("user.name",equalTo(updatedName));
    }

    @Test //Тест успешного изменения данных (email) авторизованного пользователя
    @DisplayName("Successfully changing the email of the authorized user of /api/auth/user")
    public void successfullChangeEmailOfTheAuthorizationUserTestOk() throws JsonProcessingException {
        User сreatedUser = new User(user.getEmail(), user.getPassword());
        userAccessToken = userSpec.getResponseUserAuthorization(сreatedUser, 200).accessToken;//авторизация пользователя
        String updatedEmail = "New" + user.getEmail();                                                 //изменение email пользователя
        User updatedUser = new User(updatedEmail, user.getPassword(), user.getName());
        userSpec.getResponseUpdateUserData(updatedUser, userAccessToken, 200)                 //изменение данных пользователя
                .body("user.email",equalTo(updatedEmail.toLowerCase()));
    }

    @Test //Тест неуспешного изменения данных (пароля) неавторизованного пользователя
    @DisplayName("Unsuccessfully changing the password of the unauthorized user of /api/auth/user")
    public void failChangePasswordOfTheUnauthorizationUserTestOk() throws JsonProcessingException {
        String updatedPassword = "New" + user.getPassword();                                            //изменение пароля пользователя
        User updatedUser = new User(user.getEmail(), updatedPassword, user.getName());
        userSpec.getResponseUpdateUserData(updatedUser, "", 401)                 //изменение данных пользователя
                .body("message",equalTo("You should be authorised"));
    }

    @Test //Тест неуспешного изменения данных (имени) неавторизованного пользователя
    @DisplayName("Unsuccessfully changing the name of the unauthorized user of /api/auth/user")
    public void failChangeNameOfTheUnauthorizationUserTestOk() throws JsonProcessingException {
        String updatedName = "New" + user.getName();                                                   //изменение имени пользователя
        User updatedUser = new User(user.getEmail(), user.getPassword(), updatedName);
        userSpec.getResponseUpdateUserData(updatedUser, "", 401)                 //изменение данных пользователя
                .body("message",equalTo("You should be authorised"));
    }

    @Test //Тест неуспешного изменения данных (email) неавторизованного пользователя
    @DisplayName("Unsuccessfully changing the email of the unauthorized user of /api/auth/user")
    public void failChangeEmailOfTheUnauthorizationUserTestOk() throws JsonProcessingException {
        String updatedEmail = "New" + user.getEmail();                                                 //изменение email пользователя
        User updatedUser = new User(updatedEmail, user.getPassword(), user.getName());
        userSpec.getResponseUpdateUserData(updatedUser, "", 401)                //изменение данных пользователя
                .body("message",equalTo("You should be authorised"));
    }
}