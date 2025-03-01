package net.mf;

import java.awt.image.RenderedImage;
import java.net.URI;
import java.time.Duration;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.hp.lft.report.CaptureLevel;
import com.hp.lft.report.ModifiableReportConfiguration;
import com.hp.lft.report.Reporter;
import com.hp.lft.report.Status;

//added to access the LeanFT reporting capabilities
import com.hp.lft.sdk.ModifiableSDKConfiguration;
import com.hp.lft.sdk.SDK;
import com.hpe.leanft.selenium.By;
import com.hpe.leanft.selenium.Utils;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumTest  {
	
    // This script was created against AOS 1.1.3.  Since it uses Xpath, you may need to update the script
    // if using against a different version.
	private static final String ADV_WEBSITE  = "http://sac-hvm03325.swinfra.net:8000/#/";
    //private static final String ADV_WEBSITE  = "http://nimbusserver.aos.com:8000/#/";
    //private static final String ADV_WEBSITE  = "http://www.advantageonlineshopping.com";

    //You will need to have an account created in AOS and will need to supply the credentials
    //These are known defaults as of 2018/sep/12
    private static final String ADV_LOGIN    = "Mercury"; //"insert login name here";
    private static final String ADV_PASSWORD = "Mercury"; //"insert password here";

    private static WebDriver driver;

	public SeleniumTest() {
	//Change this constructor to private if you supply your own public constructor
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        driver = new ChromeDriver(options);

        // In this case a basic Selenium enabled test using the Junit framework
        // This is base on the LeanFT 14.03 release https://admhelp.microfocus.com/leanft/en/latest/HelpCenter/Content/HowTo/CustomFrameworks.htm
        ModifiableSDKConfiguration config = new ModifiableSDKConfiguration();
        config.setServerAddress(new URI("ws://localhost:5095"));
        SDK.init(config);

        ModifiableReportConfiguration rptConfig = new ModifiableReportConfiguration();
        rptConfig.setSnapshotsLevel(CaptureLevel.All);
        Reporter.init(rptConfig);
        
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
        //Clean up and dispose of the driver
        //Good explanation of close, quit, dispose here http://stackoverflow.com/questions/15067107/difference-between-webdriver-dispose-close-and-quit
        driver.quit();
        Reporter.generateReport();
        SDK.cleanup();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

    @Test
    public void purchaseTablet() throws Exception {
        driver.get(ADV_WEBSITE);

        Reporter.reportEvent("Open Website", "Opening website: "+ADV_WEBSITE);

        //driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //Login to Advantage
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/header/nav/ul/li[3]/a/a")));
        Utils.highlight(driver.findElement(By.xpath("/html/body/header/nav/ul/li[3]/a/a")), 3000);
        driver.findElement(By.xpath("/html/body/header/nav/ul/li[3]/a/a")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/login-modal/div/div/div[3]/sec-form/sec-view[1]/div/input")));
        Utils.highlight(driver.findElement(By.xpath("/html/body/login-modal/div/div/div[3]/sec-form/sec-view[1]/div/input")), 1000);
        driver.findElement(By.xpath("/html/body/login-modal/div/div/div[3]/sec-form/sec-view[1]/div/input")).sendKeys(ADV_LOGIN);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/login-modal/div/div/div[3]/sec-form/sec-view[2]/div/input")));
        Utils.highlight(driver.findElement(By.xpath("/html/body/login-modal/div/div/div[3]/sec-form/sec-view[2]/div/input")), 1000);
        driver.findElement(By.xpath("/html/body/login-modal/div/div/div[3]/sec-form/sec-view[2]/div/input")).sendKeys(ADV_PASSWORD);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("SIGN IN")));
        Utils.highlight(driver.findElement(By.visibleText("SIGN IN")), 3000);
        driver.findElement(By.visibleText("SIGN IN")).click();
        Thread.sleep(2000);

        //Click on Tablets
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("TABLETS")));
        Utils.highlight(driver.findElement(By.visibleText("TABLETS")), 1000);
        RenderedImage img = Utils.getSnapshot(driver.findElement(By.visibleText("TABLETS")));
        Reporter.reportEvent("TABLETS","Found", Status.Passed, img);
        driver.findElement(By.visibleText("TABLETS")).click();

        //Click on specific tablet
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("HP Pro Tablet 608 G1")));
        Utils.highlight(driver.findElement(By.visibleText("HP Pro Tablet 608 G1")), 1000);
        driver.findElement(By.visibleText("HP Pro Tablet 608 G1")).click();
        Thread.sleep(3000);

        //Add Tablet to cart
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("ADD TO CART")));
        Utils.highlight(driver.findElement(By.visibleText(("ADD TO CART"))), 1000);
        driver.findElement(By.visibleText("ADD TO CART")).click();

        //Go to Checkout
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText(Pattern.compile("CHECKOUT \\(\\$*"))));
        Utils.highlight(driver.findElement(By.visibleText(Pattern.compile("CHECKOUT \\(\\$*"))), 1000);
        driver.findElement(By.visibleText(Pattern.compile("CHECKOUT \\(\\$*"))).click();

        //Checkout - Use XPath as visibleText was not working correctly on BlueShift
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"next_btn\"]")));
        Utils.highlight(driver.findElement(By.xpath("//*[@id=\"next_btn\"]")), 1000);
        driver.findElement(By.xpath("//*[@id=\"next_btn\"]")).click();

        String path ="//*[@id=\"paymentMethod\"]/div/div[2]/sec-form/sec-view[1]/div/input";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(path)));
        Utils.highlight(driver.findElement(By.xpath(path)), 1000);
        driver.findElement(By.xpath(path)).clear();
        driver.findElement(By.xpath(path)).sendKeys(ADV_LOGIN);

        path = "//*[@id=\"paymentMethod\"]/div/div[2]/sec-form/sec-view[2]/div/input";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(path)));
        Utils.highlight(driver.findElement(By.xpath(path)), 1000);
        driver.findElement(By.xpath(path)).clear();
        driver.findElement(By.xpath(path)).sendKeys(ADV_PASSWORD+"1");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("PAY NOW")));
        Utils.highlight(driver.findElement(By.visibleText("PAY NOW")), 1000);
        driver.findElement(By.visibleText("PAY NOW")).click();

        //Logout of Advantage
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuUser")));
        Utils.highlight(driver.findElement(By.id("menuUser")), 1000);
        driver.findElement(By.id("menuUser")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.visibleText("Sign out")));
        Utils.highlight(driver.findElement(By.visibleText("Sign out")), 1000);
        driver.findElement(By.visibleText("Sign out")).click();

        //Added sleep here to give time to see the selection
        Thread.sleep(3000);
    }
}