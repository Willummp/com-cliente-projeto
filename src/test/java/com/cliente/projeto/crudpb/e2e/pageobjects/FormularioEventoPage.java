package com.cliente.projeto.crudpb.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FormularioEventoPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Seletores dos elementos
    private By campoNome = By.id("nome");
    private By campoDescricao = By.id("descricao");
    private By botaoSalvar = By.id("btn-salvar");

    private By selectUsuario = By.id("usuarioId");

    // Busca por Alerta Global OU Erro de Campo
    private By containerMensagemErro = By.cssSelector(".alert-danger, .text-danger");

    public FormularioEventoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void preencherFormulario(String nome, String descricao, Long usuarioId) {
        // Espera explícita para garantir que o formulário carregou
        wait.until(ExpectedConditions.visibilityOfElementLocated(campoNome));

        driver.findElement(campoNome).clear();
        driver.findElement(campoNome).sendKeys(nome);

        driver.findElement(campoDescricao).clear();
        driver.findElement(campoDescricao).sendKeys(descricao);

        // Se um usuarioId for fornecido, selecione-o no dropdown
        if (usuarioId != null) {
            Select dropdownUsuario = new Select(driver.findElement(selectUsuario));
            dropdownUsuario.selectByValue(usuarioId.toString());
        }
    }

    public void submeter() {
        By seletorBotaoSalvar = By.cssSelector("button[type='submit']");
        driver.findElement(seletorBotaoSalvar).click();
    }

    public String getMensagemDeErroVisivel() {
        // Espera o container de erro ficar visível
        WebElement erroEl = wait.until(ExpectedConditions.visibilityOfElementLocated(containerMensagemErro));
        return erroEl.getText();
    }
}