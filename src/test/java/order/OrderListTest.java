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
import static org.junit.Assert.assertEquals;

public class OrderListTest {
    private String userAccessToken;
    private int numberOfOrders;

    User user;
    UserSpec userSpec;
    OrderSpec orderSpec;

    @Before
    public void tearUp() throws Exception {
        user = user.getRandomUser();                                                              //создание пользователя
        userAccessToken = userSpec.getResponseCreateUser(user,200).accessToken;         //создание учетной записи пользователя
        numberOfOrders = 4;                                                                       //количество заказов пользователя
        orderSpec.createListOfOrders(user, numberOfOrders);                                       //создание списка заказов пользователя
    }

    @After //удаление учетной записи пользователя
    public void tearDown() throws Exception {
        userSpec.getResponseUserDeleted(userAccessToken, 202);
    }

    @Test //Тест успешного получения списка заказов авторизованного пользователя:
    @DisplayName("Successful receipt of a list of orders from a authorized user of /orders")
    public void successfulGetOfOrdersListFromAuthorizedUserTestOk() throws JsonProcessingException {
        userAccessToken = userSpec.getResponseUserAuthorization(user, 200).accessToken;  //авторизацию пользователя
        ArrayList<Integer> orderNumber = new ArrayList<>(orderSpec.getAnOrderListRequestResponse(userAccessToken, 200) //получения списка заказов пользователя
                .extract()
                .path("orders.number"));
        assertEquals(numberOfOrders, orderNumber.size());
    }

    @Test //Тест неуспешного получения списка заказов неавторизованного пользователя:
    @DisplayName("Fail receipt of a list of orders from a unauthorized user of /orders")
    public void failGetOfOrdersListFromUnauthorizedUserTestOk() throws JsonProcessingException {
        orderSpec.getAnOrderListRequestResponse("", 401)                    //получения списка заказов пользователя
                .body("message",equalTo("You should be authorised"));
    }
}
