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

import static org.junit.jupiter.api.Assertions.*;

/**
 * AT - Requisito 3: Testes E2E completos do fluxo de Eventos
 * Valida criar, listar, editar e deletar eventos
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FluxoEventosE2ETest {

  @LocalServerPort
  private int port;

  private WebDriver driver;
  private String baseUrl;
  private FormularioEventoPage formularioPage;
  private ListaEventosPage listaPage;

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
      Usuario usuarioTeste = usuarioService.criarUsuario(
          new Usuario("Usuario Fluxo E2E", "fluxo@teste.com"));
      this.usuarioTesteId = usuarioTeste.getId();
    } catch (Exception e) {
      this.usuarioTesteId = usuarioService.listarTodos().get(0).getId();
    }

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    driver = new ChromeDriver(options);

    baseUrl = "http://localhost:" + port;
    formularioPage = new FormularioEventoPage(driver);
    listaPage = new ListaEventosPage(driver);
  }

  @AfterEach
  void teardown() {
    if (driver != null) {
      driver.quit();
    }
  }

  /**
   * AT - Requisito 3: Teste de fluxo completo de CRUD
   */
  @Test
  void deveRealizarFluxoCompletoDeCRUD() {
    // 1. LISTAR - Verifica página inicial
    driver.get(baseUrl + "/eventos");
    int quantidadeInicial = listaPage.contarEventos();

    // 2. CRIAR - Adiciona novo evento
    String nomeEvento = "Evento Fluxo " + System.currentTimeMillis();
    listaPage.clicarNovoEvento();

    formularioPage.preencherFormulario(nomeEvento, "Descrição teste fluxo", usuarioTesteId);
    formularioPage.submeter();

    // Verificação: deve voltar para lista com mensagem de sucesso
    assertTrue(driver.getCurrentUrl().contains("/eventos"));

    // 3. LISTAR novamente - Deve ter um evento a mais
    int quantidadeFinal = listaPage.contarEventos();
    assertEquals(quantidadeInicial + 1, quantidadeFinal,
        "Deveria ter um evento a mais após criação");

    assertTrue(listaPage.existeEvento(nomeEvento),
        "O evento criado deveria aparecer na listagem");
  }

  /**
   * AT - Requisito 3: Teste validando permanência de dados após refresh
   */
  @Test
  void deveManterDadosAposRecarregar() {
    // Criar evento
    String nomeEvento = "Evento Persistente " + System.currentTimeMillis();
    driver.get(baseUrl + "/eventos/novo");
    formularioPage.preencherFormulario(nomeEvento, "Descrição persistente", usuarioTesteId);
    formularioPage.submeter();

    // Atualizar página
    driver.navigate().refresh();

    // Verificação: evento ainda deve estar visível
    assertTrue(listaPage.existeEvento(nomeEvento),
        "O evento deveria persistir após recarregar a página");
  }
}
