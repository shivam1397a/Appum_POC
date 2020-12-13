package MobileSteps;

import com.vimalselvam.cucumber.listener.Reporter;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.appium.java_client.MobileElement;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.touch.TouchActions;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import static MobileSteps.WebConnector.driver;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MobileLoginTest {
    StopWatch stopWatch = new StopWatch();
    WebConnector wc = new WebConnector();

    @Given("^I navigate to Teamie login page in \"([^\"]*)\" browser$")
    public void iNavigateToTeamieLoginPageInBrowser(String browser) throws Throwable {
        wc.setUpDriver(browser);
        Reporter.addStepLog("Successfully navigated to Teamie login page");

        Thread.sleep(3000L);
        wc.PerformActionOnElement("xpath,//*[@text=\"Flights\"]", "Click", "");
        wc.PerformActionOnElement("xpath,//*[@text=\"DEPART ON\"]", "Click", "");
        ((AndroidDriver<MobileElement>) driver).findElementByAndroidUIAutomator("new UiScrollable("
                + "new UiSelector().scrollable(true)).scrollIntoView("
                + "new UiSelector().textContains(\"June 2021\"));");
        wc.PerformActionOnElement("xpath,//android.widget.LinearLayout[*[@text='June 2021']]//*[@text='25']", "Click", "");
        getscreenshot();
        Thread.sleep(2000L);
        Reporter.addScreenCaptureFromPath(destinationPath.toString());
    }

    @When("^I login to Teamie as \"([^\"]*)\"$")
    public void iLoginToTeamieAs(String user) throws Exception {
        String username = null, password = null;
        if (user.equalsIgnoreCase("Active User")) {
            username = "login.auto";
            password = "krait14%";
        } else if (user.equalsIgnoreCase("Blocked User")) {
            username = "login.auto-blocked";
            password = "krait14%";
        }
        Thread.sleep(3000L);
//        MobileElement ee = driver.
        wc.PerformActionOnElement("xpath,//input[@id='edit-name']", "Type", username);
        wc.PerformActionOnElement("xpath,//input[@id='edit-pass']", "Type", password);
        wc.PerformActionOnElement("xpath,//input[@id='edit-submit']", "Click", "");
        stopWatch.start();
        Reporter.addStepLog("Provided value for Username and Password field and clicked Login button");
        getscreenshot();
        Reporter.addScreenCaptureFromPath(destinationPath.toString());
    }

    @Then("^I verify that user is \"([^\"]*)\"$")
    public void iVerifyThatUserIs(String result) throws Exception {
        if (result.equalsIgnoreCase("successfully logged in")) {
            wc.waitForCondition("PageLoad", "", 10);
            stopWatch.stop();
            int pageload_time = (int) stopWatch.getTime(TimeUnit.SECONDS);
            if (pageload_time < 11) {
                Reporter.addStepLog("Page loaded in " + pageload_time + "s");
                Assert.assertTrue(wc.FindAnElement("xpath,//a[@title='Profile']").isDisplayed());
                System.out.println("Login attempt Successful");
                Reporter.addStepLog("Login attempt successful!!! Welcome to home page");
                String currentURL = driver.getCurrentUrl();
                if (currentURL.equals("https://teamie-next.teamieapp.com/dash/#/")) {
                    System.out.println("URL matched");
                    Reporter.addStepLog("Current URL matches the URL in requirement, i.e " + currentURL);
                } else {
                    Assert.fail("Please check your credentials");
                }
            } else {
                Reporter.addStepLog("Timedout");
                driver.close();
            }
            getscreenshot();
            Reporter.addScreenCaptureFromPath(destinationPath.toString());
        } else if (result.equalsIgnoreCase("not logged in")) {
            Assert.assertTrue(wc.FindAnElement("xpath,//input[@id='edit-name']").isDisplayed());
            System.out.println("Login attempt not successful");
            Reporter.addStepLog("Login attempt not successful!!! Check your credentials");
            String errorMsg = wc.FindAnElement("xpath,//div[h2[text()='Error message']]").getAttribute("innerText");
            Assert.assertTrue(errorMsg.contains("Sorry, unrecognized username or password."));
            System.out.println("Error message is displayed");
            Reporter.addStepLog("Error message is displayed");
            getscreenshot();
            Reporter.addScreenCaptureFromPath(destinationPath.toString());
        }
    }

    @And("^I close the automation browser$")
    public void iCloseTheAutomationBrowser() throws Exception {
        driver.close();
    }

    public static File destinationPath;

    public void getscreenshot() throws IOException {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

//        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
//        Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(driver);
        destinationPath = new File(System.getProperty("user.dir") + "/target/cucumber-reports/screenshot" + System.currentTimeMillis() + ".jpg");
        FileUtils.copyFile(scrFile, destinationPath);
    }
}

