package br.com.leo.apisimulador.config.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import jakarta.annotation.PostConstruct;
import java.security.Security;

/**
 * Configuração SSL específica para resolver conectividade com Azure SQL Server
 */
@Configuration
@Order(1) // Executar antes de outras configurações
@Slf4j
public class AzureSqlSslConfig {

    @PostConstruct
    public void configureAzureSqlSSL() {
        log.info("🔧 Configurando SSL para Azure SQL Server...");
        
        try {
            // Configurações para melhorar conectividade SSL/TLS com Azure
            System.setProperty("com.microsoft.sqlserver.jdbc.fipsProvider", "false");
            System.setProperty("com.microsoft.sqlserver.jdbc.fips", "false");
            System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "true");
            
            // Configurações de cache DNS (importante para Azure)
            System.setProperty("networkaddress.cache.ttl", "60");
            System.setProperty("networkaddress.cache.negative.ttl", "10");
            
            // Configurações de proxy (desabilitar se houver interferência)
            System.setProperty("java.net.useSystemProxies", "false");
            
            // Configurações específicas para melhorar handshake SSL
            System.setProperty("com.sun.net.ssl.enableECC", "false");
            System.setProperty("java.security.useSystemPropertiesFile", "false");
            
            // Limpar algoritmos TLS desabilitados que podem interferir
            String disabledAlgorithms = Security.getProperty("jdk.tls.disabledAlgorithms");
            if (disabledAlgorithms != null && !disabledAlgorithms.isEmpty()) {
                log.info("📋 Algoritmos TLS desabilitados: {}", disabledAlgorithms);
                // Remover restrições que podem bloquear Azure SQL
                String newDisabledAlgorithms = disabledAlgorithms
                    .replaceAll("TLSv1\\.1,?\\s*", "")
                    .replaceAll("TLSv1,?\\s*", "")
                    .replaceAll("SSLv3,?\\s*", "")
                    .replaceAll("SSLv2Hello,?\\s*", "");
                Security.setProperty("jdk.tls.disabledAlgorithms", newDisabledAlgorithms);
                log.info("🔄 Algoritmos TLS atualizados: {}", newDisabledAlgorithms);
            }
            
            // Configurações específicas do driver SQL Server
            System.setProperty("ssl.SocketFactory.provider", "com.microsoft.sqlserver.jdbc.TLSSocketFactory");
            
            log.info("✅ Configurações SSL para Azure SQL aplicadas com sucesso!");
            log.info("📋 Configurações ativas:");
            log.info("   - TLS 1.2: {}", System.getProperty("jdk.tls.client.protocols"));
            log.info("   - FIPS: {}", System.getProperty("com.microsoft.sqlserver.jdbc.fips"));
            log.info("   - SNI: {}", System.getProperty("jsse.enableSNIExtension"));
            log.info("   - Cache DNS TTL: {}s", System.getProperty("networkaddress.cache.ttl"));
            
        } catch (Exception e) {
            log.error("❌ Erro ao configurar SSL para Azure SQL: {}", e.getMessage(), e);
        }
    }
}
