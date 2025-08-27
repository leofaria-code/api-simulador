# 🐳 Docker Setup - API Simulador Hackathon 2025

## 🚀 Início rápido para o Hackathon

### Pré-requisitos
- Docker Desktop instalado
- PowerShell 5.1+ ou PowerShell Core
- Arquivo `.env` configurado (copie de `.env.example`)

### Execução rápida

```powershell
# 1. Construir a aplicação
./docker-setup.ps1 build

# 2. Iniciar a aplicação
./docker-setup.ps1 start

# 3. Testar se está funcionando
./docker-setup.ps1 test
```

## 📋 Comandos disponíveis

| Comando                   | Descrição                       |
|---------------------------|---------------------------------|
| `./docker-setup.ps1 build`   | Constrói a imagem Docker         |
| `./docker-setup.ps1 start`   | Inicia a aplicação               |
| `./docker-setup.ps1 stop`    | Para a aplicação                 |
| `./docker-setup.ps1 restart` | Reinicia a aplicação             |
| `./docker-setup.ps1 logs`    | Exibe logs em tempo real         |
| `./docker-setup.ps1 status`  | Mostra status e saúde            |
| `./docker-setup.ps1 test`    | Testa endpoints principais       |
| `./docker-setup.ps1 clean`   | Limpa recursos Docker            |

## 🌐 URLs da aplicação

Após iniciar com sucesso:

- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **Coleção Postman**: Importe `api-simulador-PostmanCollection.json`

## 📊 Endpoints principais para demonstração

### 1. Criar simulação
```bash
curl -X POST "http://localhost:8080/simulacoes" \
  -H "Content-Type: application/json" \
  -d '{
    "valorDesejado": 50000,
    "prazo": 24
  }'
```

### 2. Listar simulações
```bash
curl -X GET "http://localhost:8080/simulacoes?pagina=1&tamanho=10"
```

### 3. Volume simulado por dia
```bash
curl -X GET "http://localhost:8080/simulacoes/dia?data=2025-08-25"
```

### 4. Telemetria
```bash
curl -X GET "http://localhost:8080/monitoramento/telemetria?dataReferencia=2025-08-25"
```

## 🔧 Configuração de ambiente

O arquivo `.env` deve conter:

```properties
# Azure SQL Server
DB_URL=seu_sql_server.database.windows.net
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
DB_NAME=seu_banco

# H2 (sempre funciona)
H2_URL=jdbc:h2:mem:simulacao
H2_USERNAME=sa
H2_PASSWORD=password
```

## 🏗️ Arquitetura Docker

### Multi-stage build
- **Stage 1**: Build Maven em Alpine Linux
- **Stage 2**: Runtime JRE otimizado
- **Segurança**: Usuário não-root
- **Performance**: Cache de dependências Maven

### Recursos de produção
- Health check automático
- Logs estruturados
- Graceful shutdown
- Limite de recursos
- Timezone configurado (São Paulo)
- JVM otimizada para containers

## 🎯 Para o Hackathon

### Cenário 1: Demonstração local
```powershell
./docker-setup.ps1 build
./docker-setup.ps1 start
# Acesse o Swagger UI para demonstrar
```

### Cenário 2: Teste completo
```powershell
./docker-setup.ps1 test
# Execute a coleção Postman
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
# Verifique os logs para diagnosticar
```

### Problema: Conexão Azure falha
- Verifique o `.env` com credenciais corretas
- Confirme conectividade de rede
- O H2 sempre funciona como fallback

## 📦 Entrega para o Hackathon

### Arquivos necessários
- [x] `Dockerfile` - Otimizado e seguro
- [x] `docker-compose.yml` - Configuração completa
- [x] `.dockerignore` - Exclusões adequadas
- [x] `docker-setup.ps1` - Script de automação
- [x] `README-DOCKER.md` - Este arquivo
- [x] `.env.example` - Template de configuração

### Comandos de demonstração
```powershell
# Setup inicial
./docker-setup.ps1 build

# Demonstração
./docker-setup.ps1 start
./docker-setup.ps1 test

# Navegue para http://localhost:8080/swagger-ui.html
```

## 🏆 Benefícios da containerização

1. Portabilidade: roda em qualquer ambiente Docker
2. Consistência: mesmo comportamento em dev e produção
3. Isolamento: não interfere com outras aplicações
4. Escalabilidade: pronto para orquestração
5. Deploy rápido: um comando
6. Rollback: versionamento de imagens

---

**Pronto para o Hackathon! 🚀**

*Desenvolvido para Hackathon 2025 - API Simulador de Empréstimos*
