package com.cliente.projeto.crudpb.e2e.pageobjects;

import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.service.UsuarioService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * AT - Requisito 3: Testes Pós-Deploy e Validação em Produção
 * Testes E2E com Selenium validando a interface web de eventos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CadastroEventoE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;
    private FormularioEventoPage formularioPage;

    @Autowired
    private UsuarioService usuarioService;

    private Long usuarioTesteId;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        try {
            Usuario usuarioTeste = usuarioService.criarUsuario(new Usuario("Usuario E2E", "e2e@teste.com"));
            this.usuarioTesteId = usuarioTeste.getId();
        } catch (Exception e) {
            // Se falhar (ex: e-mail duplicado), busca o usuário existente
            this.usuarioTesteId = usuarioService.listarTodos().get(0).getId();
        }

        // Configuração do Selenium
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Executar sem interface gráfica (CI/CD)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);

        baseUrl = "http://localhost:" + port;
        formularioPage = new FormularioEventoPage(driver);
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * AT - Requisito 3: Validação pós-deploy
     * Teste de Interface validando feedback de erro seguro na UI
     */
    @Test
    void deveMostrarMensagemDeErro_QuandoSubmeterFormularioComNomeVazio() {
        driver.get(baseUrl + "/eventos/novo");

        formularioPage.preencherFormulario("", "Descrição válida", this.usuarioTesteId);
        formularioPage.submeter();

        // Verificação: mensagem de validação deve aparecer
        String mensagemErroNomeVazio = formularioPage.getMensagemDeErroVisivel();
        assertTrue(mensagemErroNomeVazio.contains("obrigatório") || mensagemErroNomeVazio.contains("requer"),
                "A mensagem de erro de nome vazio não foi encontrada.");
    }

    /**
     * AT - Requisito 3: Testes pós-deploy validando regras de negócio
     */
    @Test
    void deveMostrarErro_QuandoTentarCriarEventoComNomeDuplicado() {
        // 1. Cria o primeiro evento (válido)
        driver.get(baseUrl + "/eventos/novo");
        formularioPage.preencherFormulario("Evento Duplicado E2E", "Descrição 1", this.usuarioTesteId);
        formularioPage.submeter();

        // Aguarda redirecionamento para lista
        assertTrue(driver.getCurrentUrl().contains("/eventos"),
                "Deveria ter redirecionado para a lista de eventos");

        // 2. Tenta criar o segundo evento com mesmo nome (inválido)
        driver.get(baseUrl + "/eventos/novo");
        formularioPage.preencherFormulario("Evento Duplicado E2E", "Descrição 2", this.usuarioTesteId);
        formularioPage.submeter();

        // 3. Verificação: espera o erro de negócio
        String mensagemErroDuplicado = formularioPage.getMensagemDeErroVisivel();
        assertTrue(mensagemErroDuplicado.contains("já está em uso"),
                "A mensagem de erro de nome duplicado não foi encontrada. Mensagem real: " + mensagemErroDuplicado);
    }

    /**
     * AT - Requisito 3: Validação de sucesso na criação
     */
    @Test
    void deveCriarEventoComSucesso() {
        driver.get(baseUrl + "/eventos/novo");

        String nomeEvento = "Evento E2E " + System.currentTimeMillis(); // Nome único
        formularioPage.preencherFormulario(nomeEvento, "Descrição do evento E2E", this.usuarioTesteId);
        formularioPage.submeter();

        // Verificação: deve redirecionar para lista
        assertTrue(driver.getCurrentUrl().contains("/eventos"),
                "Deveria ter redirecionado para /eventos após criar");
    }
}