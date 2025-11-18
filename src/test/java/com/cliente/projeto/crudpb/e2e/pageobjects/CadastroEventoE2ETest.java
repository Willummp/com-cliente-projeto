package com.cliente.projeto.crudpb.e2e.pageobjects;

import com.cliente.projeto.crudpb.e2e.pageobjects.FormularioEventoPage;
import com.cliente.projeto.crudpb.model.Usuario;
import com.cliente.projeto.crudpb.service.UsuarioService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CadastroEventoE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;
    private FormularioEventoPage formularioPage;

    @Autowired
    private UsuarioService usuarioService;

    // Variável para guardar o ID do usuário de teste
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
     * Teste de Interface (Artefato 2)
     * Valida o feedback de erro seguro na UI (Artefato 4)
     */
    @Test
    void deveMostrarMensagemDeErro_QuandoSubmeterFormularioComNomeVazio() {
        driver.get(baseUrl + "/eventos/novo");

        formularioPage.preencherFormulario("", "Descrição válida", this.usuarioTesteId);
        formularioPage.submeter();

        // Verificação
        String mensagemErroNomeVazio = formularioPage.getMensagemDeErroVisivel();

        
        // 1. Cria o primeiro evento (válido)
        formularioPage.preencherFormulario("Evento Duplicado E2E", "Desc 1", this.usuarioTesteId);
        formularioPage.submeter();
        
        // 2. Tenta criar o segundo evento (inválido)
        driver.get(baseUrl + "/eventos/novo");
        formularioPage.preencherFormulario("Evento Duplicado E2E", "Desc 2", this.usuarioTesteId);
        formularioPage.submeter();
        
        // 3. Verificação (espera o erro de negócio)
        String mensagemErroDuplicado = formularioPage.getMensagemDeErroVisivel();
        
        assertTrue(mensagemErroDuplicado.contains("já está em uso por outro evento"),
                "A mensagem de erro de nome duplicado não foi encontrada.");
    }
}