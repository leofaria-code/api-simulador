#!/usr/bin/env pwsh
# Script para gerenciar a aplicação Docker - Hackathon 2025
# API Simulador de Empréstimos

param(
    [Parameter(Mandatory)]
    [ValidateSet("build", "start", "stop", "restart", "logs", "clean", "status", "test")]
    [string]$Action
)

$ErrorActionPreference = "Stop"
$ContainerName = "api-simulador-hackathon"
$ComposeFile = "docker-compose.yml"

function Show-Header {
    Write-Host "=============================================" -ForegroundColor Cyan
    Write-Host "🏦 API Simulador - Hackathon 2025" -ForegroundColor Green
    Write-Host "=============================================" -ForegroundColor Cyan
    Write-Host ""
}

function Test-Prerequisites {
    Write-Host "🔍 Verificando pré-requisitos..." -ForegroundColor Yellow
    
    if (!(Get-Command docker -ErrorAction SilentlyContinue)) {
        throw "❌ Docker não encontrado. Instale o Docker Desktop."
    }
    
    if (!(Get-Command docker-compose -ErrorAction SilentlyContinue)) {
        throw "❌ Docker Compose não encontrado."
    }
    
    if (!(Test-Path .env)) {
        throw "❌ Arquivo .env não encontrado. Copie de .env.example"
    }
    
    Write-Host "✅ Pré-requisitos OK" -ForegroundColor Green
}

function Build-Application {
    Write-Host "🔨 Construindo a aplicação..." -ForegroundColor Yellow
    docker-compose build --no-cache
    Write-Host "✅ Build concluído!" -ForegroundColor Green
}

function Start-Application {
    Write-Host "🚀 Iniciando a aplicação..." -ForegroundColor Yellow
    docker-compose up -d
    
    Write-Host "⏳ Aguardando aplicação ficar pronta..." -ForegroundColor Yellow
    $timeout = 120 # 2 minutos
    $elapsed = 0
    
    do {
        Start-Sleep -Seconds 5
        $elapsed += 5
        
        try {
            $response = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5
            if ($response.status -eq "UP") {
                Write-Host "✅ Aplicação iniciada com sucesso!" -ForegroundColor Green
                Write-Host "🌐 Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
                Write-Host "💾 H2 Console: http://localhost:8080/h2-console" -ForegroundColor Cyan
                Write-Host "❤️ Health Check: http://localhost:8080/actuator/health" -ForegroundColor Cyan
                return
            }
        }
        catch {
            Write-Host "." -NoNewline -ForegroundColor Yellow
        }
    } while ($elapsed -lt $timeout)
    
    Write-Host "⚠️ Timeout na inicialização. Verifique os logs." -ForegroundColor Red
}

function Stop-Application {
    Write-Host "🛑 Parando a aplicação..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "✅ Aplicação parada!" -ForegroundColor Green
}

function Restart-Application {
    Stop-Application
    Start-Application
}

function Show-Logs {
    Write-Host "📋 Mostrando logs da aplicação..." -ForegroundColor Yellow
    docker-compose logs -f --tail=100 api-simulador
}

function Clean-Resources {
    Write-Host "🧹 Limpando recursos Docker..." -ForegroundColor Yellow
    docker-compose down -v --remove-orphans
    docker system prune -f
    Write-Host "✅ Limpeza concluída!" -ForegroundColor Green
}

function Show-Status {
    Write-Host "📊 Status da aplicação:" -ForegroundColor Yellow
    docker-compose ps
    
    Write-Host "`n🔍 Verificando saúde..." -ForegroundColor Yellow
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5
        Write-Host "✅ Status: $($health.status)" -ForegroundColor Green
        
        if ($health.components) {
            Write-Host "📋 Componentes:" -ForegroundColor Cyan
            $health.components.PSObject.Properties | ForEach-Object {
                $status = $_.Value.status
                $color = if ($status -eq "UP") { "Green" } else { "Red" }
                Write-Host "  - $($_.Name): $status" -ForegroundColor $color
            }
        }
    }
    catch {
        Write-Host "❌ Aplicação não está respondendo" -ForegroundColor Red
    }
}

function Test-Application {
    Write-Host "🧪 Testando endpoints da aplicação..." -ForegroundColor Yellow
    
    $tests = @(
        @{
            Name = "Health Check"
            Url = "http://localhost:8080/actuator/health"
            ExpectedStatus = "UP"
        },
        @{
            Name = "Swagger UI"
            Url = "http://localhost:8080/swagger-ui.html"
            ExpectedStatus = "200"
        },
        @{
            Name = "EventHub Status"
            Url = "http://localhost:8080/monitoramento/eventhub-status"
            ExpectedStatus = "200"
        }
    )
    
    foreach ($test in $tests) {
        try {
            Write-Host "  Testing $($test.Name)..." -NoNewline
            $response = Invoke-WebRequest -Uri $test.Url -TimeoutSec 10
            if ($response.StatusCode -eq 200) {
                Write-Host " ✅" -ForegroundColor Green
            } else {
                Write-Host " ❌ ($($response.StatusCode))" -ForegroundColor Red
            }
        }
        catch {
            Write-Host " ❌ (Error: $($_.Exception.Message))" -ForegroundColor Red
        }
    }
}

# Main execution
Show-Header
Test-Prerequisites

switch ($Action) {
    "build" { Build-Application }
    "start" { Start-Application }
    "stop" { Stop-Application }
    "restart" { Restart-Application }
    "logs" { Show-Logs }
    "clean" { Clean-Resources }
    "status" { Show-Status }
    "test" { Test-Application }
}

Write-Host "`n🎯 Para o Hackathon, use:" -ForegroundColor Cyan
Write-Host "  ./docker-setup.ps1 build   # Construir a imagem" -ForegroundColor White
Write-Host "  ./docker-setup.ps1 start   # Iniciar aplicação" -ForegroundColor White
Write-Host "  ./docker-setup.ps1 test    # Testar endpoints" -ForegroundColor White
Write-Host "  ./docker-setup.ps1 status  # Ver status" -ForegroundColor White
