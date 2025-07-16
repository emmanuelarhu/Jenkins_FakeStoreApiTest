package com.emmanuelarhu.listeners;

import io.qameta.allure.Attachment;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listeners for enhanced reporting and logging
 */
public class TestListeners implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Starting test: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getMethod().getMethodName());

        // Attach failure details to Allure report
        attachFailureDetails(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getMethod().getMethodName());
    }

    @Attachment(value = "Failure Details", type = "text/plain")
    private String attachFailureDetails(ITestResult result) {
        StringBuilder details = new StringBuilder();
        details.append("Test Method: ").append(result.getMethod().getMethodName()).append("\n");
        details.append("Test Class: ").append(result.getTestClass().getName()).append("\n");
        details.append("Failure Reason: ").append(result.getThrowable().getMessage()).append("\n");
        details.append("Stack Trace: ").append(result.getThrowable().toString()).append("\n");

        return details.toString();
    }
}