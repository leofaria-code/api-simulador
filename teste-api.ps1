#!/usr/bin/env pwsh

Write-Host "=== TESTE COMPLETO DA API SIMULADOR ===" -ForegroundColor Green

# Aguardar aplicação inicializar
Write-Host "⏳ Aguardando aplicação inicializar..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host "`n🔍 TESTE 1: Health Check" -ForegroundColor Cyan
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET
    Write-Host "✅ Health Check: $($health.status)" -ForegroundColor Green
}
catch {
    Write-Host "❌ Health Check falhou: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n💰 TESTE 2: Criando Simulação" -ForegroundColor Cyan
$headers = @{"Content-Type" = "application/json" }
$simulacao1 = @{
    valorDesejado = 50000.00
    prazo         = 60
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes" -Method POST -Body $simulacao1 -Headers $headers
    Write-Host "✅ Simulação criada com ID: $($response1.idSimulacao)" -ForegroundColor Green
    Write-Host "📊 Taxa de Juros: $($response1.taxaJuros)%" -ForegroundColor Gray
}
catch {
    Write-Host "❌ Criação de simulação falhou: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n💰 TESTE 3: Criando Segunda Simulação" -ForegroundColor Cyan
$simulacao2 = @{
    valorDesejado = 100000.00
    prazo         = 120
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes" -Method POST -Body $simulacao2 -Headers $headers
    Write-Host "✅ Segunda simulação criada com ID: $($response2.idSimulacao)" -ForegroundColor Green
    Write-Host "📊 Taxa de Juros: $($response2.taxaJuros)%" -ForegroundColor Gray
}
catch {
    Write-Host "❌ Segunda simulação falhou: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n📋 TESTE 4: Listando Todas as Simulações" -ForegroundColor Cyan
try {
    $simulacoes = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes" -Method GET
    Write-Host "✅ Total de simulações: $($simulacoes.qtdRegistros)" -ForegroundColor Green
    Write-Host "   📋 Página: $($simulacoes.pagina) | Registros na página: $($simulacoes.registros.Count)" -ForegroundColor Gray
    foreach ($sim in $simulacoes.registros) {
        Write-Host "   📝 ID: $($sim.idSimulacao) | Valor: R$ $($sim.valorDesejado) | Prazo: $($sim.prazo) meses" -ForegroundColor Gray
    }
}
catch {
    Write-Host "❌ Listagem falhou: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n❌ TESTE 5: Teste de Validação (dados inválidos)" -ForegroundColor Cyan
$simulacaoInvalida = @{
    valorDesejado = -1000
    prazo         = 0
} | ConvertTo-Json

try {
    $responseInvalida = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes" -Method POST -Body $simulacaoInvalida -Headers $headers
    Write-Host "❌ Validação não funcionou - deveria ter falhado!" -ForegroundColor Red
}
catch {
    Write-Host "✅ Validação funcionou corretamente: $($_.Exception.Message)" -ForegroundColor Green
}

Write-Host "`n📊 TESTE 6: Testando Endpoints do Actuator" -ForegroundColor Cyan
try {
    $info = Invoke-RestMethod -Uri "http://localhost:8080/actuator/info" -Method GET
    Write-Host "✅ Actuator Info disponível" -ForegroundColor Green
}
catch {
    Write-Host "❌ Actuator Info falhou: $($_.Exception.Message)" -ForegroundColor Red
}

try {
    $metrics = Invoke-RestMethod -Uri "http://localhost:8080/actuator/metrics" -Method GET
    Write-Host "✅ Actuator Metrics disponível ($($metrics.names.Count) métricas)" -ForegroundColor Green
}
catch {
    Write-Host "❌ Actuator Metrics falhou: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🎉 TESTE COMPLETO FINALIZADO!" -ForegroundColor Green
Write-Host "📝 Console H2 disponível em: http://localhost:8080/h2-console" -ForegroundColor Yellow
Write-Host "📊 Actuator disponível em: http://localhost:8080/actuator" -ForegroundColor Yellow
