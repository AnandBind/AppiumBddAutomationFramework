package stepsDefinitions;

import base.base;
import io.cucumber.java.en.Given;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class loginSteps{

    @Given("the user launches the app")
    public void userLaunchesApp() {
     base.setupDriver();
    }
}
