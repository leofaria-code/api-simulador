package br.com.leo.apisimulador.controller.especializado;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;

import br.com.leo.apisimulador.config.geral.TimeZoneConfig;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Hidden
@Tag(name = "Diagnóstico", description = "🔌 Testes de serviços externos, rede e integrações")
@RestController
@RequestMapping("/diagnostico")
public class DiagnosticoController {

    @Value("${spring.datasource.principal-sqlserver.jdbc-url}")
    private String jdbcUrl;

    @Value("${spring.datasource.principal-sqlserver.username}")
    private String username;

    @Value("${spring.datasource.principal-sqlserver.password}")
    private String password;

    @Autowired(required = false)
    private EventHubProducerClient eventHubProducerClient;

    @GetMapping("/sql-server")
    public ResponseEntity<Map<String, Object>> diagnosticarSqlServer() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            resultado.put("jdbc_url", jdbcUrl.replaceAll("password=[^;]*", "password=***"));
            resultado.put("username", username);
            resultado.put("timestamp", System.currentTimeMillis());
            long inicioTeste = System.currentTimeMillis();
            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                long tempoConexao = System.currentTimeMillis() - inicioTeste;
                resultado.put("status", "CONECTADO");
                resultado.put("tempo_conexao_ms", tempoConexao);
                resultado.put("database_product_name", connection.getMetaData().getDatabaseProductName());
                resultado.put("database_product_version", connection.getMetaData().getDatabaseProductVersion());
                resultado.put("driver_name", connection.getMetaData().getDriverName());
                resultado.put("driver_version", connection.getMetaData().getDriverVersion());
                long inicioQuery = System.currentTimeMillis();
                try (Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT @@VERSION as versao, GETDATE() as data_atual")) {
                    if (rs.next()) {
                        long tempoQuery = System.currentTimeMillis() - inicioQuery;
                        resultado.put("tempo_query_ms", tempoQuery);
                        resultado.put("versao_servidor", rs.getString("versao"));
                        resultado.put("data_servidor", rs.getTimestamp("data_atual"));
                        resultado.put("query_executada", "SUCCESS");
                    }
                }
                try (Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM INFORMATION_SCHEMA.TABLES")) {
                    if (rs.next()) {
                        resultado.put("total_tabelas", rs.getInt("total"));
                    }
                }
            } catch (SQLException e) {
                resultado.put("status", "ERRO_CONEXAO");
                resultado.put("erro_tipo", e.getClass().getSimpleName());
                resultado.put("erro_mensagem", e.getMessage());
                resultado.put("erro_sql_state", e.getSQLState());
                resultado.put("erro_codigo", e.getErrorCode());
                resultado.put("tempo_tentativa_ms", System.currentTimeMillis() - inicioTeste);
            }
        } catch (Exception e) {
            resultado.put("status", "ERRO_CONFIGURACAO");
            resultado.put("erro_tipo", e.getClass().getSimpleName());
            resultado.put("erro_mensagem", e.getMessage());
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/network")
    public ResponseEntity<Map<String, Object>> diagnosticarRede() {
        Map<String, Object> resultado = new HashMap<>();

        try {
            String host = "dbhackathon.database.windows.net";
            int port = 1433;
            int timeout = 5000;
            long inicio = System.currentTimeMillis();
            try (Socket socket = new Socket()) {
                socket.connect(new java.net.InetSocketAddress(host, port), timeout);
                long tempoConexao = System.currentTimeMillis() - inicio;
                resultado.put("status", "REDE_OK");
                resultado.put("host", host);
                resultado.put("porta", port);
                resultado.put("tempo_conexao_ms", tempoConexao);
                resultado.put("conectado", socket.isConnected());
            } catch (Exception e) {
                resultado.put("status", "ERRO_REDE");
                resultado.put("host", host);
                resultado.put("porta", port);
                resultado.put("erro_tipo", e.getClass().getSimpleName());
                resultado.put("erro_mensagem", e.getMessage());
                resultado.put("tempo_tentativa_ms", System.currentTimeMillis() - inicio);
            }
        } catch (Exception e) {
            resultado.put("status", "ERRO_GERAL");
            resultado.put("erro_tipo", e.getClass().getSimpleName());
            resultado.put("erro_mensagem", e.getMessage());
        }

        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Diagnóstico do Azure Event Hub", description = "Verifica se o Azure Event Hub está configurado e tenta enviar uma mensagem de teste para validar a integração")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Diagnóstico realizado"),
            @ApiResponse(responseCode = "500", description = "Erro na integração com Event Hub")
    })
    @GetMapping("/eventhub")
    public ResponseEntity<Map<String, Object>> diagnosticoEventHub() {
        boolean eventhubConfigurado = eventHubProducerClient != null;
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("eventhub_enabled", eventhubConfigurado);
        resultado.put("timestamp", TimeZoneConfig.now());

        if (!eventhubConfigurado) {
            resultado.put("status", "DISABLED");
            resultado.put("message", "Azure Event Hub está desabilitado ou não configurado");
            resultado.put("configuration_check", Map.of(
                    "bean_configured", false,
                    "profile_active", "Event Hub desabilitado"));
            return ResponseEntity.ok(resultado);
        }

        try {
            String mensagemTeste = String.format("""
                    {
                        "tipo": "TESTE_CONEXAO",
                        "timestamp": "%s",
                        "origem": "api-simulador",
                        "dados": {
                            "endpoint": "GET /diagnostico/eventhub",
                            "descricao": "Diagnóstico de conectividade com Azure Event Hub"
                        }
                    }
                    """, TimeZoneConfig.now());
            log.info("🧪 Enviando mensagem de teste para Azure Event Hub...");
            eventHubProducerClient.send(List.of(new EventData(mensagemTeste)));
            log.info("✅ Mensagem de teste enviada com sucesso!");

            resultado.put("status", "SUCCESS");
            resultado.put("message", "Mensagem de teste enviada com sucesso para Azure Event Hub");
            resultado.put("eventhub_configured", true);
            resultado.put("configuration_check", Map.of(
                    "bean_configured", true,
                    "profile_active", "Event Hub habilitado"));
            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("❌ Erro ao testar Event Hub: {}", e.getMessage(), e);
            resultado.put("status", "ERROR");
            resultado.put("message", "Erro ao conectar com Azure Event Hub: " + e.getMessage());
            resultado.put("eventhub_configured", true);
            resultado.put("error_details", e.getClass().getSimpleName());
            resultado.put("configuration_check", Map.of(
                    "bean_configured", true,
                    "profile_active", "Event Hub habilitado"));
            return ResponseEntity.ok(resultado);
        }
    }
}
