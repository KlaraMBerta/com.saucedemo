package Login;

import com.saucedemo.Login;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

@Test
public class TestLogin extends Login {
    public void testLogin() throws IOException{
        Properties props = loadProperties("testLogin");
        waitForPageToLoad();

        //shouldCheckIfThereIsPossibilityToLoginWithoutCredentials
        clickLoginButton();
        loginErrorMessageNoData(props.getProperty("noDataError"));

    }
}
