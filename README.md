# 🎯 Sistema de Gerenciamento de Eventos - AT (Assessment Final)


[![Pipeline CI/CD Completo (TP5)](https://github.com/Willummp/com-cliente-projeto/actions/workflows/ci.yml/badge.svg)](https://github.com/Willummp/com-cliente-projeto/actions/workflows/ci.yml)
![CI/CD Pipeline](https://github.com/Willummp/com-cliente-projeto/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)

## 📋 Sobre o Projeto

Sistema CRUD completo para gerenciamento de eventos e usuários, desenvolvido com **Spring Boot** e **Thymeleaf**. Este projeto implementa as melhores práticas de desenvolvimento, incluindo **Clean Code**, **CI/CD automatizado**, **testes E2E**, e **análise de segurança**.

### 🎓 Contexto Acadêmico

Desenvolvido como entrega final do Assessment (AT) da disciplina, atendendo aos seguintes requisitos:
1. ✅ Refinamento e Clean Code
2. ✅ CI/CD Completo Automatizado
3. ✅ Testes Pós-Deploy com Selenium
4. ✅ Monitoramento e Logs
5. ✅ Documentação Completa

---

## 🏗️ Arquitetura do Sistema

```mermaid
graph TB
    subgraph "Camada de Apresentação"
        A[Thymeleaf Templates] --> B[Controllers]
    end
    
    subgraph "Camada de Negócio"
        B --> C[Services]
        C --> D[Validações]
    end
    
    subgraph "Camada de Dados"
        C --> E[Repositories]
        E --> F[PostgreSQL]
    end
    
    subgraph "Tratamento de Erros"
        G[Global Exception Handler] --> B
        D --> G
    end
    
    style A fill:#e1f5ff
    style B fill:#fff4e1
    style C fill:#e8f5e9
    style E fill:#f3e5f5
    style F fill:#ffebee
```

### 📦 Estrutura de Pacotes

```
com.cliente.projeto.crudpb/
├── controller/          # Controladores MVC
│   ├── EventoController.java
│   └── UsuarioController.java
├── service/            # Lógica de negócio
│   ├── EventoService.java
│   └── UsuarioService.java
├── repository/         # Camada de persistência (JPA)
│   ├── EventoRepository.java
│   └── UsuarioRepository.java
├── model/             # Entidades JPA
│   ├── Evento.java
│   └── Usuario.java
├── exception/         # Exceções customizadas
│   ├── GlobalExceptionHandler.java
│   ├── RecursoNaoEncontradoException.java
│   └── ValidacaoException.java
└── dto/              # Data Transfer Objects
    └── EventoDTO.java
```

---

## 🚀 Guia de Instalação e Execução

### Pré-requisitos

- ☕ **Java 17** ou superior
- 📦 **Maven 3.8+**
- 🐘 **PostgreSQL 15** (ou H2 para desenvolvimento)
- 🌐 **Google Chrome** (para testes E2E)

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/Willummp/com-cliente-projeto.git
cd com-cliente-projeto
```

### 2️⃣ Configurar Banco de Dados

Edite o arquivo `src/main/resources/application.properties.txt`:

```properties
# PostgreSQL (Produção)
spring.datasource.url=jdbc:postgresql://localhost:5432/eventos_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# Ou use H2 (Desenvolvimento)
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.datasource.driver-class-name=org.h2.Driver
```

### 3️⃣ Compilar o Projeto

```bash
mvn clean install
```

### 4️⃣ Executar a Aplicação

```bash
mvn spring-boot:run
```

🌐 **Acesse:** [http://localhost:8080/eventos](http://localhost:8080/eventos)

---

## 🧪 Guia de Testes

### Executar Todos os Testes

```bash
mvn test
```

### Testes Unitários e de Integração

```bash
mvn test -Dtest=*Test
```

### Testes E2E (Selenium)

```bash
mvn test -Dtest=*E2ETest
```

### Relatório de Cobertura de Código (JaCoCo)

```bash
mvn clean verify jacoco:report
```

📊 **Relatório disponível em:** `target/site/jacoco/index.html`

### Estrutura de Testes

```
src/test/java/
├── controller/
│   └── EventoControllerIntegrationTest.java   # Testes de integração
├── service/
│   ├── EventoServiceTest.java                 # Testes unitários
│   └── UsuarioServiceTest.java
├── e2e/pageobjects/
│   ├── CadastroEventoE2ETest.java            # Testes E2E
│   ├── FluxoEventosE2ETest.java
│   └── FormularioEventoPage.java             # Page Object
└── cobertura/
    └── CoberturaTotalTest.java               # Validação de cobertura
```

---

## 🔄 Pipeline CI/CD

### Workflow Completo (GitHub Actions)

O projeto utiliza um pipeline CI/CD totalmente automatizado com 4 jobs principais:

```mermaid
graph LR
    A[Build & Tests] --> B[Deploy Homologação]
    B --> C[Testes E2E Pós-Deploy]
    C --> D[Deploy Produção]
    
    style A fill:#4CAF50
    style B fill:#2196F3
    style C fill:#FF9800
    style D fill:#F44336
```

### Job 1: 🏗️ Build, Testes e Segurança (SAST)

**Ações executadas:**
- ✅ Checkout do código
- ✅ Configuração do Java 17
- ✅ Build Maven (`mvn clean verify`)
- ✅ Execução de testes unitários e integração
- ✅ Análise de cobertura de código (JaCoCo)
- ✅ **Análise de segurança SAST com CodeQL**
- 📦 Upload de artefatos (JAR + relatório JaCoCo)

**Validações:**
- Cobertura mínima de 90% (configurada)
- Análise estática de vulnerabilidades

### Job 2: 🚀 Deploy Homologação

**Ações executadas:**
- 📥 Download do artefato JAR
- 🚀 Deploy simulado no ambiente de homologação
- 🧪 Smoke Tests (Health Check)

**Environment:** `homologacao`

### Job 3: 🌐 Testes E2E Pós-Deploy

**Ações executadas:**
- 🌐 Execução de testes Selenium em modo headless
- ✅ Validação de cenários críticos:
  - Criação de eventos
  - Validação de campos obrigatórios
  - Regras de negócio (nome duplicado)
  - Fluxo completo de CRUD

### Job 4: 🏭 Deploy Produção (Manual Approval)

**Ações executadas:**
- ⏸️ Aguarda aprovação manual
- 🚀 Deploy em produção
- 🔔 Notificação de deploy bem-sucedido

**Environment:** `producao`

### 📝 Ativar o Workflow

Para habilitar o pipeline no seu repositório:

1. Configure os **Environments** no GitHub:
   - Vá em: `Settings > Environments`
   - Crie: `homologacao` e `producao`
   - Em `producao`, ative **Required reviewers**

2. O workflow será executado automaticamente em:
   - Push para `main`
   - Pull Requests para `main`
   - Manualmente via `Actions > Run workflow`

---

## 🛡️ Segurança

### Análise Estática (SAST)

O projeto utiliza **GitHub CodeQL** para análise de segurança automatizada:

- 🔍 Detecção de vulnerabilidades em código Java
- 🚨 Alertas de segurança no GitHub Security
- ✅ Execução automática a cada push

### Proteção de Credenciais

- ✅ Variáveis sensíveis em `application.properties` **NÃO** devem ser commitadas
- ✅ Use **GitHub Secrets** para ambientes de produção
- ✅ Arquivo `.gitignore` configurado para excluir dados sensíveis

### Bean Validation

```java
@NotBlank(message = "O nome é obrigatório.")
@Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
private String nome;
```

---

## 📊 Monitoramento e Logs

### Logs do CI/CD

O pipeline gera logs detalhados com:

- 📝 **Step Summaries**: Tabelas de status no GitHub Actions
- 🎯 **Grouping**: Agrupamento visual de etapas (`::group::`)
- 🔔 **Notificações**: Alertas de deploy (`::notice`)

### Artefatos Gerados

- 📦 **JAR da aplicação** (retenção: 30 dias)
- 📊 **Relatório JaCoCo** (cobertura de código)
- 🧪 **Resultados de testes**

---

## ✨ Princípios de Clean Code Aplicados

### Separação de Leitura e Escrita (CQRS)

```java
@Transactional(readOnly = true)  // Operações de consulta
public List<Evento> listarTodos() {
    return eventoRepository.findAll();
}

@Transactional  // Operações de modificação
public Evento criarEvento(Evento evento, Long usuarioId) {
    // ...
}
```

### Cláusulas de Guarda (Fail-Fast)

```java
public Evento criarEvento(Evento evento, Long usuarioId) {
    // Validação no início (fail-fast)
    validarNomeDuplicado(evento.getNome(), null);
    
    // Lógica principal
    Usuario criador = usuarioService.buscarPorId(usuarioId);
    evento.setUsuario(criador);
    return eventoRepository.save(evento);
}
```

### Encapsulamento de Validações

```java
private void validarNomeDuplicado(String nome, Long idExcecao) {
    Optional<Evento> conflito = eventoRepository.findByNome(nome);
    
    if (conflito.isPresent() && 
        (idExcecao == null || !conflito.get().getId().equals(idExcecao))) {
        throw new ValidacaoException("O nome '" + nome + "' já está em uso.");
    }
}
```

### Injeção de Dependência por Construtor

```java
@Service
public class EventoService {
    private final EventoRepository eventoRepository;
    private final UsuarioService usuarioService;
    
    public EventoService(EventoRepository repo, UsuarioService userService) {
        this.eventoRepository = repo;
        this.usuarioService = userService;
    }
}
```

---

## 📈 Métricas de Qualidade

| Métrica | Meta | Status |
|---------|------|--------|
| Cobertura de Código | ≥ 90% | ✅ Validado |
| Análise SAST | 0 vulnerabilidades críticas | ✅ CodeQL |
| Testes E2E | 100% cenários cobertos | ✅ Selenium |
| Build Success Rate | 100% | ✅ CI/CD |

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

**Importante:** Todos os PRs devem:
- ✅ Passar no CI/CD
- ✅ Manter cobertura ≥ 90%
- ✅ Sem vulnerabilidades críticas

---

## 📄 Licença

Este projeto é parte de um trabalho acadêmico e está disponível para fins educacionais.

---

## 👨‍💻 Autor

**Seu Nome**  
📧 Email: seu.email@exemplo.com  
🎓 Instituição: [Nome da Universidade]

---

## 📚 Referências Técnicas

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Selenium WebDriver](https://www.selenium.dev/documentation/)
- [JaCoCo Code Coverage](https://www.jacoco.org/jacoco/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Clean Code - Robert C. Martin](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

---

## 🎉 Agradecimentos

- Professor(a) orientador(a)
- Colegas de turma
- Comunidade Spring Boot

---

**Desenvolvido com ❤️ usando Spring Boot, Thymeleaf e boas práticas de engenharia de software.**
