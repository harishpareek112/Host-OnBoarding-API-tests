package com.gamepe.tests;

import com.gamepe.base.BaseTest;
import com.gamepe.config.Config;
import com.gamepe.utils.TestLogger;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.testng.annotations.Test;

// import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("GamePe Auth API")
@Feature("Send OTP")
public class SendOtpTest extends BaseTest {

    private static final Logger log = TestLogger.get(SendOtpTest.class);

    @Test(priority = 1)
    @Story("Functional")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Registered mobile pe OTP send hona chahiye")
    public void tc01_sendOtp_validMobile_shouldReturn200() {
        Map<String, String> body = getBaseBody(Config.VALID_MOBILE);

        log.info("📱 Sending OTP to registered mobile: {}", Config.VALID_MOBILE);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(200)
            .time(lessThan(3000L))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
        log.info("✅ OTP successfully triggered for mobile: {}", Config.VALID_MOBILE);
    }

    @Test(priority = 2)
    @Story("Negative")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Unregistered mobile pe OTP send nahi hona chahiye")
    public void tc02_sendOtp_unregisteredMobile_shouldReturn400Or404() {
        Map<String, String> body = getBaseBody(Config.UNREGISTERED_MOBILE);

        log.warn("⚠️  Sending OTP to UNREGISTERED mobile: {} — expecting 400/404", Config.UNREGISTERED_MOBILE);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(404)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }

    @Test(priority = 3)
    @Story("Negative")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Empty mobile pe OTP send nahi hona chahiye")
    public void tc03_sendOtp_emptyMobile_shouldReturn400Or422() {
        Map<String, String> body = getBaseBody(Config.EMPTY_STRING);

        log.warn("⚠️  Sending OTP with EMPTY mobile — expecting 400/422");
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }
}
