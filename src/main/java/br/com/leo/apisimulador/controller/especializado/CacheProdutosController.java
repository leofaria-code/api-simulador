package br.com.leo.apisimulador.controller.especializado;

import br.com.leo.apisimulador.service.ProdutoCacheService;
import br.com.leo.apisimulador.service.SimulacaoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para monitoramento e administração do cache de produtos
 */
@Hidden
@Tag(name = "Cache de Produtos", description = "🔧 Monitoramento avançado da aplicação e cache de produtos")
@RestController
@RequestMapping("/cache-produtos")
@Slf4j
@RequiredArgsConstructor
public class CacheProdutosController {

    private final ProdutoCacheService produtoCacheService;
    private final SimulacaoService simulacaoService;

    @Operation(summary = "📊 Status do Cache de Produtos", description = "Retorna informações detalhadas sobre o cache de produtos, incluindo status, última atualização, TTL e métricas de performance")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> obterStatusCache() {
        try {
            Map<String, Object> status = produtoCacheService.obterEstatisticas();

            log.info("📊 Status do cache consultado - Produtos: {}, Status: {}",
                    status.get("produtos_carregados"), status.get("status"));

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("❌ Erro ao obter status do cache: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Falha ao obter status do cache: " + e.getMessage()));
        }
    }

    @Operation(summary = "🔄 Forçar Atualização do Cache", description = "Força uma atualização imediata do cache de produtos a partir do SQL Server. Útil para sincronizar após mudanças nos produtos")
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> forcarAtualizacaoCache() {
        try {
            log.info("🔄 Solicitação de atualização forçada do cache de produtos");

            simulacaoService.atualizarCacheProdutos();

            // Aguardar um pouco para a atualização
            Thread.sleep(2000);

            Map<String, Object> resultado = produtoCacheService.forcarAtualizacao();

            log.info("✅ Cache atualizado com sucesso - Status: {}", resultado.get("status"));

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("❌ Erro ao forçar atualização do cache: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Falha ao atualizar cache: " + e.getMessage()));
        }
    }

    @Operation(summary = "📈 Métricas de Performance do Cache", description = "Retorna métricas detalhadas de performance do cache: hits, misses, tempo de busca e uso de fallbacks")
    @GetMapping("/metricas")
    public ResponseEntity<Map<String, Object>> obterMetricasCache() {
        try {
            // Exemplo: retorna as estatísticas básicas como métricas
            Map<String, Object> metricas = produtoCacheService.obterEstatisticas();

            Map<String, Object> resultado = Map.of(
                    "metricas_detalhadas", metricas,
                    "status_geral", metricas.get("status"),
                    "observacao", metricas.get("observacao")
            );

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("❌ Erro ao obter métricas do cache: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", "Falha ao obter métricas: " + e.getMessage()));
        }
    }

    @Operation(summary = "🔗 Forçar Reconexão ao SQL Server", description = "Tenta uma reconexão manual ao SQL Server para verificar se a conectividade foi restabelecida. Útil quando o SQL Server estava indisponível e queremos tentar reconectar")
    @PostMapping("/reconnect")
    public ResponseEntity<Map<String, Object>> forcarReconexaoSqlServer() {
        try {
            log.info("🔗 Solicitação de reconexão manual ao SQL Server");

            Map<String, Object> resultado = produtoCacheService.forcarReconexao();

            String status = (String) resultado.get("status");

            if ("RECONECTADO".equals(status)) {
                log.info("✅ Reconexão ao SQL Server bem-sucedida!");
                return ResponseEntity.ok(resultado);
            } else {
                log.warn("❌ Falha na reconexão ao SQL Server: {}", resultado.get("erro_mensagem"));
                return ResponseEntity.status(503).body(resultado); // Service Unavailable
            }

        } catch (Exception e) {
            log.error("❌ Erro durante tentativa de reconexão: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "ERRO_INTERNO",
                            "erro", "Falha durante reconexão: " + e.getMessage()));
        }
    }
}