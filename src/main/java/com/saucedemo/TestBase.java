package com.saucedemo;

import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;

public class TestBase {
    public static final Logger LOG = LoggerFactory.getLogger(TestBase.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    protected Actions actions;
    protected Properties props;

    public TestBase() {super();}

    protected static final String USERNAME_INPUT_ID="user-name";
    protected static final String PASSWORD_INPUT_ID="password";
    protected static final String LOGIN_BUTTON_ID="login-button";
    protected static final String FIRST_NAME_ID="first-name";
    protected static final String LAST_NAME_ID="last-name";
    protected static final String POSTAL_CODE_ID="postal-code";

    public void setGlobalProp() throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("common_resource.properties")) {
            if (input == null) {
                LOG.error("Sorry, unable to find common_resource.properties");
                throw new IOException("File common_resource.properties not found");
            }
            props.load(input);
            System.out.println("file common_resource.properties was uploaded");
        } catch (IOException ex) {
            LOG.error("IOException: " + ex.getMessage());
            throw ex;
        }

        try (InputStream envInput = getClass().getClassLoader().getResourceAsStream("environment.properties")) {
            if (envInput == null) {
                LOG.error("Sorry, unable to find environment.properties");
                throw new IOException("File environment.properties not found");
            }
            Properties envProps = new Properties();
            envProps.load(envInput);
            for (String key : envProps.stringPropertyNames()) {
                props.setProperty(key, envProps.getProperty(key));
            }
        } catch (IOException ex) {
            LOG.error("IOException: " + ex.getMessage());
            throw ex;
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    @BeforeTest(alwaysRun = true)
    public void setUp() {
        props = new Properties();
        try {
            setGlobalProp();
        } catch (Exception e) {
            LOG.error("Global properties weren't set", e);
            return;
        }
        System.setProperty("org.uncommons.reportng.escape-output", "false");
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);

        final ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--test-type");
        options.addArguments("--window-size=1280,1024");
        options.addArguments("--remote-allow-origins=*");

        String headlessProp = props.getProperty("headless");
        if ("true".equals(headlessProp)) {
            options.addArguments("--headless");
        }

        options.setCapability(ChromeOptions.CAPABILITY, options);

        String driverPath = props.getProperty("chromeDriverPath");
        if (driverPath != null) {
            System.setProperty("webdriver.chrome.driver", driverPath);
        } else {
            LOG.error("chromeDriverPath property is not set");
            return;
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, ofSeconds(40));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
        driver.manage().window().maximize();
        js = ((JavascriptExecutor) driver);
        actions = new Actions(driver);

        String url = props.getProperty("url");
        if (url != null) {
            driver.get(url);
        } else {
            LOG.error("url property is not set");
            return;
        }

        String name = getNameBasedOnUrl();
        Map<String, String> environmentData = new HashMap<>();
        environmentData.put("URL", name);
        environmentData.put("User", "Admin");
        environmentData.put("OS", System.getProperty("os.name"));
        environmentData.put("Browser", "chrome");
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void catchExceptions(ITestResult result, Method method) throws IOException {
        if (!result.isSuccess()) {
            System.out.println("Test failed");
        }
    }

    protected String getProjectPath() {
        return props.getProperty("projectPath");
    }

    public String getNameBasedOnUrl() {
        var url = props.getProperty("url");
        var environmentName = props.getProperty("name");
        if (url != null && url.contains(environmentName)) {
            LOG.debug("im here" + environmentName + " " + url);
        }
        return environmentName;
    }

    protected WebElement getElementBy(String type, String typeValue) {
        switch (type) {
            case ("id"):
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(typeValue)));
                return driver.findElement(By.id(typeValue));
            case ("xPath"):
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(typeValue)));
                return driver.findElement(By.xpath(typeValue));
            case ("href"):
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//a[@href='" + typeValue + "']")));
                return driver.findElement(By.xpath("*//a[@href='" + typeValue + "']"));
            case ("name"):
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(typeValue)));
                return driver.findElement(By.name(typeValue));
            case ("className"):
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(typeValue)));
                return driver.findElement(By.className(typeValue));
            case ("value"):
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("*//input[@value='" + typeValue + "']")));
                return driver.findElement(By.xpath("*//input[@value='" + typeValue + "']"));
            case ("cssSelector"):
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(typeValue)));
                return driver.findElement(By.cssSelector(typeValue));
        }
        return driver.findElement(By.cssSelector(typeValue));
    }

    protected List<WebElement> getElementsBy(String type, String typeValue) {
    return switch (type)
            {
                case "id"-> driver.findElements(By.id(typeValue));
                case "xPath"-> driver.findElements(By.xpath(typeValue));
                case "href"-> driver.findElements(By.xpath("*//a[@href='" + typeValue + "']"));
                case "name"-> driver.findElements(By.name(typeValue));
                case "className"-> driver.findElements(By.className(typeValue));
                case "value"-> driver.findElements(By.xpath("*//input[@value='" + typeValue + "']"));
                case "cssSelector"-> driver.findElements(By.cssSelector(typeValue));
                default-> driver.findElements(By.cssSelector(typeValue));
            };

    }
    protected void clickElementBy(String type, String typeValue){ getElementBy(type,typeValue).click();}
    protected void setElementBy(String type, String typeValue, String value){
              getElementBy(type,typeValue). clear();
              getElementBy(type,typeValue). sendKeys(value);
    }
    @Step("Type {username} / {password}.")
    protected void loginToShop(String username, String password, Properties prop){
              waitForPageToLoad();
              if(props.getProperty("randomUsers").equals("true"))
              {
                  String[]usernames = prop.getProperty("username").split(",");
                  String[]passwords = prop.getProperty("password").split(",");
                  int rnd = new Random().nextInt(usernames.length);
                  username = usernames[rnd];
                  password = passwords[rnd];
              }
              setElementBy("id", USERNAME_INPUT_ID, username);
              setElementBy("id", PASSWORD_INPUT_ID, password);
              clickElementBy("id", LOGIN_BUTTON_ID);
              LOG.debug("Logged in to shop");
    }


    protected Properties loadProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("testLogin/" + fileName)) {
            if (input == null) {
                throw new IOException("File " + fileName + " not found");
            }
            properties.load(input);
        }
        return properties;
    }


    protected void setUserData(String firstName, String lastName, String postalCode){
              clickElementBy("id",FIRST_NAME_ID);
              setElementBy("id",FIRST_NAME_ID, firstName);
              clickElementBy("id",LAST_NAME_ID);
              setElementBy("id",LAST_NAME_ID,lastName);
              clickElementBy("id",POSTAL_CODE_ID);
              setElementBy("id",POSTAL_CODE_ID,postalCode);
    }
    public void clickEnter(){
              waitForPageToLoad();
              actions.sendKeys(Keys.ENTER);
              actions.build().perform();
    }
    protected void waitASec(){
              try{
                  sleep(3000);
              }catch (InterruptedException e){
                  LOG.error("Cannot sleep" + e);
              }
    }
    protected void waitForPageToLoad(){
              wait.until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));
              waitASec();
    }
    protected String getTextMessage(String type, String typeValue){ return getElementBy(type,typeValue).getText();}
    protected void scrollUpByPage(){
              JavascriptExecutor jse = (JavascriptExecutor) driver;
              jse.executeScript("window.scrollBy(0,-650");

    }
    protected void scrollDownByPage(){
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,650");

    }
    public void focusOnElement(WebElement element){
              new Actions(driver).moveToElement(element).perform();
              waitASec();
    }
    public void focusOnElementAndClick(WebElement element){
        new Actions(driver).moveToElement(element).click().perform();
        waitASec();}
    protected boolean checkIfElementIsDisplayed(final String type, final String typeValue){
              try{
                  return switch (type){
                      case "id"-> driver.findElement(By.id(typeValue)).isDisplayed();
                      case "xPath"-> driver.findElement(By.xpath(typeValue)).isDisplayed();
                      case "href"-> driver.findElement(By.xpath("*//a[@href='" + typeValue + "']")).isDisplayed();
                      case "name"-> driver.findElement(By.name(typeValue)).isDisplayed();
                      case "className"-> driver.findElement(By.className(typeValue)).isDisplayed();
                      case "value"-> driver.findElement(By.xpath("*//input[@value='" + typeValue + "']")).isDisplayed();
                      case "cssSelector"-> driver.findElement(By.cssSelector(typeValue)).isDisplayed();
                      default-> driver.findElement(By.cssSelector(typeValue)).isDisplayed();
                  };
              }catch (org.openqa.selenium.NoSuchElementException e){
                  return false;
              }
    } protected void findElementInTheList(final String type, final String typeValue){
              List<WebElement> elements = getElementsBy(type, typeValue);
              elements.get(0).isDisplayed();
              elements.get(0).click();
    }
    protected void checkChanges(String before, String after){
              waitForPageToLoad();
        Assert.assertEquals(before, after, "There wasn't any change");
    }
    protected String prepareURLBasedOnProfileURL(String urlPath){
              String host = props.getProperty("url");
              return host + urlPath;
    }
}