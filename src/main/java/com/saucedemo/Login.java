package com.saucedemo;

import org.testng.Assert;

public class Login extends TestBase {
    protected static final String FORM_XPATH = "//*[@class='login_wrapper-inner']";

    protected void clickLoginButton() {
        clickElementBy("id", LOGIN_BUTTON_ID);
    }

    protected void setPassword(String password) {
        setElementBy("id", PASSWORD_INPUT_ID, password);
    }

    protected void setUsername(String username) {
        setElementBy("id", USERNAME_INPUT_ID, username);
    }

    private void verifyErrorMessage(String actualErrorMessage, String expectedErrorMessage) {
        System.out.println("Expected Error Message: " + expectedErrorMessage);
        System.out.println("Actual Error Message: " + actualErrorMessage);

        Assert.assertNotNull(actualErrorMessage, "Actual error message is null");
        Assert.assertNotNull(expectedErrorMessage, "Expected error message is null");
        Assert.assertTrue(actualErrorMessage.contains(expectedErrorMessage), "No validation error");
    }

    protected void loginErrorMessage(String errorMessageKey, String actualErrorMessage) {
        String expectedErrorMessage = props.getProperty(errorMessageKey);
        verifyErrorMessage(actualErrorMessage, expectedErrorMessage);
    }

    protected void verifyNoPasswordError() {
        waitForPageToLoad();
        loginErrorMessage("noPassword", "Epic sadface: Password is required");
    }

    protected void verifyNoUsernameError() {
        waitForPageToLoad();
        loginErrorMessage("noUsername", "Epic sadface: Username is required");
    }

    protected void verifyNoUsernameAndPasswordError() {
        waitForPageToLoad();
        loginErrorMessage("noDataError", "Epic sadface: Username is required");
    }

    protected void verifyInvalidUsernameOrPasswordError() {
        waitForPageToLoad();
        loginErrorMessage("invalidCredentials", "Epic sadface: Username and password do not match any user in this service");
    }

    protected void resetUsername() {
        setUsername("");

    }
    protected void resetPassword(){
        setPassword("");
    }
    protected void verifySuccessfulLogin() {
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, "https://www.saucedemo.com/inventory.html", "User is not on the inventory page");
    }
}