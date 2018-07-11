package com.example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;

/**
 * Created by micarr on 07/13/18.
 */
public class CookieTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    private void setup() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "8.0");
        caps.setCapability("deviceName", "Android Emulator");
        caps.setCapability("browserName", "Chrome");
        caps.setCapability("automationName", "UiAutomator2");
        // May need this line, as necessary, to connect.
        caps.setCapability("chromedriverExecutableDir", "/Users/lappm/chromedrivers");
        driver = new AndroidDriver<WebElement>(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        wait = new WebDriverWait(driver, 10);
    }

    @AfterMethod
    private void teardown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        wait = null;
    }

    private Cookie cookieSetup(String cookieName) {
        driver.get("https://google.com");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
        Date expiry = new GregorianCalendar(2019, 6, 6).getTime();
        Cookie cookie = new Cookie(cookieName, "value", "", expiry);
        driver.manage().addCookie(cookie);
        return wait.until(d -> driver.manage().getCookieNamed(cookieName));
    }

    @Test
    public void testSessionCookieDelete() {
        String cookieName = System.getProperty("cookie", "session");
        Cookie cookie = cookieSetup(cookieName);
        // Android fails here
        driver.manage().deleteCookie(cookie);
        wait.until(d -> driver.manage().getCookieNamed(cookieName) == null);
    }

    @Test
    public void testSessionCookieDeleteAll() {
        String cookieName = System.getProperty("cookie", "session");
        cookieSetup(cookieName);
        // This doesn't fail on Android
        driver.manage().deleteAllCookies();
        wait.until(d -> driver.manage().getCookieNamed(cookieName) == null);
    }

}
