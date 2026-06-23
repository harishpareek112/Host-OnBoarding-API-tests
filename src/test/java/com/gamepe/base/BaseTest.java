package com.gamepe.base;

import com.gamepe.config.Config;
import com.gamepe.utils.TestLogger;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class BaseTest {

    protected RequestSpecification requestSpec;
    private static final Logger log = TestLogger.get(BaseTest.class);

    @BeforeClass
    public void setUp() {
        log.info("╔══════════════════════════════════════════════════╗");
        log.info("║      GamePe Auth API Test Suite - STARTING       ║");
        log.info("╚══════════════════════════════════════════════════╝");
        log.info("🌐 Base URL    : {}", Config.BASE_URL);
        log.info("📱 Mobile No.  : {}", Config.VALID_MOBILE);
        log.info("🔑 Correct OTP : {}", Config.CORRECT_OTP);
        log.info("🚫 Wrong OTP   : {}", Config.WRONG_OTP);
        log.info("──────────────────────────────────────────────────");

        requestSpec = new RequestSpecBuilder()
        .setBaseUri(Config.BASE_URL)
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .addHeader("platform",           Config.HEADER_PLATFORM)
        .addHeader("workspace_id",       Config.HEADER_WORKSPACE_ID)
        .addHeader("x-app-secret-key",   Config.HEADER_X_APP_SECRET_KEY)
        .addHeader("x-app-version",      Config.HEADER_X_APP_VERSION)
        .addHeader("x-app-build-type",   Config.HEADER_X_APP_BUILD_TYPE)
        .addHeader("x-app-type",         Config.HEADER_X_APP_TYPE)
        .addHeader("origin",             Config.HEADER_ORIGIN)
        .addHeader("referer",            Config.HEADER_REFERER)
        .addHeader("user-agent",         Config.HEADER_USER_AGENT)
        .log(LogDetail.ALL)
        .build();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        log.info("✅ RestAssured initialized successfully");
    }

    @BeforeMethod
    public void beforeEachTest(java.lang.reflect.Method method) {
        TestLogger.logTestStart(log, method.getName());
    }

    @AfterMethod
    public void afterEachTest(ITestResult result) {
        String name = result.getMethod().getMethodName();
        if (result.getStatus() == ITestResult.SUCCESS) {
            TestLogger.logTestPass(log, name);
        } else if (result.getStatus() == ITestResult.FAILURE) {
            String reason = result.getThrowable() != null
                    ? result.getThrowable().getMessage()
                    : "Unknown error";
            TestLogger.logTestFail(log, name, reason);
        } else {
            log.warn("⚠️  TEST SKIPPED : {}", name);
        }
    }

    @AfterSuite
    public void tearDown() {
        log.info("╔══════════════════════════════════════════════════╗");
        log.info("║       GamePe Auth API Test Suite - COMPLETE      ║");
        log.info("╚══════════════════════════════════════════════════╝");
    }

    protected java.util.Map<String, String> getBaseBody(String mobileNumber) {
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("player_id", Config.PLAYER_ID);
        body.put("mobile_number", mobileNumber);
        body.put("otp_hash", Config.OTP_HASH);
        body.put("web_login", Config.WEB_LOGIN);
        body.put("device_id", Config.DEVICE_ID);
        return body;
    }
}
