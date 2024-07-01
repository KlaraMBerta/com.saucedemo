package Login;

import com.saucedemo.Login;
import io.qameta.allure.Description;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

public class TestLogin extends Login {
    @BeforeMethod
    public void setUpTest() throws IOException {
        super.setUp(); // ensure the WebDriver and WebDriverWait are initialized
        props = loadProperties("testLogin.properties");
    }

    @Test
    @Description("Test login functionality")
    public void testLogin() throws IOException {
        waitForPageToLoad();

        // shouldCheckIfThereIsPossibilityToLoginWithoutCredentials
        clickLoginButton();
        verifyNoUsernameAndPasswordError();


        // shouldCheckIfThereIsPossibilityToLoginWithoutUsername
        setPassword(props.getProperty("password"));
        clickLoginButton();
        verifyNoUsernameError();

        // shouldCheckIfThereIsPossibilityToLoginWithoutPassword
        resetPassword();
        setUsername(props.getProperty("username"));
        clickLoginButton();
        verifyNoPasswordError();

        // shouldCheckIfThereIsPossibilityToLoginWithInvalidCredentials
        resetUsername();
        setPassword(props.getProperty("invalidPassword"));
        setUsername(props.getProperty("invalidUsername"));
        clickLoginButton();
        verifyInvalidUsernameOrPasswordError();

        // shouldCheckIfUserCanLoginWithValidCredentials
        resetUsername();
        resetPassword();
        setUsername(props.getProperty("username"));
        setPassword(props.getProperty("password"));
        clickLoginButton();
        waitForPageToLoad();
        verifySuccessfulLogin();
    }
}