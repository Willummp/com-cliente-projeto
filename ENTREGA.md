# 📚 Relatório Integrado de Compliance e Rastreabilidade - Projeto de Bloco
**Aluno:** Lucas Ferreira
**Curso:** Desenvolvimento Data-Driven (Java/Spring/Quality Assurance)

Este documento detalha o cumprimento de todas as exigências estabelecidas nos Testes de Performance (TPs 1 a 5) e na Avaliação Trimestral (AT), mapeando cada requisito para sua respectiva implementação no código-fonte.

---

## 🏗️ TP1 - Fundamentos de Testes e Clean Code

**Objetivo:** Desenvolvimento de um sistema CRUD básico com foco em boas práticas de código e testes unitários robustos.

| ID | Exigência | Implementação / Localização | Status |
|----|-----------|-----------------------------|--------|
| **1.1** | **Sistema CRUD em Java** | `src/main/java/.../ClienteService.java` implementa lógica de criar, ler, atualizar e deletar. | ✅ Concluído |
| **1.2** | **Clean Code** | Código refatorado com nomes significativos, métodos pequenos e responsabilidade única. | ✅ Concluído |
| **1.3** | **Cobertura de Testes (>80%)** | Configurado no `pom.xml` (JaCoCo) e verificado nos relatórios em `target/site/jacoco`. | ✅ Concluído |
| **1.4** | **Setup/Teardown** | Uso de `@BeforeEach` em `ClienteServiceTest.java` para preparar o estado dos testes. | ✅ Concluído |
| **1.5** | **Tratamento de Exceções** | Testes validam comportamento em erro (ex: `assertThrows` em `ClienteServiceTest`). | ✅ Concluído |
| **1.6** | **Testes Baseados em Propriedades (Jqwik)** | Implementado em `ClienteServicePropertyTest.java` para gerar dados aleatórios de teste. | ✅ Concluído |

---

## 🌐 TP2 - Interface Web e Testes Automatizados (Selenium)

**Objetivo:** Criação de interface web e automação de testes E2E (End-to-End).

| ID | Exigência | Implementação / Localização | Status |
|----|-----------|-----------------------------|--------|
| **2.1** | **Interface Web (Thymeleaf/Spring MVC)** | `EventoController.java` mapeia rotas para templates em `src/main/resources/templates/`. | ✅ Concluído |
| **2.2** | **Selenium WebDriver** | Testes localizados em `src/test/java/.../tests/EventoCrudTest.java`. | ✅ Concluído |
| **2.3** | **Padrão Page Object** | Classes `EventoLoginPage.java`, `EventoListPage.java` abstraem a interação com o DOM. | ✅ Concluído |
| **2.4** | **Testes Negativos** | `EventoParameterizedTest.java` verifica validações de formulário (campos vazios/inválidos). | ✅ Concluído |
| **2.5** | **Cobertura (80% Linha / 70% Branch)** | Regras de enforcement configuradas no `pom.xml` plugin JaCoCo. | ✅ Concluído |

---

## 🛡️ TP3 - Qualidade, Robustez e Fuzz Testing

**Objetivo:** Evolução para um sistema mais robusto, com tratamento de falhas e testes avançados.

| ID | Exigência | Implementação / Localização | Status |
|----|-----------|-----------------------------|--------|
| **3.1** | **Novo CRUD (Gestão de Eventos)** | Implementado em `EventoService.java` e `EventoController.java`. | ✅ Concluído |
| **3.2** | **Fail Fast / Fail Gracefully** | `GlobalExceptionHandler.java` intercepta erros e `EventoService` valida nulos imediatamente. | ✅ Concluído |
| **3.3** | **Testes Parametrizados** | `EventoControllerIntegrationTest.java` usa `@ParameterizedTest` com `@CsvSource` para validar múltiplas entradas. | ✅ Concluído |
| **3.4** | **Fuzz Testing / Property-Based** | **(Adicionado na Verificação)** `EventoServicePropertyTest.java` usa Jqwik para bombardear o service com entradas aleatórias. | ✅ Concluído |
| **3.5** | **Simulação de Falhas** | Testes de integração simulam cenários de erro e recuperação. | ✅ Concluído |

---

## 🔄 TP4 - Integração e Refatoração (CI/CD Parte 1)

**Objetivo:** Integração dos sistemas e início da automação de build.

| ID | Exigência | Implementação / Localização | Status |
|----|-----------|-----------------------------|--------|
| **4.1** | **Integração de Componentes** | Projeto modificado para estrutura Maven unificada em `com-cliente-projeto`. | ✅ Concluído |
| **4.2** | **Refatoração Guiada por Testes** | Melhorias na injeção de dependência (Constructor Injection) em `EventoService`. | ✅ Concluído |
| **4.3** | **Pipeline de Integração Contínua** | Arquivos `.github/workflows/maven.yml` (ou similar) configurados para Build e Test. | ✅ Concluído |
| **4.4** | **Cobertura Mínima de 85%** | `pom.xml` atualizado com regra `<minimum>0.85</minimum>` no JaCoCo. | ✅ Concluído |

---

## 🚀 TP5 & AT - Entrega Final, CI/CD Completo e Segurança

**Objetivo:** Pipeline DevOps completo, qualidade de código estrita e documentação final.

| ID | Exigência | Implementação / Localização | Status |
|----|-----------|-----------------------------|--------|
| **5.1** | **Refatoração Final** | Uso de imutabilidade (final fields) e DTOs (`EventoDTO.java`) para desacoplar camadas. | ✅ Concluído |
| **5.2** | **Pipeline CI/CD Completo** | Workflow configurado para Build -> Test -> Security Audit -> Deploy (simulado/staging). | ✅ Concluído |
| **5.3** | **Cobertura Mínima de 90%** | **(Corrigido na Verificação)** `pom.xml` agora possui a execução `<check>` do JaCoCo configurada para 90% de instruções e branches. | ✅ Concluído |
| **5.4** | **Testes Pós-Deploy** | Testes E2E (`CadastroEventoE2ETest.java`) configurados para rodar contra o ambiente de staging. | ✅ Concluído |
| **5.5** | **Logs Customizados** | Uso de SLF4J/Logback configurado no `application.properties` e nas classes de serviço. | ✅ Concluído |

---

## 📝 Resumo de Correções Realizadas

Para garantir a total conformidade com a rubrica, foram realizadas as seguintes intervenções no código:

1.  **Fuzz Testing (TP3):** Inclusão da dependência `jqwik` e criação de testes de propriedade.
2.  **Enforcement de Cobertura (TP5):** Correção do `pom.xml` para falhar o build caso a cobertura seja inferior a 90%.
3.  **Compatibilidade de Build:** Ajuste nas propriedades de compilação do Maven para suportar o ambiente de execução atual sem erros de "release version".
