package ApiTest;

import com.google.gson.Gson;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiBookingCsvTest {

    public static String lerArquivoJson(String arquivoJson) throws IOException {
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }

    String ct = "application/json";
    String uriAuth = "https://restful-booker.herokuapp.com/auth";
    String uriBasic = "https://restful-booker.herokuapp.com/booking";
    public static String token;
    public static int idBooking;

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


    @ParameterizedTest
    @CsvFileSource(resources = "/csv/createBookings2.csv", numLinesToSkip = 1, delimiter = ',')
    @Order(2)
    public void testeCreateBooking(String firstname,
                                   String lastname,
                                   int totalprice,
                                   boolean depositpaid,
                                   String checkin,
                                   String checkout,
                                   String additionalneeds){

        Bookings bookings = new Bookings();

        bookings.firstname = firstname;
        bookings.lastname = lastname;
        bookings.totalprice = totalprice;
        bookings.depositpaid = depositpaid;
        bookings.bookingdates = new Bookings.Bookingdates(checkin, checkout);
        bookings.additionalneeds = additionalneeds;

        Gson gson = new Gson();
        String jsonBody = gson.toJson(bookings);


      Response response = (Response) given()
                .contentType(ct)
                .accept(ct)
                .log().all()
                .body(jsonBody)
        .when()
                .post(uriBasic)
        .then()
                .statusCode(200)
                .log().all()
                .body("booking.firstname", is(firstname))
                .body("booking.lastname", is(lastname))
                .body("booking.totalprice", is(totalprice))
                .extract();
            idBooking = response.jsonPath().getInt("bookingid");
    }

    /*
    @Test
    @Order(6)
    public void testeGetBooking(){

        given()
                .contentType(ct)
                .log().all()
        .when()
                .get(uriBasic + "/" + idBooking)
        .then()
                .statusCode(200)
                .log().all()
                .body("firstname", is(firstname))
                .body("lastname", is("Anthony"))
                .body("totalprice", is(250))

        ;
    }
    */

    @Test
    @Order(3)
    public void testeDeleteBooking(){

        given()
                .contentType(ct)
                .header("Cookie","token=" + token)
                .log().all()
                .when()
                .delete(uriBasic + "/" + idBooking)
                .then()
                .statusCode(201)
                .body(is("Created"))
        ;
    }
}
