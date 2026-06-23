package com.gamepe.tests;

import com.gamepe.base.BaseTest;
import com.gamepe.config.Config;
import com.gamepe.utils.TestLogger;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("GamePe Auth API")
@Feature("Sign Up")
public class SignUpTest extends BaseTest {

    private static final Logger log = TestLogger.get(SignUpTest.class);

    // ─────────────────────────────────────────
    // FUNCTIONAL TESTS
    // ─────────────────────────────────────────

    @Test(priority = 1)
    @Story("Functional")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Valid 10-digit mobile number se signup hona chahiye")
    public void tc01_signUp_validMobile_shouldReturn200Or201() {
        Map<String, String> body = new HashMap<>();
        body.put("mobile_number", Config.VALID_MOBILE);

        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SIGNUP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SIGNUP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(200), is(201), is(409)))
            .time(lessThan(3000L))
            .extract().response();

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            try {
                if (response.jsonPath().get("data.player_id") != null) {
                    Config.PLAYER_ID = String.valueOf(response.jsonPath().getInt("data.player_id"));
                    log.info("🎯 Captured PLAYER_ID: {}", Config.PLAYER_ID);
                }
                if (response.jsonPath().get("data.workspace_id") != null) {
                    Config.WORKSPACE_ID = String.valueOf(response.jsonPath().get("data.workspace_id"));
                    log.info("🏢 Captured WORKSPACE_ID: {}", Config.WORKSPACE_ID);
                }
            } catch (Exception e) {
                log.warn("⚠️ Failed to extract player_id or workspace_id: {}", e.getMessage());
            }
        }

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
        log.info("📝 Note: 409 = user already exists (acceptable on preprod)");
    }

    // ─────────────────────────────────────────
    // NEGATIVE TESTS
    // ─────────────────────────────────────────

    @Test(priority = 2)
    @Story("Negative")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Empty mobile number reject hona chahiye")
    public void tc02_signUp_emptyMobile_shouldReturn400Or422() {
        Map<String, String> body = new HashMap<>();
        body.put("mobile_number", Config.EMPTY_STRING);

        log.warn("⚠️  Sending EMPTY mobile_number — expecting 400/422");
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SIGNUP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SIGNUP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }

    @Test(priority = 3)
    @Story("Negative")
    @Severity(SeverityLevel.CRITICAL)
    @Description("5-digit short mobile number reject hona chahiye")
    public void tc03_signUp_shortMobile_shouldReturn400Or422() {
        Map<String, String> body = new HashMap<>();
        body.put("mobile_number", Config.SHORT_MOBILE);

        log.warn("⚠️  Sending SHORT mobile '{}' — expecting 400/422", Config.SHORT_MOBILE);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SIGNUP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SIGNUP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }

    @Test(priority = 4)
    @Story("Negative")
    @Severity(SeverityLevel.NORMAL)
    @Description("Mobile number mein letters hone par reject hona chahiye")
    public void tc04_signUp_alphaInMobile_shouldReturn400Or422() {
        Map<String, String> body = new HashMap<>();
        body.put("mobile_number", Config.ALPHA_MOBILE);

        log.warn("⚠️  Sending ALPHA mobile '{}' — expecting 400/422", Config.ALPHA_MOBILE);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SIGNUP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SIGNUP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }

    @Test(priority = 5)
    @Story("Negative")
    @Severity(SeverityLevel.NORMAL)
    @Description("Empty JSON body bhejne par reject hona chahiye")
    public void tc05_signUp_emptyBody_shouldReturn400Or422() {
        log.warn("⚠️  Sending EMPTY body {{}} — expecting 400/422");
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SIGNUP_ENDPOINT, "{}");

        Response response = given()
            .spec(requestSpec)
            .body("{}")
        .when()
            .post(Config.SIGNUP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }
}
