package ApiTest;

import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiBookingTest {

    String ct = "application/json";

    String uriAuth = "https://restful-booker.herokuapp.com/auth";

    String uriBasic = "https://restful-booker.herokuapp.com/booking";

    public static String lerArquivoJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }
    public static String token;

    public static int idBooking1;

@Test
@Order(1)
public void testeCreateToken() throws IOException {

        String jsonBody = lerArquivoJson("src/test/resources/json/authCredencials.json");

   Response response = (Response) given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
    .when()
            .post(uriAuth)
    .then()
            .statusCode(200)
            .body("token", hasLength(15))
            .extract()
   ;
    token = response.jsonPath().getString("token");
    System.out.println("Seu token é: " + token);
}

    @Test
    @Order(2)
    public void testeGetBookingIds(){

    given()
            .contentType(ct)
            .log().all()
    .when()
            .get(uriBasic)
    .then()
            .statusCode(200)
            .log().all()
    ;
}
    @Test
    @Order(6)
    public void testeGetBooking(){

    given()
            .contentType(ct)
            .log().all()
    .when()
            .get(uriBasic + "/" + idBooking1)
    .then()
            .statusCode(200)
            .log().all()
            .body("firstname", is("Carmelo"))
            .body("lastname", is("Anthony"))
            .body("totalprice", is(250))

    ;
}

    @Test
    @Order(3)
    public void testeCreateBooking() throws IOException {

    String jsonBody = lerArquivoJson("src/test/resources/json/createBooking.json");

       Response response = (Response) given()
                .contentType(ct)
                .log().all()
                .body(jsonBody)
        .when()
                .post(uriBasic)
        .then()
                .statusCode(200)
                .log().all()
                .body("booking.firstname", is("Vince"))
                .body("booking.lastname", is("Carter"))
                .body("booking.totalprice", is(238))
                .extract();
            idBooking1 = response.jsonPath().getInt("bookingid");
    }

    @Test
    @Order(5)
    public void testeUpdateBooking() throws IOException {

        String jsonBody = lerArquivoJson("src/test/resources/json/UpdateBooking.json");

        given()
                .contentType(ct)
                .accept(ct)
                .header("Cookie","token=" + token)
                .log().all()
                .body(jsonBody)
        .when()
                .put(uriBasic + "/" + idBooking1)
        .then()
                .statusCode(200)
                .log().all()
                .body("firstname", is("Carmelo"))
                .body("lastname", is("Anthony"))
                .body("totalprice", is(250))
                .body("depositpaid", equalTo(true))
        ;
    }

    @Test
    @Order(4)
    public void testePartialUpdateBooking() throws IOException {

    String jsonBody = lerArquivoJson("src/test/resources/json/PartialUpdateBooking.json");

        given()
                .contentType(ct)
                .accept(ct)
                .header("Cookie","token=" + token)
                .log().all()
                .body(jsonBody)
        .when()
                .patch(uriBasic + "/" + idBooking1)
        .then()
                .statusCode(200)
                .log().all()
                .body("bookingdates.checkin", is("2023-12-21"))
                .body("totalprice", is(135))
        ;
    }

    @Test
    @Order(7)
    public void testeDeleteBooking(){

        given()
                .contentType(ct)
                .header("Cookie","token=" + token)
                .log().all()
        .when()
                .delete(uriBasic + "/" + idBooking1)
        .then()
                .statusCode(201)
                .body(is("Created"))
        ;
    }
}
