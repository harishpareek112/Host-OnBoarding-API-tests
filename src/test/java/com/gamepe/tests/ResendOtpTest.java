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
@Feature("Resend OTP")
public class ResendOtpTest extends BaseTest {

    private static final Logger log = TestLogger.get(ResendOtpTest.class);

    @Test(priority = 1)
    @Story("Functional")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Registered mobile pe OTP resend hona chahiye")
    public void tc01_resendOtp_validMobile_shouldReturn200Or429() {
        Map<String, String> body = getBaseBody(Config.VALID_MOBILE);

        log.info("🔄 Resending OTP to mobile: {}", Config.VALID_MOBILE);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.RESEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.RESEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(200), is(429)))
            .time(lessThan(3000L))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());

        if (response.statusCode() == 429) {
            log.warn("⚠️  Rate limit hit (429) — too many resend attempts. This is expected behavior.");
        } else {
            log.info("✅ OTP resent successfully");
        }
    }

    @Test(priority = 2)
    @Story("Negative")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Unregistered mobile pe resend nahi hona chahiye")
    public void tc02_resendOtp_unregisteredMobile_shouldReturn400Or404() {
        Map<String, String> body = getBaseBody(Config.UNREGISTERED_MOBILE);

        log.warn("⚠️  Resend to UNREGISTERED mobile: {} — expecting 400/404", Config.UNREGISTERED_MOBILE);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.RESEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.RESEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(404)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }

    @Test(priority = 3)
    @Story("Negative")
    @Severity(SeverityLevel.NORMAL)
    @Description("Empty mobile pe resend nahi hona chahiye")
    public void tc03_resendOtp_emptyMobile_shouldReturn400Or422() {
        Map<String, String> body = getBaseBody(Config.EMPTY_STRING);

        log.warn("⚠️  Resend with EMPTY mobile — expecting 400/422");
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.RESEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.RESEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }
}
