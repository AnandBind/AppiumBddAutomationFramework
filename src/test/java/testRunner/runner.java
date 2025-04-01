package testRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/login.feature",
    glue = {"stepsDefinitions","base"},
    plugin = {"pretty", "html:target/cucumber-reports.html"},
    monochrome = true
)

public class runner extends AbstractTestNGCucumberTests{

}
