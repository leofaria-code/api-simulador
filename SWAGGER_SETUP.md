# 📚 Configuração do Swagger/OpenAPI - API Simulador

Este documento explica como a documentação Swagger/OpenAPI foi configurada para a **API Simulador de Empréstimos** do Hackathon 2025.

---

## 🎯 Endpoints Documentados

A documentação Swagger exibe apenas os endpoints de **Simulação** e **Telemetria**:

### 🏦 Simulações

- **POST `/simulacoes`**  
  Cria uma nova simulação de empréstimo.  
  **Body Exemplo:**  
  ```json
  {
    "valorDesejado": 10000.00,
    "prazo": 24
  }
  ```

- **GET `/simulacoes`**  
  Lista todas as simulações, com suporte à paginação.  
  **Exemplo:**  
  ```
  /simulacoes?pagina=0&tamanho=50
  ```

- **GET `/simulacoes/dia`**  
  Consulta o volume de simulações por data.  
  **Exemplo:**  
  ```
  /simulacoes/dia?data=2025-08-25
  ```

### 📊 Telemetria

- **GET `/monitoramento/telemetria`**  
  Retorna métricas e estatísticas dos endpoints da API.  
  **Exemplo:**  
  ```
  /monitoramento/telemetria?dataReferencia=2025-08-25
  ```

---

## 📝 Como acessar o Swagger

- **Ambiente local:**  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## ⚙️ Configuração utilizada

A documentação foi configurada via arquivo `SwaggerConfig.java`:

- Título, descrição e contato personalizados
- Licença Apache 2.0
- Servidor local configurado dinamicamente
- Links externos para o repositório
- Apenas endpoints relevantes expostos

Exemplo de configuração:

```java
@Bean
public OpenAPI apiDoc() {
    return new OpenAPI()
        .info(new Info()
            .title("Hackathon 2025 - API Simulador")
            .description("Simulação de empréstimos e telemetria da API.")
            .version("1.0.0")
            .contact(new Contact()
                .name("Leonardo Oliveira Faria")
                .email("leofaria.email@gmail.com")
                .url("https://github.com/leofaria-code"))
            .license(new License()
                .name("Apache License 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .servers(List.of(
            new Server()
                .url("http://localhost:" + serverPort)
                .description("Ambiente de Desenvolvimento Local")
        ))
        .externalDocs(new ExternalDocumentation()
            .description("Repositório do Projeto no GitHub")
            .url("https://github.com/leofaria-code/api-simulador"));
}
```

---

## 📖 Recursos da documentação

- Descrições detalhadas dos endpoints
- Exemplos de requisições e respostas
- Validações e regras de negócio visíveis
- Modelos (schemas) exibidos apenas para simulação e telemetria

---

## 🔗 Links úteis

- [Repositório GitHub](https://github.com/leofaria-code/api-simulador)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

## 📞 Contato

**Desenvolvedor:** Leonardo Oliveira Faria  
**Email:** leofaria.email@gmail.com  
**GitHub:** [@leofaria-code](https://github.com/leofaria-code)
