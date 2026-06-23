package com.gamepe.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logger utility.
 * Har class mein sirf: Logger log = TestLogger.get(ClassName.class);
 */
public class TestLogger {

    private TestLogger() {}

    public static Logger get(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    // ── Separator lines for readability ──

    public static void logTestStart(Logger log, String testName) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("▶  TEST START  : {}", testName);
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    public static void logTestPass(Logger log, String testName) {
        log.info("✅ TEST PASSED  : {}", testName);
        log.info("─────────────────────────────────────────────────");
    }

    public static void logTestFail(Logger log, String testName, String reason) {
        log.error("❌ TEST FAILED  : {}", testName);
        log.error("   Reason       : {}", reason);
        log.info("─────────────────────────────────────────────────");
    }

    public static void logRequest(Logger log, String method, String url, Object body) {
        log.debug("📤 REQUEST");
        log.debug("   Method : {}", method);
        log.debug("   URL    : {}", url);
        log.debug("   Body   : {}", body);
    }

    public static void logResponse(Logger log, int statusCode, long responseTimeMs, String body) {
        log.debug("📥 RESPONSE");
        log.debug("   Status  : {}", statusCode);
        log.debug("   Time    : {} ms", responseTimeMs);
        log.debug("   Body    : {}", body);
    }
}
