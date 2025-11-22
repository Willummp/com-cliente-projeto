package com.cliente.projeto.crudpb.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * AT - Requisito 3: Page Object Model para Listagem de Eventos
 * Encapsula interações com a página de listagem de eventos
 */
public class ListaEventosPage {

  private final WebDriver driver;
  private final WebDriverWait wait;

  // Seletores
  private final By tabelaEventos = By.cssSelector("table tbody tr");
  private final By linkNovoEvento = By.cssSelector("a[href='/eventos/novo']");
  private final By mensagemSucesso = By.className("alert-success");

  public ListaEventosPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
  }

  public int contarEventos() {
    // Espera a tabela estar visível antes de contar
    try {
      wait.until(ExpectedConditions.visibilityOfElementLocated(tabelaEventos));
    } catch (Exception e) {
      // Se der timeout (tabela vazia), retorna 0
      return 0;
    }
    List<WebElement> linhas = driver.findElements(tabelaEventos);
    return linhas.size();
  }

  public void clicarNovoEvento() {
    driver.findElement(linkNovoEvento).click();
  }

  public void clicarEditar(int indice) {
    List<WebElement> linhas = driver.findElements(tabelaEventos);
    if (indice < linhas.size()) {
      WebElement linha = linhas.get(indice);
      WebElement botaoEditar = linha.findElement(By.linkText("Editar"));
      botaoEditar.click();
    }
  }

  public void clicarDeletar(int indice) {
    List<WebElement> linhas = driver.findElements(tabelaEventos);
    if (indice < linhas.size()) {
      WebElement linha = linhas.get(indice);
      WebElement botaoDeletar = linha.findElement(By.linkText("Deletar"));
      botaoDeletar.click();
    }
  }

  public String getMensagemSucesso() {
    WebElement mensagem = wait.until(ExpectedConditions.visibilityOfElementLocated(mensagemSucesso));
    return mensagem.getText();
  }

  public boolean existeEvento(String nomeEvento) {
    List<WebElement> linhas = driver.findElements(tabelaEventos);
    for (WebElement linha : linhas) {
      if (linha.getText().contains(nomeEvento)) {
        return true;
      }
    }
    return false;
  }
}
