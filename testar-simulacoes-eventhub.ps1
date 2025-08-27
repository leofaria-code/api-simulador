# Script para testar simulações e EventHub
Write-Host "🚀 TESTANDO SIMULAÇÕES E EVENTHUB" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan

# Teste 1: Verificar status do EventHub
Write-Host "`n1️⃣ Verificando status do EventHub..." -ForegroundColor Yellow
try {
    $status = Invoke-RestMethod -Uri "http://localhost:8080/monitoramento/eventhub-status" -Method Get
    Write-Host "✅ EventHub Status: $($status.message)" -ForegroundColor Green
    Write-Host "   Habilitado: $($status.eventhub_enabled)" -ForegroundColor Gray
}
catch {
    Write-Host "❌ Erro ao verificar status: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 2: Enviar mensagem de teste para EventHub
Write-Host "`n2️⃣ Testando conexão direta com EventHub..." -ForegroundColor Yellow
try {
    $testResult = Invoke-RestMethod -Uri "http://localhost:8080/monitoramento/test-eventhub" -Method Post
    Write-Host "✅ Teste EventHub: $($testResult.status)" -ForegroundColor Green
    Write-Host "   $($testResult.message)" -ForegroundColor Gray
}
catch {
    Write-Host "❌ Erro no teste: $($_.Exception.Message)" -ForegroundColor Red
}

# Teste 3: Criar simulações que enviam para EventHub
Write-Host "`n3️⃣ Criando simulações (que enviam para EventHub)..." -ForegroundColor Yellow

$simulacoes = @(
    @{ valorDesejado = 5000.00; prazo = 12; desc = "Financiamento Básico" },
    @{ valorDesejado = 15000.00; prazo = 36; desc = "Financiamento Médio" },
    @{ valorDesejado = 25000.00; prazo = 48; desc = "Financiamento Grande" },
    @{ valorDesejado = 8000.00; prazo = 18; desc = "Financiamento Rápido" }
)

$sucessos = 0
$erros = 0

foreach ($sim in $simulacoes) {
    try {
        $body = $sim | ConvertTo-Json
        $response = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes" -Method Post -Body $body -ContentType "application/json"
        $sucessos++
        Write-Host "   ✅ $($sim.desc): R$ $($sim.valorDesejado) em $($sim.prazo)x - ID: $($response.id)" -ForegroundColor Green
        Write-Host "      Produto: $($response.codigoProduto) - $($response.descricaoProduto)" -ForegroundColor Gray
        Write-Host "      Taxa: $($response.taxaJuros)% | Sistemas: $($response.resultadosSimulacao.Count)" -ForegroundColor Gray
    }
    catch {
        $erros++
        Write-Host "   ❌ Falha na simulação $($sim.desc): $($_.Exception.Message)" -ForegroundColor Red
    }
    Start-Sleep -Milliseconds 500
}

# Teste 4: Verificar telemetria
Write-Host "`n4️⃣ Verificando telemetria da aplicação..." -ForegroundColor Yellow
try {
    $telemetria = Invoke-RestMethod -Uri "http://localhost:8080/monitoramento/telemetria" -Method Get
    Write-Host "✅ Telemetria obtida para: $($telemetria.dataReferencia)" -ForegroundColor Green
    
    if ($telemetria.listaEndpoints -and $telemetria.listaEndpoints.Count -gt 0) {
        foreach ($endpoint in $telemetria.listaEndpoints) {
            Write-Host "   📊 $($endpoint.nomeEndpoint):" -ForegroundColor Cyan
            Write-Host "      - Requisições: $($endpoint.qtdRequisicoes)" -ForegroundColor Gray
            Write-Host "      - Tempo médio: $($endpoint.tempoMedio)ms" -ForegroundColor Gray
            Write-Host "      - Taxa sucesso: $([math]::Round($endpoint.percentualSucesso * 100, 1))%" -ForegroundColor Gray
        }
    }
    else {
        Write-Host "   ℹ️ Nenhum endpoint com dados ainda" -ForegroundColor Yellow
    }
}
catch {
    Write-Host "❌ Erro ao obter telemetria: $($_.Exception.Message)" -ForegroundColor Red
}

# Resumo
Write-Host "`n📊 RESUMO DOS TESTES:" -ForegroundColor Cyan
Write-Host "   Simulações criadas: $sucessos" -ForegroundColor Green
Write-Host "   Simulações com erro: $erros" -ForegroundColor Red

if ($sucessos -gt 0) {
    Write-Host "`n🎯 RESULTADOS ESPERADOS:" -ForegroundColor Cyan
    Write-Host "   1. Cada simulação foi salva no banco H2" -ForegroundColor Yellow
    Write-Host "   2. Cada simulação foi enviada para Azure Event Hub" -ForegroundColor Yellow
    Write-Host "   3. Verifique os logs da aplicação para confirmação" -ForegroundColor Yellow
    
    Write-Host "`n🔍 COMO CONFIRMAR NO PORTAL AZURE:" -ForegroundColor Cyan
    Write-Host "   1. Acesse portal.azure.com" -ForegroundColor Gray
    Write-Host "   2. Vá para Event Hub 'eventhack'" -ForegroundColor Gray
    Write-Host "   3. Clique em 'Event Hubs' > 'simulacoes'" -ForegroundColor Gray
    Write-Host "   4. Veja 'Metrics' > 'Incoming Messages'" -ForegroundColor Gray
    Write-Host "   5. Devem aparecer $sucessos mensagens enviadas!" -ForegroundColor Green
}

Write-Host "`n✅ Testes concluídos!" -ForegroundColor Green
