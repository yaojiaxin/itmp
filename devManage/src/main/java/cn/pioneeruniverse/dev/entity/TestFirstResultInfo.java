package cn.pioneeruniverse.dev.entity;

import java.util.List;

public class TestFirstResultInfo {
    private String systemCode;
    private String testRequestNumber;
    private String successNumber;
    private String failedNumber;
    private String testResult;
    private String testResultDetailUrl;
    private String testType;
    private List<TestFirstResultInfo> subSystemInfo;

    private String subSystemCode;
    private String subTestRequestNumber;
    private String subSuccessNumber;
    private String subFailedNumber;
    private String subTestResult;
    private String subTestResultDetailUrl;

    public String getSystemCode() {
        return systemCode;
    }
    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getTestRequestNumber() {
        return testRequestNumber;
    }
    public void setTestRequestNumber(String testRequestNumber) {
        this.testRequestNumber = testRequestNumber;
    }

    public String getSuccessNumber() {
        return successNumber;
    }
    public void setSuccessNumber(String successNumber) {
        this.successNumber = successNumber;
    }

    public String getFailedNumber() {
        return failedNumber;
    }
    public void setFailedNumber(String failedNumber) {
        this.failedNumber = failedNumber;
    }

    public String getTestResult() {
        return testResult;
    }
    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTestResultDetailUrl() {
        return testResultDetailUrl;
    }
    public void setTestResultDetailUrl(String testResultDetailUrl) {
        this.testResultDetailUrl = testResultDetailUrl;
    }

    public List<TestFirstResultInfo> getSubSystemInfo() {
        return subSystemInfo;
    }
    public void setSubSystemInfo(List<TestFirstResultInfo> subSystemInfo) {
        this.subSystemInfo = subSystemInfo;
    }

    public String getSubSystemCode() {
        return subSystemCode;
    }
    public void setSubSystemCode(String subSystemCode) {
        this.subSystemCode = subSystemCode;
    }

    public String getSubTestRequestNumber() {
        return subTestRequestNumber;
    }
    public void setSubTestRequestNumber(String subTestRequestNumber) {
        this.subTestRequestNumber = subTestRequestNumber;
    }

    public String getSubSuccessNumber() {
        return subSuccessNumber;
    }
    public void setSubSuccessNumber(String subSuccessNumber) {
        this.subSuccessNumber = subSuccessNumber;
    }

    public String getSubFailedNumber() {
        return subFailedNumber;
    }
    public void setSubFailedNumber(String subFailedNumber) {
        this.subFailedNumber = subFailedNumber;
    }

    public String getSubTestResult() {
        return subTestResult;
    }
    public void setSubTestResult(String subTestResult) {
        this.subTestResult = subTestResult;
    }

    public String getSubTestResultDetailUrl() {
        return subTestResultDetailUrl;
    }

    public String getTestType() {
        return testType;
    }
    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void setSubTestResultDetailUrl(String subTestResultDetailUrl) {
        this.subTestResultDetailUrl = subTestResultDetailUrl;
    }
}
