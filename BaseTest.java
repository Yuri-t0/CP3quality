package br.com.cp3.scrumpoker;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;


public abstract class BaseTest {

    protected WebDriver driver;

    protected static final String BASE_URL = "https://www.scrumpoker.online";
    protected static final String JOIN_URL  = BASE_URL + "/join/667041?token=af415b0a8f14b4be2a99724a189ac6";
    protected static final int    TIMEOUT   = 10; 

   
    @BeforeAll
    static void configurarDriverGlobal() {
        WebDriverManager.chromedriver().setup();
    }

  
    @BeforeEach
    void abrirNavegador() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");       
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1366,768");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEOUT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

   
    @AfterEach
    void fecharNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }
}
