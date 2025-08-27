# 🏦 API Simulador de Empréstimos

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue?style=for-the-badge&logo=apache-maven)
![Azure](https://img.shields.io/badge/Azure-SQL%20Server-0078d4?style=for-the-badge&logo=microsoft-azure)
![Docker](https://img.shields.io/badge/Docker-Supported-2496ed?style=for-the-badge&logo=docker)

**API REST completa para simulação de empréstimos com sistemas SAC e PRICE**

[🚀 Demo](#-demonstração) • [📖 Documentação](#-documentação-da-api) • [🛠️ Instalação](#%EF%B8%8F-instalação) • [🐳 Docker](#-docker) • [📊 Monitoramento](#-monitoramento)

</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Instalação](#%EF%B8%8F-instalação)
- [Configuração](#%EF%B8%8F-configuração)
- [Docker](#-docker)
- [Documentação da API](#-documentação-da-api)
- [Endpoints](#-endpoints)
- [Monitoramento](#-monitoramento)
- [Testes](#-testes)
- [Deploy](#-deploy)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

---

## 🎯 Sobre o Projeto

A **API Simulador de Empréstimos** é uma solução robusta desenvolvida para o **Hackathon**, oferecendo simulações financeiras precisas usando os sistemas de amortização **SAC** (Sistema de Amortização Constante) e **PRICE** (Sistema Francês de Amortização).

### 🏆 Características Principais

- ✅ **Cálculos Financeiros Precisos**: Implementação matemática rigorosa dos sistemas SAC e PRICE
- ✅ **Arquitetura Escalável**: Microserviços com múltiplas fontes de dados
- ✅ **Observabilidade Completa**: Telemetria, métricas e monitoramento integrado
- ✅ **Documentação Interativa**: Swagger/OpenAPI 3.0 completo

---

## ⚡ Funcionalidades

### 🧮 **Simulações Financeiras**
- **Sistema SAC**: Parcelas decrescentes com amortização constante
- **Sistema PRICE**: Parcelas fixas com amortização crescente
- **Validações Automáticas**: Valores mínimos e prazos válidos
- **Persistência Inteligente**: Armazenamento em SQL Server e H2

### 📊 **Relatórios e Consultas**
- **Listagem Paginada**: Histórico completo de simulações
- **Consultas por Data**: Volume agregado por período
- **Análise por Produto**: Métricas segmentadas
- **Exportação de Dados**: Formatos JSON estruturados

### 🔍 **Monitoramento e Telemetria**
- **Métricas de Performance**: Tempo de resposta por endpoint
- **Volume de Operações**: Contadores de requisições
- **Taxa de Sucesso**: Monitoramento de falhas

### 🛡️ **Segurança e Qualidade**
- **Validação Robusta**: Bean Validation (JSR-303)
- **Tratamento de Erros**: Respostas padronizadas
- **Logs Estruturados**: Rastreabilidade completa
- **Health Checks**: Monitoramento de saúde da aplicação

---

## 🛠️ Tecnologias

### **Backend**
- **Java 17** - LTS com performance otimizada
- **Spring Boot 3.5.5** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Validation** - Validação de entrada

### **Bancos de Dados**
- **SQL Server (Azure)** - Dados principais de produção
- **H2 Database** - Simulações em memória

### **Documentação**
- **SpringDoc OpenAPI 3** - Documentação interativa
- **Swagger UI** - Interface de testes
- **Postman Collection** - Coleção completa com todos os endpoints e testes automáticos

### **Observabilidade**
- **Spring Actuator** - Health checks e métricas
- **Azure Event Hub** - Telemetria em tempo real

### **Deploy e DevOps**
- **Docker** - Containerização
- **Docker Compose** - Orquestração local
- **Maven** - Gerenciamento de dependências

---

## 🏗️ Arquitetura

```mermaid
graph TB
    Client[👤 Cliente] --> Swagger[📚 Swagger UI]
    Client --> API[🚀 API REST]
    
    API --> Controller[🎮 Controllers]
    Controller --> Service[⚙️ Services]
    Service --> Repository[📦 Repositories]
    
    Repository --> H2[(💾 H2 Database)]
    Repository --> SQL[(🗄️ SQL Server)]
    
    Service --> EventHub[☁️ Azure Event Hub]
    Service --> Telemetria[📊 Telemetria Service]
    
    API --> Actuator[🔍 Spring Actuator]
    
    subgraph "🔧 Configurações"
        Config[⚙️ Configs]
        Exception[❌ Exception Handler]
        Validation[✅ Validations]
    end
```

### **Camadas da Aplicação**

1. **Presentation Layer** - Controllers REST com documentação Swagger
2. **Business Layer** - Services com lógica de negócio e cálculos financeiros
3. **Data Layer** - Repositories para múltiplas fontes de dados
4. **Configuration Layer** - Configurações de infraestrutura e segurança

---

## ⚙️ Instalação

### **Pré-requisitos**

- ☕ **Java 17+** - [Download OpenJDK](https://adoptopenjdk.net/)
- 📦 **Maven 3.9+** - [Download Maven](https://maven.apache.org/download.cgi)
- 🐳 **Docker** (opcional) - [Download Docker](https://www.docker.com/get-started)
- ☁️ **Conta Azure** (para produção) - [Azure Portal](https://portal.azure.com/)

### **1. Clonar o Repositório**

```bash
git clone https://github.com/leofaria-code/api-simulador.git
cd api-simulador
```

### **2. Configurar Variáveis de Ambiente**

```bash
# Copiar o arquivo de exemplo
cp .env.example .env

# Editar as configurações
nano .env
```

### **3. Instalar Dependências**

```bash
mvn clean install
```

### **4. Executar a Aplicação**

```bash
mvn spring-boot:run
```

### **5. Verificar a Instalação**

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Swagger UI
open http://localhost:8080/swagger-ui.html
```

### **🚀 Quick Start - Primeiros Passos**

Após a instalação, você pode testar a API de 3 formas:

#### **1. 📖 Swagger UI (Recomendado para desenvolvedores)**
- Acesse: http://localhost:8080/swagger-ui.html
- Interface completa com documentação interativa
- Teste todos os endpoints diretamente no navegador

#### **2. 📁 Postman Collection (Recomendado para testes)**
- Importe o arquivo: `api-simulador-PostmanCollection.json`
- Coleção completa com **os endpoints**

#### **3. 🔧 cURL (Para automação)**
```bash
# Exemplo: Criar uma simulação
curl -X POST "http://localhost:8080/simulacoes" \
  -H "Content-Type: application/json" \
  -d '{"valorDesejado": 10000, "prazo": 24}'
```

💡 **Dica**: Use a **Postman Collection** para uma experiência completa com testes automáticos!

---

## 🔧 Configuração

### **Arquivo `.env`**

```bash
# 🗄️ SQL Server (Produção)
DB_URL=seu-servidor-azure.database.windows.net
DB_PORT=1433
DB_NAME=hackathon_db
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha_super_secreta

# 💾 H2 Database (Desenvolvimento)
H2_URL=jdbc:h2:mem:simulacao
H2_USERNAME=sa
H2_PASSWORD=password

# ☁️ Azure Event Hub
AZURE_EVENTHUB_CONNECTION_STRING=Endpoint=sb://...
AZURE_EVENTHUB_ENTITY_PATH=simulacoes
AZURE_EVENTHUB_NAME=simulacoes-hub

# ⚙️ Configurações Gerais
SERVER_PORT=8080
SHOW_SQL=false
```

### **Perfis de Ambiente**

```bash
# Desenvolvimento
mvn spring-boot:run

# Produção
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Testes
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

---

## 🐳 Docker

### **Build Local**

```bash
# Construir a imagem
docker build -t api-simulador:latest .

# Executar container
docker run -p 8080:8080 --env-file .env api-simulador:latest
```

### **Docker Compose**

```bash
# Subir todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f api-simulador

# Parar serviços
docker-compose down
```

### **Arquivos Docker**

- `Dockerfile` - Imagem da aplicação
- `docker-compose.yml` - Orquestração completa
- `.dockerignore` - Exclusões do build

---

## 📖 Documentação da API

### **Swagger UI**
🌐 **Acesso**: http://localhost:8080/swagger-ui.html

### **OpenAPI Spec**
📄 **JSON**: http://localhost:8080/api-docs  
📄 **YAML**: http://localhost:8080/api-docs.yaml

### **Coleção Postman Completa**
📁 **Arquivo**: `api-simulador-PostmanCollection.json`

🚀 **Coleção atualizada com todos os endpoints:**
- ✅ **Simulações completas** - Criar, listar e analisar simulações
- ✅ **Monitoramento avançado** - Telemetria e métricas de performance
- ✅ **Documentação completa** - Descrições detalhadas de cada endpoint

📥 **Para importar no Postman:**
1. Abra o Postman
2. Clique em "Import"
3. Selecione o arquivo `api-simulador-PostmanCollection.json`
4. Configure a variável `base_url` se necessário

---

## 🔗 Endpoints

### 🏦 **Simulações**

| Método | Endpoint | Descrição | Parâmetros | Auth |
|--------|----------|-----------|------------|------|
| `POST` | `/simulacoes` | Criar nova simulação | `valorDesejado`, `prazo` | - |
| `GET` | `/simulacoes` | Listar simulações (paginado) | `pagina`, `tamanho` | - |
| `GET` | `/simulacoes/dia` | Volume por data específica | `data` (query) | - |

### 📊 **Monitoramento e Telemetria**

| Método | Endpoint | Descrição | Parâmetros | Auth |
|--------|----------|-----------|------------|------|
| `GET` | `/monitoramento/telemetria` | Métricas da aplicação | `dataReferencia` | - |

---

## **Exemplos de Uso**

#### **📝 Criar Simulação**

```bash
curl -X POST "http://localhost:8080/simulacoes" \
  -H "Content-Type: application/json" \
  -d '{
    "valorDesejado": 10000.00,
    "prazo": 24
  }'
```

#### **📋 Listar Simulações**

```bash
curl "http://localhost:8080/simulacoes?pagina=0&tamanho=50"
```

#### **📅 Volume por Data**

```bash
curl "http://localhost:8080/simulacoes/dia?data=2025-08-25"
```

#### **📊 Telemetria**

```bash
curl "http://localhost:8080/monitoramento/telemetria?dataReferencia=2025-08-25"
```

---

## 📊 Monitoramento

### **Health Checks**

```bash
# Status geral
curl http://localhost:8080/actuator/health

# Informações da aplicação
curl http://localhost:8080/actuator/info
```

### **Telemetria Customizada**

```bash
# Telemetria da aplicação
curl "http://localhost:8080/monitoramento/telemetria?dataReferencia=2025-08-25"
```

## 🧪 Testes

### **Testes Automatizados**

- ✅ **Testes Unitários** - Services e utilitários
- ✅ **Testes de Integração** - Controllers e repositories
- ✅ **Testes de Contrato** - APIs e schemas

### **Scripts de Teste**

```bash
# PowerShell - Teste da API
./teste-api.ps1

# PowerShell - Teste de formato
./teste-formato.ps1

# PowerShell - Testar EventHub
./testar-simulacoes-eventhub.ps1
```

### **🔧 Testes com Postman**

A coleção Postman inclui **testes automáticos** para todos os endpoints:

📁 **Arquivo**: `api-simulador-PostmanCollection.json`

#### **Testes incluídos:**
- ✅ Validação de status code (200/201)
- ✅ Verificação de Content-Type JSON
- ✅ Validação de tempo de resposta (<5s)
- ✅ Estrutura de dados das respostas

#### **Como executar:**
1. **Importe a coleção** no Postman
2. **Configure a variável** `base_url = http://localhost:8080`
3. **Execute Collection Runner** para testes automatizados
4. **Visualize os resultados** de todos os endpoints

#### **Exemplo de uso avançado:**
```javascript
// Testes personalizados incluídos na coleção
pm.test('Simulação retorna SAC e PRICE', function () {
    const response = pm.response.json();
    pm.expect(response).to.have.property('simulacaoSAC');
    pm.expect(response).to.have.property('simulacaoPRICE');
});
```

💡 **Dica**: Use o **Collection Runner** do Postman para executar todos os testes de uma vez e gerar relatórios automatizados!

---

## 📝 Licença

Este projeto está licenciado sob a **Apache License 2.0** - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👨‍💻 Autor

<div align="center">

**Leonardo Faria**

[![GitHub](https://img.shields.io/badge/GitHub-leofaria--code-black?style=for-the-badge&logo=github)](https://github.com/leofaria-code)
[![Email](https://img.shields.io/badge/Email-leofaria.email@gmail.com-red?style=for-the-badge&logo=gmail)](mailto:leofaria.email@gmail.com)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Profile-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/leofaria-code)

</div>