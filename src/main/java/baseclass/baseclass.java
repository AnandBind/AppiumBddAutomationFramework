package baseclass;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

public class baseclass {

    private static final Logger logger = LoggerFactory.getLogger(baseclass.class);

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<Properties> prop = ThreadLocal.withInitial(() -> {
        Properties properties = new Properties();
        try {
            String configFilePath = System.getProperty("config.file.path", "E:\\AutomationAppiumTest\\properties\\config.properties");
            FileInputStream ip = new FileInputStream(configFilePath);
            properties.load(ip);
        } catch (IOException e) {
            logger.error("Failed to load properties file", e);
            throw new RuntimeException("Failed to load properties file", e);
        }
        return properties;
    });

    public static Properties getProperties() {
        return prop.get();
    }

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    public static void setDriver(String platformName) {
        try {
            DesiredCapabilities caps = new DesiredCapabilities();
            String appiumServerUrl = getProperties().getProperty("appium.server");

            if (platformName.equalsIgnoreCase("Android")) {
                caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
                caps.setCapability(MobileCapabilityType.DEVICE_NAME, getProperties().getProperty("android.deviceName"));
                caps.setCapability(MobileCapabilityType.APP, getProperties().getProperty("android.appPath"));
                caps.setCapability("appPackage", getProperties().getProperty("android.appPackage"));
                caps.setCapability("appActivity", getProperties().getProperty("android.appActivity"));
                caps.setCapability("noReset", Boolean.parseBoolean(getProperties().getProperty("android.noReset")));
                caps.setCapability("fullReset", Boolean.parseBoolean(getProperties().getProperty("android.fullReset")));

                // Added UIAutomator2 as automation name
                caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");

                driver.set(new AndroidDriver(new URL(appiumServerUrl), caps));
            }
            else if (platformName.equalsIgnoreCase("iOS")) {
                caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
                caps.setCapability(MobileCapabilityType.DEVICE_NAME, getProperties().getProperty("ios.deviceName"));
                caps.setCapability(MobileCapabilityType.APP, getProperties().getProperty("ios.appPath"));

                driver.set(new IOSDriver(new URL(appiumServerUrl), caps));
            }
            else {
                throw new IllegalArgumentException("Invalid platform: " + platformName);
            }

            // Adding implicit wait for stability
            getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            logger.info("Driver initialized successfully for platform: " + platformName);

        } catch (MalformedURLException e) {
            logger.error("Invalid Appium Server URL", e);
            throw new RuntimeException("Invalid Appium Server URL", e);
        } catch (Exception e) {
            logger.error("Error initializing driver", e);
            throw new RuntimeException("Error initializing driver", e);
        }
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            logger.info("Appium driver quit successfully.");
        }
    }

    public void launchApp() {
        logger.info("Initializing Appium Driver...");

        String platform = getProperties().getProperty("platform");

        setDriver(platform);

        logger.info("Appium Driver initialized successfully for platform: " + platform);
    }

    public static void takeScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            try {
                File source = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
                String screenshotPath = System.getProperty("user.dir") + "/screenshots/";
                String screenshotName = scenario.getName().replaceAll(" ", "_") + "_" + System.currentTimeMillis() + ".png";

                // Ensure the screenshots directory exists
                File directory = new File(screenshotPath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File destination = new File(screenshotPath + screenshotName);
                FileUtils.copyFile(source, destination);
                logger.info("Screenshot saved: " + destination.getAbsolutePath());

                // Attach screenshot to Cucumber report
                byte[] screenshotBytes = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshotBytes, "image/png", "Failed Scenario Screenshot");

            } catch (IOException e) {
                logger.error("Error taking screenshot: " + e.getMessage());
            }
        }
    }
}
