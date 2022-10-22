package user;

import static io.restassured.RestAssured.given;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import config.Config;

public class UserSpec {
    private static final String ROOT = "/auth/register";
    private static final String LOGIN = "/auth/login";
    private static final String LOGOUT = "/auth/logout";
    private static final String UPDATE= "/auth/user";
    private static String jsonString;
    public static String message;
    public static boolean success;
    public static String accessToken;
    public static String refreshToken;

    public UserSpec (boolean success, String message, String accessToken, String refreshToken) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    static ObjectMapper mapper = new ObjectMapper();

    //создание учетной записи пользователя
    @Step("Getting of the response when creating a user of /api/auth/register")
    public static UserSpec getResponseCreateUser(User user, int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(user);
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(Config.BASE_URL)
                .body(jsonString)
                .when()
                .post(ROOT)
                .then().log().all()
                .statusCode(statusCode)
                .extract()
                .response();
        success = response.path("success");
        message = response.path("message");
        accessToken = response.path("accessToken");
        refreshToken = response.path("refreshToken");
        return new UserSpec(success, message, accessToken, refreshToken);
    }

    //авторизация учетной записи пользователя
    @Step("Getting of the response when the user is authorized of /api/auth/login")
    public static UserSpec getResponseUserAuthorization (User user, int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(user);
        Response response = given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(Config.BASE_URL)
                .body(jsonString)
                .when()
                .post(LOGIN)
                .then().log().all()
                .statusCode(statusCode).extract()
                .response();
        success = response.path("success");
        message = response.path("message");
        accessToken = response.path("accessToken");
        refreshToken = response.path("refreshToken");
        return new UserSpec(success, message, accessToken, refreshToken);
    }

    //выход из учетной записи пользователя
    @Step("Getting a response when logging out of a user account of /api/auth/logout")
    public static ValidatableResponse getResponseLogoutUser(String userRefreshToken, int statusCode) {
        jsonString = "{\"token\": \"" + userRefreshToken + "\"}";
        return given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(Config.BASE_URL)
                .body(jsonString)
                .when()
                .post(LOGOUT)
                .then().log().all()
                .statusCode(statusCode);
    }

    //удаление пользователя
    @Step("Getting a response when a user account is deleted of /api/auth/user")
    public static ValidatableResponse getResponseUserDeleted(String userAccessToken, int statusCode) {
        return given().log().all()
                .header("Authorization", userAccessToken)
                .baseUri(Config.BASE_URL)
                .when()
                .delete(UPDATE)
                .then().log().all()
                .statusCode(statusCode);
    }

    //обновление данных пользователя
    @Step("Getting a response update user data of - /api/auth/user")
    public static ValidatableResponse getResponseUpdateUserData(User user, String userAccessToken, int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(user);
        return given().log().all()
                .headers("Authorization", userAccessToken, "Content-Type", "application/json")
                .baseUri(Config.BASE_URL)
                .body(jsonString)
                .when()
                .patch(UPDATE)
                .then().log().all()
                .statusCode(statusCode);
    }
}
