package br.com.cp3.scrumpoker;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * Classe base para os testes E2E do ScrumPoker Online.
 * Configura e encerra o WebDriver antes/após cada teste.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    protected static final String BASE_URL = "https://www.scrumpoker.online";
    protected static final String JOIN_URL  = BASE_URL + "/join/667041?token=af415b0a8f14b4be2a99724a189ac6";
    protected static final int    TIMEOUT   = 10; // segundos

    /**
     * Configuração global: baixa/verifica o ChromeDriver uma única vez.
     */
    @BeforeAll
    static void configurarDriverGlobal() {
        WebDriverManager.chromedriver().setup();
    }

    /**
     * Antes de cada teste: abre o Chrome em modo headless (sem abrir janela).
     * Para rodar COM janela, remova a opção "--headless=new".
     */
    @BeforeEach
    void abrirNavegador() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");        // rodar sem interface gráfica (CI/CD friendly)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1366,768");
        options.addArguments("--disable-gpu");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEOUT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    /**
     * Após cada teste: fecha o navegador.
     */
    @AfterEach
    void fecharNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }
}
