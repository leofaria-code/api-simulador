# 📚 Configuração do Swagger/OpenAPI - API Simulador

## 🎯 Resumo das Melhorias Implementadas

Este documento descreve as melhorias implementadas na documentação Swagger/OpenAPI da **API Simulador de Empréstimos**.

## 🔧 Alterações Realizadas

### 1. **SwaggerConfig.java** - Configuração Principal
✅ **Melhorias implementadas:**
- Título mais descritivo e profissional
- Descrição detalhada com markdown formatado
- Informações sobre funcionalidades da API
- Lista de tecnologias utilizadas
- Múltiplos servidores (desenvolvimento, homologação, produção)
- Links externos para documentação
- Configuração dinâmica da porta através de `@Value`

### 2. **application.yml** - Configurações SpringDoc
✅ **Configurações adicionadas:**
```yaml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    try-it-out-enabled: true
    operations-sorter: alpha
    tags-sorter: alpha
    display-request-duration: true
    show-extensions: true
    show-common-extensions: true
  show-actuator: true
  writer-with-default-pretty-printer: true
```

### 3. **DTOs Documentados** 
✅ **Schemas adicionados para:**

#### **SimulacaoRequestDTO**
- Descrição dos campos de entrada
- Exemplos de valores
- Validações mínimas documentadas

#### **SimulacaoResponseDTO**
- Documentação completa dos campos de resposta
- Exemplos práticos
- Descrição do modelo de dados

#### **ErrorResponseDTO**
- Padronização de respostas de erro
- Campos claramente documentados
- Timestamps e códigos de erro

#### **ParcelaDTO**
- Detalhamento de cada parcela
- Valores monetários formatados
- Numeração sequencial

#### **ResultadoSimulacaoDTO**
- Diferentes sistemas de amortização
- Lista de parcelas detalhada

#### **TelemetriaResponseDTO**
- Dados de monitoramento
- Métricas de performance

### 4. **Enums Documentados**
✅ **TipoSimulacaoEnum**
- Descrição dos sistemas SAC e PRICE
- Documentação das diferenças entre sistemas

## 🚀 URLs de Acesso

### 🏠 Ambiente Local
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

### ☁️ Ambientes Azure
- **Produção**: https://api-simulador.azurewebsites.net/swagger-ui.html
- **Homologação**: https://api-simulador-staging.azurewebsites.net/swagger-ui.html

## 📋 Funcionalidades Documentadas

### 🏦 **Simulações**
1. **POST /simulacoes** - Criar nova simulação
2. **GET /simulacoes** - Listar simulações paginadas
3. **GET /simulacoes/dia/{data}** - Volume por data

### 📊 **Monitoramento**
1. **GET /monitoramento/telemetria** - Métricas da aplicação

### 🏠 **Navegação**
1. **GET /** - Redirecionamento para Swagger
2. **GET /api** - Redirecionamento para Swagger

## 🎨 Recursos Visuais

### ✨ **Interface Melhorada**
- Emojis para melhor identificação
- Descrições estruturadas com markdown
- Exemplos práticos em todos os campos
- Organização por tags lógicas

### 📖 **Documentação Rica**
- Descrições detalhadas de cada endpoint
- Códigos de resposta HTTP documentados
- Exemplos de requisições e respostas
- Regras de negócio claramente definidas

## 🔍 Validações e Constraints

### 💰 **Valores Monetários**
- Valor mínimo: R$ 200,00
- Formatação com 2 casas decimais
- Validação automática

### 📅 **Prazos**
- Prazo mínimo: 1 mês
- Validação de campos obrigatórios

### 📄 **Paginação**
- Máximo 200 registros por página
- Ordenação padrão por ID descendente

## 🛠️ Tecnologias Utilizadas

- **SpringDoc OpenAPI 3**: v2.7.0
- **Spring Boot**: v3.5.5
- **Java**: 17+
- **Maven**: Gerenciamento de dependências

## 📝 Como Usar

1. **Inicie a aplicação**:
   ```bash
   mvn spring-boot:run
   ```

2. **Acesse o Swagger**:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Teste os endpoints** diretamente na interface

4. **Visualize os modelos** na seção "Schemas"

## 🔗 Links Úteis

- [Repositório GitHub](https://github.com/leofaria-code/api-simulador)
- [Documentação Spring Boot](https://spring.io/projects/spring-boot)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

## 📞 Contato

**Desenvolvedor**: Leonardo Faria  
**Email**: leofaria.email@gmail.com  
**GitHub**: [@leofaria-code](https://github.com/leofaria-code)
