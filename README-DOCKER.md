# 🐳 Docker Setup - API Simulador Hackathon 2025

## 🚀 Quick Start para o Hackathon

### Pré-requisitos
- Docker Desktop instalado
- PowerShell 5.1+ ou PowerShell Core
- Arquivo `.env` configurado (copie de `.env.example`)

### Execução Rápida

```powershell
# 1. Construir a aplicação
./docker-setup.ps1 build

# 2. Iniciar a aplicação
./docker-setup.ps1 start

# 3. Testar se está funcionando
./docker-setup.ps1 test
```

## 📋 Comandos Disponíveis

| Comando | Descrição |
|---------|-----------|
| `./docker-setup.ps1 build` | Constrói a imagem Docker |
| `./docker-setup.ps1 start` | Inicia a aplicação |
| `./docker-setup.ps1 stop` | Para a aplicação |
| `./docker-setup.ps1 restart` | Reinicia a aplicação |
| `./docker-setup.ps1 logs` | Exibe logs em tempo real |
| `./docker-setup.ps1 status` | Mostra status e saúde |
| `./docker-setup.ps1 test` | Testa endpoints principais |
| `./docker-setup.ps1 clean` | Limpa recursos Docker |

## 🌐 URLs da Aplicação

Após iniciar com sucesso:

- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/actuator/health
- **Postman Collection**: Importe `api-simulador-PostmanCollection.json`

## 📊 Endpoints Principais para Demonstração

### 1. Criar Simulação
```bash
curl -X POST "http://localhost:8080/simulacoes" \
  -H "Content-Type: application/json" \
  -d '{
    "valorDesejado": 50000,
    "prazo": 24
  }'
```

### 2. Listar Simulações
```bash
curl -X GET "http://localhost:8080/simulacoes?pagina=1&tamanho=10"
```

### 3. EventHub Status
```bash
curl -X GET "http://localhost:8080/monitoramento/eventhub-status"
```

### 4. Telemetria
```bash
curl -X GET "http://localhost:8080/monitoramento/telemetria"
```

## 🔧 Configuração de Ambiente

O arquivo `.env` deve conter:

```properties
# Azure SQL Server
DB_URL=your_azure_sql_server.database.windows.net
DB_USERNAME=your_username
DB_PASSWORD=your_password
DB_NAME=your_database

# Azure Event Hub
AZURE_EVENTHUB_CONNECTION_STRING=your_connection_string
AZURE_EVENTHUB_ENTITY_PATH=simulacoes
AZURE_EVENTHUB_NAME=simulacoes

# H2 (sempre funciona)
H2_URL=jdbc:h2:mem:simulacao
H2_USERNAME=sa
H2_PASSWORD=password
```

## 🏗️ Arquitetura Docker

### Multi-stage Build
- **Stage 1**: Maven build em Alpine Linux
- **Stage 2**: Runtime JRE otimizado
- **Segurança**: Usuario não-root
- **Performance**: Cache de dependências Maven

### Features de Produção
- ✅ Health checks automáticos
- ✅ Logs estruturados
- ✅ Graceful shutdown
- ✅ Resource limits
- ✅ Timezone configurado (São Paulo)
- ✅ JVM otimizada para containers

## 🎯 Para o Hackathon

### Cenário 1: Demonstração Local
```powershell
./docker-setup.ps1 build
./docker-setup.ps1 start
# Acessar Swagger UI para demonstrar
```

### Cenário 2: Teste Completo
```powershell
./docker-setup.ps1 test
# Executar Postman Collection
```

### Cenário 3: Monitoramento
```powershell
./docker-setup.ps1 status
./docker-setup.ps1 logs
```

## 🐛 Troubleshooting

### Problema: Build falha
```powershell
./docker-setup.ps1 clean
./docker-setup.ps1 build
```

### Problema: Aplicação não inicia
```powershell
./docker-setup.ps1 logs
# Verificar logs para diagnosticar
```

### Problema: Conexão Azure falha
- Verificar `.env` com credenciais corretas
- Confirmar conectividade de rede
- H2 sempre funciona como fallback

## 📦 Entrega para Hackathon

### Arquivos Necessários
- [x] `Dockerfile` - Optimizado e seguro
- [x] `docker-compose.yml` - Configuração completa
- [x] `.dockerignore` - Exclusões adequadas
- [x] `docker-setup.ps1` - Script de automação
- [x] `README-DOCKER.md` - Este arquivo
- [x] `.env.example` - Template de configuração

### Comandos de Demonstração
```powershell
# Setup inicial
./docker-setup.ps1 build

# Demonstração
./docker-setup.ps1 start
./docker-setup.ps1 test

# Navegue para http://localhost:8080/swagger-ui.html
```

## 🏆 Benefícios da Containerização

1. **Portabilidade**: Roda em qualquer ambiente Docker
2. **Consistência**: Mesmo comportamento em dev/prod
3. **Isolamento**: Não interfere com outras aplicações
4. **Escalabilidade**: Pronto para orquestração
5. **Deploy Rápido**: Uma linha de comando
6. **Rollback**: Versioning de imagens

---

**🎯 Ready for Hackathon! 🚀**

*Desenvolvido para Hackathon 2025 - API Simulador de Empréstimos*
