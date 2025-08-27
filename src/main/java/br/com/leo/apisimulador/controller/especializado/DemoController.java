package br.com.leo.apisimulador.controller.especializado;

import br.com.leo.apisimulador.config.geral.CharsetConfig;
import br.com.leo.apisimulador.config.geral.TimeZoneConfig;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@Hidden
@Tag(name = "Demonstração", description = "🧪 Exemplos e demonstrações de configurações")
public class DemoController {

    @GetMapping("/api/demo/charset")
    @Operation(summary = "Demonstração do CharsetConfig", description = "Mostra exemplos de codificação e sanitização de caracteres brasileiros")
    public ResponseEntity<Map<String, Object>> demonstrarCharset() {
        Map<String, Object> demo = new LinkedHashMap<>();

        String textoCompleto = "Simulação financeira com acentuação: João, José, coração, pão, ação, informações";
        String textoComSimbolos = "Preço: R$ 1.250,00 • Taxa: 2,5% • Prazo: 36 meses";
        String nomeCompleto = "José da Silva Araújo Neto";

        demo.put("charset_configurado", CharsetConfig.getDefaultCharset().name());
        demo.put("charset_display_name", CharsetConfig.getDefaultCharset().displayName());
        demo.put("texto_original", textoCompleto);
        demo.put("texto_com_simbolos", textoComSimbolos);
        demo.put("nome_completo", nomeCompleto);

        byte[] textoCodificado = CharsetConfig.encode(textoCompleto);
        demo.put("texto_bytes_length", textoCodificado.length);
        demo.put("texto_decodificado", CharsetConfig.decode(textoCodificado));
        demo.put("texto_garantido_utf8", CharsetConfig.ensureUtf8(textoCompleto));
        demo.put("nome_garantido_utf8", CharsetConfig.ensureUtf8(nomeCompleto));
        demo.put("texto_sanitizado", CharsetConfig.sanitize(textoCompleto));
        demo.put("nome_sanitizado", CharsetConfig.sanitize(nomeCompleto));
        demo.put("timezone_configurado", TimeZoneConfig.getDefaultZoneId().getId());
        demo.put("timestamp_atual", TimeZoneConfig.now());
        demo.put("configuracao_ativa", "CharsetConfig e TimeZoneConfig centralizados");
        demo.put("suporte_caracteres_br", "Ativo - suporte completo a ç, ã, õ, ê, etc.");

        return ResponseEntity.ok(demo);
    }

    @GetMapping("/api/demo/timezone")
    @Operation(summary = "Demonstração do TimeZoneConfig", description = "Mostra exemplos de manipulação de datas e horários usando a configuração centralizada de timezone")
    public ResponseEntity<Map<String, Object>> demonstrarTimeZone() {
        Map<String, Object> info = new LinkedHashMap<>();

        info.put("zona_configurada", TimeZoneConfig.getDefaultZoneId().getId());
        info.put("timestamp_atual", TimeZoneConfig.now());
        info.put("data_atual", TimeZoneConfig.today());
        info.put("data_formatada", TimeZoneConfig.today().toString());
        info.put("descricao", "Exemplo de uso do TimeZoneConfig para garantir datas e horários corretos no Brasil");

        return ResponseEntity.ok(info);
    }

    @GetMapping("/api/demo/config")
    @Operation(summary = "Status das Configurações Centralizadas", description = "Mostra o status de todas as configurações centralizadas da aplicação")
    public ResponseEntity<Map<String, Object>> statusConfiguracao() {
        Map<String, Object> status = new LinkedHashMap<>();

        status.put("timezone", Map.of(
                "zona_configurada", TimeZoneConfig.getDefaultZoneId().getId(),
                "timestamp_atual", TimeZoneConfig.now(),
                "data_atual", TimeZoneConfig.today(),
                "status", "Ativo - Centralizado"));

        status.put("charset", Map.of(
                "charset_padrao", CharsetConfig.getDefaultCharset().name(),
                "charset_display", CharsetConfig.getDefaultCharset().displayName(),
                "suporte_br", "Ativo - Caracteres especiais brasileiros",
                "status", "Ativo - Centralizado"));

        status.put("resumo", Map.of(
                "configuracoes_centralizadas", 2,
                "timezone_sao_paulo", "✓ Ativo",
                "charset_utf8_br", "✓ Ativo",
                "pronto_para_hackathon", "✓ Sim"));

        return ResponseEntity.ok(status);
    }

}