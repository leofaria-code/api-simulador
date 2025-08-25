# Script para testar API sem interferir na aplicação
Write-Host "🔄 Testando API..." -ForegroundColor Yellow
Write-Host "📍 Endpoint: GET /simulacoes" -ForegroundColor Cyan

# Teste 1: Verificar se API está rodando
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/simulacoes?pagina=0`&tamanho=200" -Method GET -Headers @{'accept' = '*/*' }
    Write-Host "✅ API RESPONDE!" -ForegroundColor Green
    Write-Host "📋 Formato da resposta:" -ForegroundColor White
    $response | ConvertTo-Json -Depth 5
}
catch {
    Write-Host "❌ API NÃO RESPONDE: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🧪 Teste concluído!" -ForegroundColor Yellow
