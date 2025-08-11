package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Hooks {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    // Fetch from environment variables or system properties for security
    private static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME") != null ?
            System.getenv("BROWSERSTACK_USERNAME") : System.getProperty("BROWSERSTACK_USERNAME", "YOUR_USERNAME");

    private static final String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY") != null ?
            System.getenv("BROWSERSTACK_ACCESS_KEY") : System.getProperty("BROWSERSTACK_ACCESS_KEY", "YOUR_ACCESS_KEY");

    private static final String BS_URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @Before
    public void setUp() throws Exception {
        String runMode = System.getProperty("runMode", "local");

        if (runMode.equalsIgnoreCase("browserstack")) {
            String browser = System.getProperty("browserName", "chrome");
            String browserVersion = System.getProperty("browserVersion", "latest");
            String os = System.getProperty("os", "Windows");
            String osVersion = System.getProperty("osVersion", "11");
            String deviceName = System.getProperty("deviceName");

            MutableCapabilities caps = new MutableCapabilities();
            caps.setCapability("browserName", browser);
            caps.setCapability("browserVersion", browserVersion);

            Map<String, Object> bstackOptions = new HashMap<>();
            bstackOptions.put("os", os);
            bstackOptions.put("osVersion", osVersion);
            bstackOptions.put("projectName", "El Pais Opinion Automation");
            bstackOptions.put("buildName", "BrowserStack Parallel Build");
            bstackOptions.put("sessionName", "Cucumber Scenario - " + browser + " on " + os);

            if (deviceName != null && !deviceName.isEmpty()) {
                bstackOptions.put("deviceName", deviceName);
            }

            caps.setCapability("bstack:options", bstackOptions);

            WebDriver remoteDriver = new RemoteWebDriver(new URL(BS_URL), caps);
            driver.set(remoteDriver);
            logger.info("Running tests on BrowserStack: {} {} {} {}", browser, browserVersion, os, osVersion);

        } else {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");cla
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-extensions");
            options.addArguments("--user-data-dir=/tmp/chrome-profile-" + System.currentTimeMillis()); // unique profile for CI

            WebDriver localDriver = new ChromeDriver(options);
            localDriver.manage().window().maximize();
            driver.set(localDriver);
            logger.info("Running tests locally on Chrome with temporary profile");
        }
    }

    @After
    public void tearDown() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            currentDriver.quit();
            driver.remove();
            logger.info("Browser session ended");
        }
    }

    @BeforeAll
    public static void cleanOldImages() {
        String folderPath = "downloaded_images";
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            logger.info("Deleted old image: {}", file.getName());
                        } else {
                            logger.warn("Failed to delete old image: {}", file.getName());
                        }
                    }
                }
            }
        } else {
            logger.info("No downloaded_images folder found to clean.");
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }
}
