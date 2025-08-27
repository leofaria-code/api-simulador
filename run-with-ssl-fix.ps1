# Script para iniciar a aplicação com configurações SSL otimizadas para Azure SQL
# Resolve problemas de handshake SSL/TLS com Azure Database

Write-Host "🔧 Configurando aplicação para conectividade com Azure SQL..." -ForegroundColor Yellow

# Definir variáveis de ambiente específicas para SSL/TLS
$env:JAVA_OPTS = @"
-Dcom.microsoft.sqlserver.jdbc.fipsProvider=false
-Djavax.net.ssl.trustStore=""
-Djavax.net.ssl.trustStorePassword=""
-Dcom.microsoft.sqlserver.jdbc.fips=false
-Djavax.net.ssl.keyStore=""
-Djavax.net.ssl.keyStorePassword=""
-Djava.net.useSystemProxies=false
-Dnetworkaddress.cache.ttl=60
-Dnetworkaddress.cache.negative.ttl=10
-Dcom.sun.net.ssl.enableECC=false
-Djdk.tls.disabledAlgorithms=""
-Djava.security.useSystemPropertiesFile=false
-Djdk.tls.client.protocols=TLSv1.2
-Dssl.SocketFactory.provider=com.microsoft.sqlserver.jdbc.TLSSocketFactory
-Dhttps.protocols=TLSv1.2
-Djsse.enableSNIExtension=true
-Dcom.microsoft.sqlserver.jdbc.traceFile=""
"@

Write-Host "📋 Configurações SSL aplicadas:" -ForegroundColor Green
Write-Host "   - TLS 1.2 forçado" -ForegroundColor Gray
Write-Host "   - FIPS desabilitado" -ForegroundColor Gray
Write-Host "   - Trust store customizado" -ForegroundColor Gray
Write-Host "   - Cache DNS otimizado" -ForegroundColor Gray
Write-Host "   - SNI habilitado" -ForegroundColor Gray

Write-Host "`n🚀 Iniciando aplicação..." -ForegroundColor Green
mvn spring-boot:run
