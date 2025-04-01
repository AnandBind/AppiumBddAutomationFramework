package baseclass;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class hooks extends baseclass {
    private static final Logger logger = LoggerFactory.getLogger(hooks.class);

    @Before
    public void setup(Scenario scenario) {
        logger.info("Starting Scenario: " + scenario.getName());

        String platform = getProperties().getProperty("platform");
        setDriver(platform);

        logger.info("Driver initialized for platform: " + platform);
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            logger.error("Scenario Failed: " + scenario.getName());
            takeScreenshot(scenario);
        } else {
            logger.info("Scenario Passed: " + scenario.getName());
        }

        quitDriver();
        logger.info("Driver quit successfully.");
    }

}
