package MobileSteps;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import cucumber.api.Scenario;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;


public class WebConnector<V> {

    public static WebDriver driver = null;
    public SessionId session = null;
    public static Properties prop = new Properties();

    public WebConnector() {
        try {
            prop.load(new FileInputStream("./configs/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebDriver getDriver() {
        return this.getDriver();
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setUpDriver(String browser) throws MalformedURLException {
        if (browser == null) {
            browser = "chrome";
        }
        switch (browser) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("start-maximized");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                System.setProperty("webdriver.gecko.driver", "./drivers/geckodriver.exe");
                driver = new FirefoxDriver();
                driver.manage().window().maximize();
                break;
            case "android":
                DesiredCapabilities androidCapabilities = new DesiredCapabilities();
                androidCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "sdk_gphone_x86_arm");
                androidCapabilities.setCapability(MobileCapabilityType.UDID, "emulator-5554");
                androidCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT,60);
                androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "11");
                androidCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
//                androidCapabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
//                androidCapabilities.setCapability(MobileCapabilityType.APP,"C:\\Users\\admin\\IdeaProjects\\Teamie_challenge\\apk\\apk\\com.android.calculator2.apk");
                androidCapabilities.setCapability("appPackage","com.cleartrip.android");
                androidCapabilities.setCapability("appActivity",".common.activities.CleartripHomeActivity");
                driver = new AndroidDriver<MobileElement>(new URL("http://127.0.0.1:4723/wd/hub"),
                        androidCapabilities);
                System.out.println("###........CONNECTION SUCCESSFULLY ESTABLISHED WITH NODE DEVICE........###");
//                JavascriptExecutor js = (JavascriptExecutor) driver;
//                js.executeScript("target.setDeviceOrientation(UIA_DEVICE_ORIENTATION_LANDSCAPELEFT);");
                break;
            case "iPad":
            case "iPhone":
                DesiredCapabilities iPadCapabilities = new DesiredCapabilities();
                iPadCapabilities.setCapability("deviceName", "<your iPhone/iPad’s Name>");
                iPadCapabilities.setCapability("udid", "<your iPhone’s udid>");
                iPadCapabilities.setCapability("bundleId", "com.google.Chrome");
                iPadCapabilities.setCapability(CapabilityType.BROWSER_NAME, prop.getProperty("Device_Browser"));
                iPadCapabilities.setCapability(CapabilityType.VERSION, prop.getProperty("DeviceVersion"));
                iPadCapabilities.setCapability(CapabilityType.PLATFORM_NAME,"iOS");
                //capabilities.setCapability("app", prop.getProperty("ApplicationPath"));
                driver = new AppiumDriver<MobileElement>(new URL(prop.getProperty("DeviceUrl")), iPadCapabilities);
                System.out.println("###........CONNECTION SUCCESSFULLY ESTABLISHED WITH NODE DEVICE........###");
                JavascriptExecutor js1 = (JavascriptExecutor) driver;
                js1.executeScript("target.setDeviceOrientation(UIA_DEVICE_ORIENTATION_LANDSCAPELEFT);");
                break;

            default:
                throw new IllegalArgumentException("Browser \"" + browser + "\" isn't supported.");
        }
    }

    public void waitForPageLoad(int timeout) {
        ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";");
    }

    public By getElementWithLocator(String WebElement) throws Exception {
        String locatorTypeAndValue = WebElement;
        String[] locatorTypeAndValueArray = locatorTypeAndValue.split(",");
        String locatorType = locatorTypeAndValueArray[0].trim();
        String locatorValue = locatorTypeAndValueArray[1].trim();
        switch (locatorType.toUpperCase()) {
            case "ID":
                return By.id(locatorValue);
            case "NAME":
                return By.name(locatorValue);
            case "TAGNAME":
                return By.tagName(locatorValue);
            case "LINKTEXT":
                return By.linkText(locatorValue);
            case "PARTIALLINKTEXT":
                return By.partialLinkText(locatorValue);
            case "XPATH":
                return By.xpath(locatorValue);
            case "CSS":
                return By.cssSelector(locatorValue);
            case "CLASSNAME":
                return By.className(locatorValue);
            default:
                return null;
        }
    }

    public WebElement FindAnElement(String WebElement) throws Exception {
        return driver.findElement(getElementWithLocator(WebElement));
    }

    public void PerformActionOnElement(String WebElement, String Action, String Text) throws Exception {
        switch (Action) {
            case "Click":
                FindAnElement(WebElement).click();
                break;
            case "Type":
                FindAnElement(WebElement).sendKeys(Text);
                break;
            case "Clear":
                FindAnElement(WebElement).clear();
                break;
            case "WaitForElementDisplay":
                waitForCondition("Presence", WebElement, 60);
                break;
            case "WaitForElementClickable":
                waitForCondition("Clickable", WebElement, 60);
                break;
            case "ElementNotDisplayed":
                waitForCondition("NotPresent", WebElement, 60);
                break;
            default:
                throw new IllegalArgumentException("Action \"" + Action + "\" isn't supported.");
        }
    }

    public void waitForCondition(String TypeOfWait, String WebElement, int Time) {
        try {
            Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(Time)).pollingEvery(Duration.ofSeconds(5)).ignoring(Exception.class);
            switch (TypeOfWait) {
                case "PageLoad":
                    wait.until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));
                    break;
                case "Clickable":
                    wait.until(ExpectedConditions.elementToBeClickable(FindAnElement(WebElement)));
                    break;
                case "Presence":
                    wait.until(ExpectedConditions.presenceOfElementLocated(getElementWithLocator(WebElement)));
                    break;
                case "Visibility":
                    wait.until(ExpectedConditions.visibilityOfElementLocated(getElementWithLocator(WebElement)));
                    break;
                case "NotPresent":
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(getElementWithLocator(WebElement)));
                    break;
                default:
                    Thread.sleep(Time * 1000);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("wait For Condition \"" + TypeOfWait + "\" isn't supported.");
        }
    }

}