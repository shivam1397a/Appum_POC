package Steps;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.vimalselvam.cucumber.listener.Reporter;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import javax.imageio.ImageIO;

public class LoginTest {
    WebDriver driver;
    public static String dynamictime = String.valueOf(System.currentTimeMillis());
    StopWatch stopWatch =new StopWatch();

    @Given("^I navigate to Teamie login page in \"([^\"]*)\" browser$")
    public void iNavigateToTeamieLoginPageInBrowser(String browser) throws Throwable {
        if (browser.equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", ".\\drivers\\chromedriver.exe");
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            System.setProperty("webdriver.chrome.driver", ".\\drivers\\geckodriver.exe");
            driver = new FirefoxDriver();
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://teamie-next.teamieapp.com");
        Reporter.addStepLog("Successfully navigated to Teamie login page");
        getscreenshot();
        Reporter.addScreenCaptureFromPath(LoginTest.destinationPath.toString());
    }

    @When("^I login to Teamie as \"([^\"]*)\"$")
    public void iLoginToTeamieAs(String user) throws Exception {
        String username = null, password = null;
        if (user.equalsIgnoreCase("Active User")){
            username = "login.auto";
            password = "krait14%";
        } else if (user.equalsIgnoreCase("Blocked User")){
            username = "login.auto-blocked";
            password = "krait14%";
        }
        Thread.sleep(3000l);
        driver.findElement(By.id("edit-name")).sendKeys(username);
        driver.findElement(By.id("edit-pass")).sendKeys(password);
        driver.findElement(By.id("edit-submit")).click();
        stopWatch.start();
        Reporter.addStepLog("Provided value for Username and Password field and clicked Login button");
        getscreenshot();
        Reporter.addScreenCaptureFromPath(LoginTest.destinationPath.toString());
    }

    @Then("^I verify that user is \"([^\"]*)\"$")
    public void iVerifyThatUserIs(String result) throws Exception {
        if (result.equalsIgnoreCase("successfully logged in")){
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@title='Profile']")));
            stopWatch.stop();
            int pageload_time = (int)stopWatch.getTime(TimeUnit.SECONDS);
            Reporter.addStepLog("Page loaded in "+pageload_time+"s");
            Assert.assertTrue(driver.findElement(By.xpath("//a[@title='Profile']")).isDisplayed());
            System.out.println("Login attempt Successful");
            Reporter.addStepLog("Login attempt successful!!! Welcome to home page");
            String currentURL = driver.getCurrentUrl();
            if (currentURL.equals("https://teamie-next.teamieapp.com/dash/#/")) {
                System.out.println("URL matched");
                Reporter.addStepLog("Current URL matches the URL in requirement, i.e " + currentURL);
            } else {
                Assert.fail("Please check your credentials");
            }
            getscreenshot();
            Reporter.addScreenCaptureFromPath(LoginTest.destinationPath.toString());
        }else if (result.equalsIgnoreCase("not logged in")){
            Assert.assertTrue(driver.findElement(By.id("edit-name")).isDisplayed());
            System.out.println("Login attempt not successful");
            Reporter.addStepLog("Login attempt not successful!!! Check your credentials");
            String errorMsg = driver.findElement(By.xpath("//div[h2[text()='Error message']]")).getAttribute("innerText");
            Assert.assertTrue(errorMsg.contains("Sorry, unrecognized username or password."));
            System.out.println("Error message is displayed");
            Reporter.addStepLog("Error message is displayed");
            getscreenshot();
            Reporter.addScreenCaptureFromPath(LoginTest.destinationPath.toString());
        }
    }

    @And("^I close the automation browser$")
    public void iCloseTheAutomationBrowser() throws Exception{
        System.out.println("Execution Successful");
        Reporter.addStepLog("Execution Successful");
        driver.close();
    }

    public static File destinationPath;

    public void getscreenshot() throws IOException
    {
//        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
        Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(driver);
        destinationPath = new File(System.getProperty("user.dir") + "/target/cucumber-reports/screenshot"+System.currentTimeMillis()+".jpg");
        ImageIO.write(screenshot.getImage(), "jpg", destinationPath);
    }
}
