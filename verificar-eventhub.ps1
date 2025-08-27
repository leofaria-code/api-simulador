# Script para Verificar Azure Event Hub
# Executa: .\verificar-eventhub.ps1

Write-Host "🔍 VERIFICAÇÃO DO AZURE EVENT HUB" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar se a aplicação está rodando
Write-Host "1️⃣ Verificando se a aplicação está ativa..." -ForegroundColor Yellow
try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 5
    if ($healthCheck.status -eq "UP") {
        Write-Host "✅ Aplicação está rodando" -ForegroundColor Green
    }
    else {
        Write-Host "❌ Aplicação não está saudável: $($healthCheck.status)" -ForegroundColor Red
        exit 1
    }
}
catch {
    Write-Host "❌ Aplicação não está respondendo na porta 8080" -ForegroundColor Red
    Write-Host "   Execute: mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# 2. Verificar configuração do EventHub
Write-Host "2️⃣ Verificando configuração do EventHub..." -ForegroundColor Yellow

# Ler variáveis do .env
if (Test-Path ".env") {
    Get-Content ".env" | ForEach-Object {
        if ($_ -match "AZURE_EVENTHUB_(.+)=(.+)") {
            $key = $matches[1]
            $value = $matches[2]
            Write-Host "   $key = $value" -ForegroundColor Gray
        }
    }
    Write-Host "✅ Arquivo .env encontrado" -ForegroundColor Green
}
else {
    Write-Host "❌ Arquivo .env não encontrado" -ForegroundColor Red
    exit 1
}

Write-Host ""

# 3. Fazer uma simulação para testar o envio
Write-Host "3️⃣ Testando envio para EventHub via simulação..." -ForegroundColor Yellow

$simulacaoBody = @{
    valorDesejado = 5000.00
    prazo         = 12
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes" -Method Post -Body $simulacaoBody -ContentType "application/json" -TimeoutSec 30
    Write-Host "✅ Simulação criada com sucesso!" -ForegroundColor Green
    Write-Host "   ID da Simulação: $($response.id)" -ForegroundColor Gray
    Write-Host "   Produto: $($response.codigoProduto) - $($response.descricaoProduto)" -ForegroundColor Gray
    
    # Aguardar um pouco para o EventHub processar
    Write-Host "⏳ Aguardando processamento..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    
}
catch {
    Write-Host "❌ Erro ao criar simulação: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Verifique os logs da aplicação" -ForegroundColor Yellow
}

Write-Host ""

# 4. Verificar logs da aplicação para EventHub
Write-Host "4️⃣ Verificando logs recentes..." -ForegroundColor Yellow
Write-Host "   (Procure por mensagens de EventHub nos logs da aplicação)" -ForegroundColor Gray
Write-Host "   Mensagens esperadas:" -ForegroundColor Gray
Write-Host "   - 'Simulação concluída com sucesso'" -ForegroundColor Gray
Write-Host "   - Ou 'Erro ao enviar mensagem para EventHub'" -ForegroundColor Gray

Write-Host ""

# 5. Verificar múltiplas simulações
Write-Host "5️⃣ Enviando múltiplas simulações para teste..." -ForegroundColor Yellow

$simulacoes = @(
    @{ valorDesejado = 10000; prazo = 24 },
    @{ valorDesejado = 15000; prazo = 36 },
    @{ valorDesejado = 8000; prazo = 18 }
)

$sucessos = 0
$erros = 0

foreach ($sim in $simulacoes) {
    try {
        $body = $sim | ConvertTo-Json
        $response = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes" -Method Post -Body $body -ContentType "application/json" -TimeoutSec 15
        $sucessos++
        Write-Host "   ✅ Simulação R$ $($sim.valorDesejado) em $($sim.prazo)x - ID: $($response.id)" -ForegroundColor Green
    }
    catch {
        $erros++
        Write-Host "   ❌ Falha na simulação R$ $($sim.valorDesejado) em $($sim.prazo)x" -ForegroundColor Red
    }
    Start-Sleep -Seconds 1
}

Write-Host ""
Write-Host "📊 RESULTADO DOS TESTES:" -ForegroundColor Cyan
Write-Host "   Simulações enviadas com sucesso: $sucessos" -ForegroundColor Green
Write-Host "   Simulações com erro: $erros" -ForegroundColor Red

if ($sucessos -gt 0) {
    Write-Host ""
    Write-Host "🎉 PRÓXIMOS PASSOS PARA VERIFICAR EVENTHUB:" -ForegroundColor Cyan
    Write-Host "1. Acesse o Portal Azure (portal.azure.com)" -ForegroundColor Yellow
    Write-Host "2. Vá para o Event Hub 'eventhack'" -ForegroundColor Yellow
    Write-Host "3. Clique em 'Event Hubs' > 'simulacoes'" -ForegroundColor Yellow
    Write-Host "4. Verifique as métricas:" -ForegroundColor Yellow
    Write-Host "   - Incoming Messages" -ForegroundColor Gray
    Write-Host "   - Outgoing Messages" -ForegroundColor Gray
    Write-Host "   - Incoming Requests" -ForegroundColor Gray
    Write-Host ""
    Write-Host "📈 Ou use a telemetria da aplicação:" -ForegroundColor Cyan
    Write-Host "   GET http://localhost:8080/monitoramento/telemetria" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🔧 TROUBLESHOOTING:" -ForegroundColor Cyan
Write-Host "- Se não apareceram mensagens no EventHub:" -ForegroundColor Yellow
Write-Host "  1. Verifique as credenciais no .env" -ForegroundColor Gray
Write-Host "  2. Confirme se o EventHub 'simulacoes' existe" -ForegroundColor Gray
Write-Host "  3. Verifique as permissões da chave de acesso" -ForegroundColor Gray
Write-Host "  4. Analise os logs da aplicação Spring Boot" -ForegroundColor Gray

Write-Host ""
Write-Host "✅ Verificação concluída!" -ForegroundColor Green
