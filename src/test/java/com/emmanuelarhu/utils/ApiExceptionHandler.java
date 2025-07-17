package com.emmanuelarhu.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Centralized exception handling for API tests
 */
public class ApiExceptionHandler {

    /**
     * Handle API response exceptions with detailed logging
     * @param response The API response
     * @param operation The operation being performed
     * @param expectedStatuses Array of expected status codes
     */
    public static void handleApiResponse(Response response, String operation, int... expectedStatuses) {
        try {
            int actualStatus = response.getStatusCode();
            String responseBody = response.getBody().asString();

            // Log response details
            System.out.println("📊 API Response for " + operation + ":");
            System.out.println("   Status Code: " + actualStatus);
            System.out.println("   Response Time: " + response.getTime() + "ms");
            System.out.println("   Content Type: " + response.getContentType());

            // Attach response details to Allure
            Allure.addAttachment("Response Details", "text/plain",
                    "Operation: " + operation + "\n" +
                            "Status Code: " + actualStatus + "\n" +
                            "Response Time: " + response.getTime() + "ms\n" +
                            "Content Type: " + response.getContentType() + "\n" +
                            "Response Body: " + responseBody);

            // Check if status is expected
            boolean isExpectedStatus = false;
            for (int expectedStatus : expectedStatuses) {
                if (actualStatus == expectedStatus) {
                    isExpectedStatus = true;
                    break;
                }
            }

            if (!isExpectedStatus) {
                String errorMessage = String.format(
                        "Unexpected status code for %s. Expected: %s, Actual: %d",
                        operation, java.util.Arrays.toString(expectedStatuses), actualStatus
                );

                // Log error details
                System.err.println("❌ " + errorMessage);
                System.err.println("   Response Body: " + responseBody);

                // Add error to Allure
                Allure.step(errorMessage, Status.FAILED);

                // Handle specific error cases
                handleSpecificErrors(actualStatus, operation, responseBody);
            } else {
                System.out.println("✅ " + operation + " completed with expected status: " + actualStatus);
            }

        } catch (Exception e) {
            handleGenericException(e, operation);
        }
    }

    /**
     * Handle specific HTTP error codes with appropriate messages
     * @param statusCode The HTTP status code
     * @param operation The operation being performed
     * @param responseBody The response body
     */
    private static void handleSpecificErrors(int statusCode, String operation, String responseBody) {
        switch (statusCode) {
            case 400:
                System.err.println("🔴 Bad Request (400): The request was invalid");
                break;
            case 401:
                System.err.println("🔴 Unauthorized (401): Authentication required");
                break;
            case 403:
                System.err.println("🔴 Forbidden (403): Access denied - API may have restrictions");
                System.err.println("   This could be due to:");
                System.err.println("   - Rate limiting");
                System.err.println("   - API key requirements");
                System.err.println("   - IP restrictions");
                System.err.println("   - API policy changes");
                break;
            case 404:
                System.err.println("🔴 Not Found (404): Resource not found");
                break;
            case 405:
                System.err.println("🔴 Method Not Allowed (405): HTTP method not supported");
                break;
            case 422:
                System.err.println("🔴 Unprocessable Entity (422): Invalid data format");
                break;
            case 429:
                System.err.println("🔴 Too Many Requests (429): Rate limit exceeded");
                break;
            case 500:
                System.err.println("🔴 Internal Server Error (500): Server-side error");
                break;
            case 502:
                System.err.println("🔴 Bad Gateway (502): Server communication error");
                break;
            case 503:
                System.err.println("🔴 Service Unavailable (503): Server temporarily unavailable");
                break;
            default:
                System.err.println("🔴 Unexpected HTTP status: " + statusCode);
        }
    }

    /**
     * Handle generic exceptions with detailed logging
     * @param exception The exception that occurred
     * @param operation The operation being performed
     */
    public static void handleGenericException(Exception exception, String operation) {
        String errorMessage = String.format("Exception during %s: %s", operation, exception.getMessage());

        System.err.println("❌ " + errorMessage);
        System.err.println("   Exception Type: " + exception.getClass().getSimpleName());

        if (exception.getCause() != null) {
            System.err.println("   Root Cause: " + exception.getCause().getMessage());
        }

        // Add exception details to Allure
        Allure.addAttachment("Exception Details", "text/plain",
                "Operation: " + operation + "\n" +
                        "Exception Type: " + exception.getClass().getSimpleName() + "\n" +
                        "Message: " + exception.getMessage() + "\n" +
                        "Stack Trace: " + getStackTraceAsString(exception));

        // Log stack trace for debugging
        exception.printStackTrace();
    }

    /**
     * Handle network-related exceptions
     * @param exception The network exception
     * @param operation The operation being performed
     */
    public static void handleNetworkException(Exception exception, String operation) {
        System.err.println("🌐 Network issue during " + operation + ": " + exception.getMessage());

        // Common network issues
        if (exception.getMessage().contains("timeout")) {
            System.err.println("   ⏱️ Request timed out - API may be slow or unreachable");
        } else if (exception.getMessage().contains("Connection refused")) {
            System.err.println("   🔌 Connection refused - API server may be down");
        } else if (exception.getMessage().contains("UnknownHostException")) {
            System.err.println("   🌐 DNS resolution failed - Check API URL");
        } else if (exception.getMessage().contains("SSLException")) {
            System.err.println("   🔒 SSL/TLS issue - Certificate or security problem");
        }

        handleGenericException(exception, operation);
    }

    /**
     * Convert stack trace to string
     * @param exception The exception
     * @return Stack trace as string
     */
    private static String getStackTraceAsString(Exception exception) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Log test step with timestamp
     * @param stepDescription Description of the test step
     */
    public static void logTestStep(String stepDescription) {
        String timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
        );
        System.out.println("📋 [" + timestamp + "] " + stepDescription);
        Allure.step(stepDescription);
    }

    /**
     * Log API performance metrics
     * @param response The API response
     * @param operation The operation performed
     */
    public static void logPerformanceMetrics(Response response, String operation) {
        long responseTime = response.getTime();

        System.out.println("⚡ Performance Metrics for " + operation + ":");
        System.out.println("   Response Time: " + responseTime + "ms");

        // Performance thresholds
        if (responseTime < 1000) {
            System.out.println("   ✅ Excellent performance (< 1s)");
        } else if (responseTime < 3000) {
            System.out.println("   ⚠️ Acceptable performance (1-3s)");
        } else if (responseTime < 10000) {
            System.out.println("   🔸 Slow performance (3-10s)");
        } else {
            System.out.println("   🔴 Poor performance (> 10s)");
        }

        // Add performance data to Allure
        Allure.addAttachment("Performance Metrics", "text/plain",
                "Operation: " + operation + "\n" +
                        "Response Time: " + responseTime + "ms\n" +
                        "Status: " + response.getStatusCode() + "\n" +
                        "Content Length: " + response.getBody().asString().length() + " bytes");
    }
}