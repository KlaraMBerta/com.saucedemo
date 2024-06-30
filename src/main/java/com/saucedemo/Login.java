package com.saucedemo;

import org.testng.Assert;

public class Login extends TestBase {
    private static final String FORM_XPATH="//div[@class='login_wrapper-inner']']";
    protected void clickLoginButton() {
        clickElementBy("id", LOGIN_BUTTON_ID);
    }
    protected void loginErrorMessageNoData(String noDataError){
        Assert.assertTrue(getTextMessage("xPath",FORM_XPATH).contains(props.getProperty("noDataError")), "No validation error");


    }
}