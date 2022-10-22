package order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import config.Config;
import io.restassured.response.ValidatableResponse;
import user.User;
import user.UserSpec;
import java.util.ArrayList;

public class OrderSpec {

    private static final String INGREDIENTS = "/ingredients";
    private static final String ORDERS = "/orders";
    private static String jsonString;
    static OrderSpec orderSpec;
    static UserSpec userSpec;
    static ObjectMapper mapper = new ObjectMapper();

    //получение данных об ингредиентах
    @Step("Getting a response to a request for data about ingredients of /api/ingredients")
    public static ValidatableResponse getResponseRequestIngredients() throws JsonProcessingException {
        return given().log().all()
                .baseUri(Config.BASE_URL)
                .get(INGREDIENTS)
                .then().log().all()
                .statusCode(200);
    }

    //создание заказа
    @Step("Getting of the response when creating a order of /api/orders")
    public static ValidatableResponse getResponseCreateOrder(Order order, String userAccessToken, int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(order);
        return given().log().all()
                .headers("Authorization", userAccessToken, "Content-Type", "application/json")
                .baseUri(Config.BASE_URL)
                .body(jsonString)
                .when()
                .post(ORDERS)
                .then().log().all()
                .statusCode(statusCode);
    }

    //создание списка валидных хешей ингредиентов
    @Step("Сreating a list of valid hashes of ingredients of /api/orders")
    public static ArrayList<String> getCreatedListOfValidHashesOfIngredients() throws JsonProcessingException {
        ArrayList<String> ingredientsHash = new ArrayList<>(orderSpec.getResponseRequestIngredients()
                .extract()
                .path("data._id"));
        return ingredientsHash;
    }

    //создание списка заказов пользователя
    @Step("Creating a orderList of /api/orders")
    public static void createListOfOrders(User user, int numberOfOrders) throws JsonProcessingException {
        ArrayList<String> ingredientsHash = getCreatedListOfValidHashesOfIngredients();                                     // получение списка валидных хешей ингредиентов
        String[] ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};       // массив ингредиентов для заказа
        Order order = new Order(ingredients);
        UserSpec response = userSpec.getResponseUserAuthorization(user, 200);                                     // запрос на авторизацию пользователя
        for (int i = 0; i < numberOfOrders; i++){                                                                           // создание numberOfOrders количества заказов
            orderSpec.getResponseCreateOrder(order, response.accessToken, 200)                                     // запрос на создание заказа
                    .assertThat()
                    .body("order.number",notNullValue());
        }
        userSpec.getResponseLogoutUser(response.refreshToken, 200);                                                // выход из учетной записи пользователя
    }

    //получение списка заказов
    @Step("Getting of a list of orders of - /api/orders")
    public static ValidatableResponse getAnOrderListRequestResponse(String userAccessToken, int statusCode) {
        return given().log().all()
                .header("Authorization", userAccessToken)
                .baseUri(Config.BASE_URL)
                .when()
                .get(ORDERS)
                .then().log().all()
                .statusCode(statusCode);
    }
}
