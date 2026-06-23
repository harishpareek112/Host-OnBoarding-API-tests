# GamePe Auth API Tests — Java + RestAssured

## Project Structure

```
gamepe-api-tests/
├── pom.xml
└── src/test/java/com/gamepe/
    ├── config/
    │   └── Config.java          ← Base URL, endpoints, test data
    ├── base/
    │   └── BaseTest.java        ← RestAssured setup (runs before all tests)
    └── tests/
        ├── SignUpTest.java       ← 5 test cases
        ├── SendOtpTest.java      ← 3 test cases
        ├── VerifyOtpTest.java    ← 5 test cases
        └── ResendOtpTest.java    ← 3 test cases
```

## Prerequisites

- Java 11+
- Maven 3.6+

## Setup

```bash
# Project folder mein jaao
cd gamepe-api-tests

# Dependencies download karo
mvn clean install -DskipTests
```

## Tests Run Karo

```bash
# Saare tests run karo
mvn test

# Sirf ek class run karo
mvn test -Dtest=SignUpTest

# Sirf ek method run karo
mvn test -Dtest=SignUpTest#tc01_signUp_validMobile_shouldReturn200Or201
```

## Allure Report Generate Karo

```bash
mvn allure:serve
# Browser automatically khulega pretty report ke saath
```

## Test Data Change Karna

`src/test/java/com/gamepe/config/Config.java` mein:

```java
public static final String VALID_MOBILE  = "9876543210";  // apna number daalo
public static final String CORRECT_OTP   = "0000";         // correct OTP
public static final String WRONG_OTP     = "1234";         // galat OTP
```

## Total Test Cases: 16

| Class           | Functional | Negative | Total |
|----------------|-----------|---------|-------|
| SignUpTest      | 1         | 4       | 5     |
| SendOtpTest     | 1         | 2       | 3     |
| VerifyOtpTest   | 2         | 3       | 5     |
| ResendOtpTest   | 1         | 2       | 3     |
| **Total**       | **5**     | **11**  | **16**|
