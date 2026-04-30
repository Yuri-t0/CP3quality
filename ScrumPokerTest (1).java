package br.com.cp3.scrumpoker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CP3 – Testes End-to-End Automatizados
 * Site: https://www.scrumpoker.online
 *
 * CT01 – SUCESSO:  Entrar em sessão com nome válido
 * CT02 – EXCEÇÃO:  Tentar entrar na sessão sem informar o nome (validação de campo)
 * CT03 – EXCEÇÃO:  Acessar URL de sessão com ID inexistente (erro de rota/navegação)
 * CT04 – EXCEÇÃO:  Página principal fora do ar ou sem elementos essenciais
 */
@DisplayName("CP3 | Testes E2E – ScrumPoker Online")
public class ScrumPokerTest extends BaseTest {

    // =========================================================================
    // CT01 – SUCESSO: Entrar na sessão com nome válido
    // =========================================================================

    /**
     * Fluxo : Acessar URL de join → preencher nome → confirmar.
     * Esperado: Usuário entra na sala (URL muda ou elemento da sala fica visível).
     */
    @Test
    @DisplayName("CT01 – Sucesso: Entrar em sessão com nome válido")
    void ct01_EntrarNaSessaoComNomeValido() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(JOIN_URL);

        WebElement campoNome = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='text'], input[placeholder*='name'], input[id*='name']")
                )
        );
        campoNome.clear();
        campoNome.sendKeys("TestadorCP3");

        WebElement btnEntrar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit'], button.btn-primary, input[type='submit']")
                )
        );
        btnEntrar.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/session/"),
                ExpectedConditions.urlContains("/room/"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".poker-card, .card-value, .session-room, [class*='card']")
                )
        ));

        String urlAtual = driver.getCurrentUrl();
        assertFalse(
                urlAtual.contains("/join/"),
                "CT01 FALHOU – Esperava sair da tela de join, mas ainda está em: " + urlAtual
        );
    }

    // =========================================================================
    // CT02 – EXCEÇÃO: Validação de campo obrigatório
    // Foco: comportamento do FORMULÁRIO quando o campo está vazio
    // =========================================================================

    /**
     * Fluxo : Acessar URL de join → deixar nome vazio → clicar em confirmar.
     * Esperado: Sistema bloqueia o avanço via validação HTML5, mensagem de erro
     *           ou mantendo o usuário na mesma tela de join.
     */
    @Test
    @DisplayName("CT02 – Exceção: Tentar entrar na sessão sem informar o nome")
    void ct02_EntrarSemNome() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(JOIN_URL);

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='text'], input[placeholder*='name'], input[id*='name']")
                )
        );

        WebElement btnEntrar = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[type='submit'], button.btn-primary, input[type='submit']")
                )
        );
        btnEntrar.click();

        String urlApos = driver.getCurrentUrl();

        boolean continuaNaTelaDeJoin = urlApos.contains("/join/");
        boolean exibeMensagemDeErro  = !driver.findElements(
                By.cssSelector(".error, .alert-danger, [class*='error'], [class*='invalid']")
        ).isEmpty();
        boolean campoHtmlInvalido = !driver.findElements(
                By.cssSelector("input:invalid")
        ).isEmpty();

        assertTrue(
                continuaNaTelaDeJoin || exibeMensagemDeErro || campoHtmlInvalido,
                "CT02 FALHOU – Sistema deveria bloquear o avanço com nome vazio, mas avançou para: " + urlApos
        );
    }

    // =========================================================================
    // CT03 – EXCEÇÃO: Rota/URL inexistente
    // Foco: comportamento de NAVEGAÇÃO para um recurso que não existe no servidor
    // =========================================================================

    /**
     * Fluxo : Navegar para uma URL de sessão com ID e token que não existem.
     * Esperado: Sistema exibe erro 404/mensagem de sessão não encontrada
     *           ou redireciona para a home/listagem de sessões.
     */
    @Test
    @DisplayName("CT03 – Exceção: Acessar sessão com ID/token inexistente")
    void ct03_AcessarSessaoInexistente() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        String urlInvalida = BASE_URL + "/join/999999999?token=tokeninvalido00000000000000000000";
        driver.get(urlInvalida);

        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

        String urlAtual   = driver.getCurrentUrl();
        String paginaHTML = driver.getPageSource().toLowerCase();

        boolean redirecionouParaHome    = urlAtual.equals(BASE_URL + "/") || urlAtual.equals(BASE_URL);
        boolean redirecionouParaSessoes = urlAtual.contains("/sessions");
        boolean exibeMensagemDeErro     = paginaHTML.contains("not found")
                || paginaHTML.contains("invalid")
                || paginaHTML.contains("error")
                || paginaHTML.contains("não encontrado")
                || paginaHTML.contains("404");

        assertTrue(
                redirecionouParaHome || redirecionouParaSessoes || exibeMensagemDeErro,
                "CT03 FALHOU – Sessão inválida deveria retornar erro ou redirecionar. URL final: " + urlAtual
        );
    }

    // =========================================================================
    // CT04 – EXCEÇÃO: Disponibilidade e integridade da página principal
    // Foco: comportamento do SISTEMA como um todo — site no ar, título correto,
    //       elementos de navegação presentes. Completamente diferente dos fluxos
    //       de formulário (CT02) e rota (CT03).
    // =========================================================================

    /**
     * Fluxo : Acessar a página inicial do ScrumPoker Online.
     * Esperado: Página carrega com título reconhecível ("scrum"/"poker") e
     *           ao menos um link ou botão visível na interface.
     * Exceção : Falha se o site estiver fora do ar, com título errado ou
     *           sem elementos de navegação.
     */
    @Test
    @DisplayName("CT04 – Exceção: Página principal deve estar acessível e com elementos essenciais")
    void ct04_PaginaPrincipalAcessivelEComElementos() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(BASE_URL);
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete';"));

        String titulo = driver.getTitle().toLowerCase();
        assertFalse(
                titulo.isEmpty(),
                "CT04 FALHOU – Título da página está vazio; possível falha de carregamento."
        );
        assertTrue(
                titulo.contains("scrum") || titulo.contains("poker") || titulo.contains("planning"),
                "CT04 FALHOU – Título inesperado. Esperava 'scrum' ou 'poker', obteve: '" + titulo + "'"
        );

        List<WebElement> elementos = driver.findElements(By.cssSelector("a, button"));
        assertFalse(
                elementos.isEmpty(),
                "CT04 FALHOU – Nenhum link ou botão na página principal; site pode estar offline."
        );

        String urlFinal = driver.getCurrentUrl();
        assertFalse(
                urlFinal.contains("error") || urlFinal.contains("404") || urlFinal.contains("offline"),
                "CT04 FALHOU – Página principal redirecionou para erro: " + urlFinal
        );
    }
}
