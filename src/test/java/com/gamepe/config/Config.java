package com.gamepe.config;

public class Config {

    // Base URL
    public static final String BASE_URL =
        "https://pre-prod-gamepe-backend-auth-core-apis-36251886398.asia-south1.run.app";

    // Endpoints
    public static final String SIGNUP_ENDPOINT    = "/auth/signup";
    public static final String SEND_OTP_ENDPOINT  = "/auth/otp/send";
    public static final String VERIFY_OTP_ENDPOINT = "/auth/otp/verify";
    public static final String RESEND_OTP_ENDPOINT = "/auth/otp/resend";

    // Test Data
    public static final String VALID_MOBILE     = "9876543210";
    public static final String CORRECT_OTP      = "0000";
    public static final String WRONG_OTP        = "1234";
    public static final String SHORT_MOBILE     = "98765";
    public static final String ALPHA_MOBILE     = "98765ABCDE";
    public static final String UNREGISTERED_MOBILE = "0000000000";
    public static final String NON_NUMERIC_OTP  = "ABCD";
    public static final String EMPTY_STRING     = "";
    public static String PLAYER_ID              = "11254690";
    public static String WORKSPACE_ID           = "workspace-123";
    public static String AUTH_TOKEN             = "";
    public static final int    APP_ID            = 2;
    public static final String OTP_HASH         = "";
    public static final String WEB_LOGIN        = "";
    public static final String DEVICE_ID        = "30685769ebafdd1a";
    // Required Headers (from Postman curl)
    public static final String HEADER_PLATFORM          = "Android";
    public static final String HEADER_WORKSPACE_ID      = "62642";
    public static final String HEADER_X_APP_SECRET_KEY  = "2";
    public static final String HEADER_X_APP_VERSION     = "300";
    public static final String HEADER_X_APP_BUILD_TYPE  = "debug";
    public static final String HEADER_X_APP_TYPE        = "playStore";
    public static final String HEADER_ORIGIN            = "https://host-onboarding-preprod.web.app";
    public static final String HEADER_REFERER           = "https://host-onboarding-preprod.web.app/";
    public static final String HEADER_USER_AGENT        = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36";
}
