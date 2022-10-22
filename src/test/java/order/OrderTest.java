package order;

import user.User;
import user.UserSpec;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {

    private String userAccessToken;
    private ArrayList<String> ingredientsHash;
    private String[] ingredients;

    User user;
    UserSpec userSpec;
    Order order;
    OrderSpec orderSpec;

    @Before
    public void tearUp() throws Exception {
        user = user.getRandomUser();                                                                         //создание пользователя
        userAccessToken = userSpec.getResponseCreateUser(user,200).accessToken;                     //создание учетной записи пользователя
        ingredientsHash = orderSpec.getCreatedListOfValidHashesOfIngredients();                              //создание списка валидных хешей ингредиентов
    }

    @After //удаление учетной записи пользователя
    public void tearDown() throws Exception {
        userSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test //Тест успешного создания заказа с авторизацией с двумя ингредиентами
    @DisplayName("Successful order creation with authorization with two ingredients of /ingredients")
    public void successfulCreateOrderWithAuthorizationAndTwoIngredientsTestOk() throws JsonProcessingException {
        userAccessToken = userSpec.getResponseUserAuthorization(user, 200).accessToken;              //авторизацию пользователя
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};  //массив ингредиентов для заказа
        order = new Order(ingredients);
        orderSpec.getResponseCreateOrder(order, userAccessToken, 200)                                //создание заказа
                .assertThat()
                .body("order.number",notNullValue());
    }

    @Test //Тест неуспешного создания заказа с авторизацией без ингредиентов
    @DisplayName("Fail order creation with authorization without ingredients of /ingredients")
    public void failCreateOrderWithAuthorizationAndZeroIngredientTestOk() throws JsonProcessingException {
        userAccessToken = userSpec.getResponseUserAuthorization(user, 200).accessToken;              //авторизацию пользователя
        order = new Order(ingredients);
        orderSpec.getResponseCreateOrder(order, userAccessToken, 400)                                //создание заказа
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    //тест падает - заказ создается
    @Test //Тест неуспешного создания заказа без авторизации с двумя ингредиентами
    @DisplayName("Fail order creation without authorization with two ingredients of /ingredients")
    public void failCreateOrderWithoutAuthorizationAndTwoIngredientTestOk() throws JsonProcessingException {
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};  //массив ингредиентов для заказа
        order = new Order(ingredients);
        orderSpec.getResponseCreateOrder(order, "", 200)                                //создание заказа
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test //Тест неуспешного создания заказа без авторизации без ингредиентов
    @DisplayName("Fail order creation without authorization without ingredients of /ingredients")
    public void failCreateOrderWithoutAuthorizationAndZeroIngredientTestOk() throws JsonProcessingException {
        order = new Order(ingredients);
        orderSpec.getResponseCreateOrder(order, "", 400)                                //создание заказа
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    //тест падает - возвращаются код ответа 400 и сообщение "One or more ids provided are incorrect"
    @Test //Тест неуспешного создания заказа с авторизацией и неверным хешем ингредиента
    @DisplayName("Fail order creation with authorization with incorrect hash ingredient of /ingredients")
    public void failCreateOrderWithAuthorizationAndIncorrectHashIngredientTestOk() throws JsonProcessingException {
        userAccessToken = userSpec.getResponseUserAuthorization(user, 200).accessToken;              //авторизация пользователя
        ingredients = new String[]{"123456789012345678901234"};                                                //невалидный хеш
        order = new Order(ingredients);
        orderSpec.getResponseCreateOrder(order, userAccessToken, 500)                                 //создание заказа
                .body("message",equalTo("Internal Server Error"));
    }
}