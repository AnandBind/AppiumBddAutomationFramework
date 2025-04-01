package base;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class base {
    private static final Logger logger = LoggerFactory.getLogger(base.class);
    private static AppiumDriver driver;

    public static AppiumDriver getDriver() {
        return driver;
    }

    public static void setupDriver() {
        try {
            File appDir = new File("src");
            File app = new File(appDir, "General-Store.apk"); // App file

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "AnandAmulator"); // Emulator Name
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 14);
            capabilities.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());

            driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            logger.info("Appium Driver initialized successfully.");

        } catch (MalformedURLException e) {
            logger.error("Error initializing Appium driver", e);
            throw new RuntimeException(e);
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            logger.info("Driver closed successfully.");
        }
    }
}
