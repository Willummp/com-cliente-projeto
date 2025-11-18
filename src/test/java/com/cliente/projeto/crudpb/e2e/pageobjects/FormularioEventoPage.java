package com.cliente.projeto.crudpb.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select; // Importado
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FormularioEventoPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Seletores dos elementos
    private By campoNome = By.id("nome");
    private By campoDescricao = By.id("descricao");
    private By botaoSalvar = By.id("btn-salvar"); // (O seu HTML não tem este ID, vamos corrigir)

    /*
    ==================== REATORAÇÃO (Req 4) ====================
    Adicionando o seletor para o novo dropdown de usuário.
    ============================================================
    */
    private By selectUsuario = By.id("usuarioId");

    /*
    ==================== CORREÇÃO (Bug TP3) ====================
    O seu PageObject procurava por "validation-errors", mas
    o HTML de refatoração que fiz (e o seu teste de integração)
    mostra os erros de DTO (como "O nome é obrigatório.")
    embaixo de cada campo, com a classe 'text-danger'.
    
    Para o teste E2E, vamos padronizar e procurar pela
    mensagem de erro de *regra de negócio* (ex: nome duplicado),
    que aparece no 'alert-danger'.
    ============================================================
    */
    private By containerMensagemErro = By.className("alert-danger"); // Buscando pelo Alerta de Erro


    public FormularioEventoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    /*
    ==================== REATORAÇÃO (Req 4) ====================
    O método de preenchimento foi atualizado para
    incluir o preenchimento do novo campo de usuário.
    ============================================================
    */
    public void preencherFormulario(String nome, String descricao, Long usuarioId) {
        driver.findElement(campoNome).clear();
        driver.findElement(campoNome).sendKeys(nome);

        driver.findElement(campoDescricao).clear();
        driver.findElement(campoDescricao).sendKeys(descricao);

        // Se um usuarioId for fornecido, selecione-o no dropdown
        if (usuarioId != null) {
            // Selenium usa a classe 'Select' para interagir com dropdowns
            Select dropdownUsuario = new Select(driver.findElement(selectUsuario));
            dropdownUsuario.selectByValue(usuarioId.toString());
        }
    }

    public void submeter() {
        By seletorBotaoSalvar = By.cssSelector("button[type='submit']");
        driver.findElement(seletorBotaoSalvar).click();
    }

    public String getMensagemDeErroVisivel() {
        // Espera o container de erro (alert-danger) ficar visível
        WebElement erroEl = wait.until(ExpectedConditions.visibilityOfElementLocated(containerMensagemErro));
        return erroEl.getText();
    }
}