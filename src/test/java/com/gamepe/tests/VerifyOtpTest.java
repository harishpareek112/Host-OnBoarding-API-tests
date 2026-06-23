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
@Feature("Verify OTP")
public class VerifyOtpTest extends BaseTest {

    private static final Logger log = TestLogger.get(VerifyOtpTest.class);

    @Test(priority = 1)
    @Story("Functional")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Correct OTP (0000) verify hona chahiye")
    public void tc01_verifyOtp_correctOtp_shouldReturn200() {
        Response sendResponse = sendOtp(Config.VALID_MOBILE);
        if (sendResponse.statusCode() != 200) {
            log.warn("⚠️ Send OTP returned {}. Continuing to verification attempt.", sendResponse.statusCode());
        }

        Map<String, Object> body = buildVerifyBody(Config.VALID_MOBILE, Config.CORRECT_OTP);
        Response response = verifyOtp(body);

        String message = safeGetMessage(response);
        if (response.statusCode() == 400 && message.toLowerCase().contains("expired")) {
            log.warn("⚠️ OTP expired on first attempt, invoking resend OTP and retrying verification.");
            Response resendResponse = resendOtp(Config.VALID_MOBILE);
            if (resendResponse.statusCode() != 200 && resendResponse.statusCode() != 429) {
                log.warn("⚠️ Resend OTP returned {}", resendResponse.statusCode());
            }
            response = verifyOtp(body);
        }

        response.then()
            .log().all()
            .statusCode(200)
            .time(lessThan(3000L));

        if (response.statusCode() == 200) {
            try {
                if (response.jsonPath().get("data.auth_token") != null) {
                    Config.AUTH_TOKEN = response.jsonPath().getString("data.auth_token");
                    log.info("🔐 Captured AUTH_TOKEN: {}", Config.AUTH_TOKEN);
                }
            } catch (Exception e) {
                log.warn("⚠️ Failed to extract auth_token: {}", e.getMessage());
            }
        }

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
        log.info("✅ Correct OTP verified successfully");
    }

    private Map<String, Object> buildVerifyBody(String mobileNumber, String otp) {
        Map<String, Object> body = new HashMap<>();
        body.put("player_id", Config.PLAYER_ID);
        body.put("mobile_number", mobileNumber);
        body.put("client_otp", otp);
        // Match curl: workspaceID (capital D) and allow null
        body.put(
            "workspaceID",
            (Config.WORKSPACE_ID == null || Config.WORKSPACE_ID.isBlank()) ? null : Config.WORKSPACE_ID
        );
        body.put("app_id", Config.APP_ID);
        body.put("device_id", Config.DEVICE_ID);
        return body;
    }

    private Response sendOtp(String mobileNumber) {
        Map<String, String> body = getBaseBody(mobileNumber);
        log.info("📩 Sending OTP to mobile: {}", mobileNumber);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.SEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.SEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
        return response;
    }

    private Response resendOtp(String mobileNumber) {
        Map<String, String> body = getBaseBody(mobileNumber);
        log.info("🔄 Resending OTP to mobile: {}", mobileNumber);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.RESEND_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.RESEND_OTP_ENDPOINT)
        .then()
            .log().all()
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
        return response;
    }

    private Response verifyOtp(Map<String, Object> body) {
        log.info("🔑 Verifying OTP for mobile: {}", body.get("mobile_number"));
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.VERIFY_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.VERIFY_OTP_ENDPOINT)
        .then()
            .log().all()
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
        return response;
    }

    private String safeGetMessage(Response response) {
        try {
            String message = response.jsonPath().getString("message");
            return message == null ? "" : message;
        } catch (Exception e) {
            return "";
        }
    }

    @Test(priority = 2)
    @Story("Functional")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Wrong OTP (1234) reject hona chahiye — SECURITY CRITICAL")
    public void tc02_verifyOtp_wrongOtp_shouldNotReturn200() {
        Map<String, Object> body = new HashMap<>();
        body.put("player_id", Config.PLAYER_ID);
        body.put("mobile_number", Config.VALID_MOBILE);
        body.put("client_otp", Config.WRONG_OTP);
        body.put(
            "workspaceID",
            (Config.WORKSPACE_ID == null || Config.WORKSPACE_ID.isBlank()) ? null : Config.WORKSPACE_ID
        );
        body.put("app_id", Config.APP_ID);
        body.put("device_id", Config.DEVICE_ID);

        log.warn("🔐 SECURITY TEST: Verifying WRONG OTP '{}' — must NOT return 200!", Config.WRONG_OTP);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.VERIFY_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.VERIFY_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(401), is(422)))
            .time(lessThan(3000L))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
        log.info("✅ Wrong OTP correctly rejected with status: {}", response.statusCode());
    }

    @Test(priority = 3)
    @Story("Negative")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Non-numeric OTP (ABCD) reject hona chahiye")
    public void tc03_verifyOtp_nonNumericOtp_shouldReturn400Or422() {
        Map<String, Object> body = new HashMap<>();
        body.put("player_id", Config.PLAYER_ID);
        body.put("mobile_number", Config.VALID_MOBILE);
        body.put("client_otp", Config.NON_NUMERIC_OTP);
        body.put(
            "workspaceID",
            (Config.WORKSPACE_ID == null || Config.WORKSPACE_ID.isBlank()) ? null : Config.WORKSPACE_ID
        );
        body.put("app_id", Config.APP_ID);
        body.put("device_id", Config.DEVICE_ID);

        log.warn("⚠️  Sending NON-NUMERIC OTP '{}' — expecting 400/401/422", Config.NON_NUMERIC_OTP);
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.VERIFY_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.VERIFY_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(401), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }

    @Test(priority = 4)
    @Story("Negative")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Empty OTP reject hona chahiye")
    public void tc04_verifyOtp_emptyOtp_shouldReturn400Or422() {
        Map<String, Object> body = new HashMap<>();
        body.put("player_id", Config.PLAYER_ID);
        body.put("mobile_number", Config.VALID_MOBILE);
        body.put("client_otp", Config.EMPTY_STRING);
        body.put(
            "workspaceID",
            (Config.WORKSPACE_ID == null || Config.WORKSPACE_ID.isBlank()) ? null : Config.WORKSPACE_ID
        );
        body.put("app_id", Config.APP_ID);
        body.put("device_id", Config.DEVICE_ID);

        log.warn("⚠️  Sending EMPTY OTP — expecting 400/401/422");
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.VERIFY_OTP_ENDPOINT, body);

        Response response = given()
            .spec(requestSpec)
            .body(body)
        .when()
            .post(Config.VERIFY_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(401), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }

    @Test(priority = 5)
    @Story("Negative")
    @Severity(SeverityLevel.NORMAL)
    @Description("Empty JSON body verify pe reject hona chahiye")
    public void tc05_verifyOtp_emptyBody_shouldReturn400Or422() {
        log.warn("⚠️  Sending EMPTY body {{}} to verify endpoint — expecting 400/422");
        TestLogger.logRequest(log, "POST", Config.BASE_URL + Config.VERIFY_OTP_ENDPOINT, "{}");

        Response response = given()
            .spec(requestSpec)
            .body("{}")
        .when()
            .post(Config.VERIFY_OTP_ENDPOINT)
        .then()
            .log().all()
            .statusCode(anyOf(is(400), is(422)))
            .extract().response();

        TestLogger.logResponse(log, response.statusCode(), response.time(), response.asString());
    }
}

