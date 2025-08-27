package br.com.leo.apisimulador.config.geral;

import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.annotation.PostConstruct;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Configuração centralizada de charset/encoding para toda a aplicação.
 * Define UTF-8 como padrão para suportar caracteres especiais brasileiros (ç, ã, õ, etc.).
 */
@Configuration
@Order(2) // Executar após TimeZoneConfig
public class CharsetConfig implements WebMvcConfigurer {
    
    /**
     * Charset padrão da aplicação configurável via application.yml
     */
    @Value("${app.charset:UTF-8}")
    private String defaultCharset;
    
    /**
     * Define o charset padrão da JVM para toda a aplicação.
     * Isso afeta file.encoding, console output, etc.
     */
    @PostConstruct
    public void init() {
        System.setProperty("file.encoding", defaultCharset);
        System.setProperty("sun.jnu.encoding", defaultCharset);
        
        // Log da configuração aplicada
        System.out.println("🔤 Charset configurado: " + defaultCharset);
        System.out.println("🇧🇷 Suporte a caracteres especiais: ç, ã, õ, ü, etc.");
    }
    
    /**
     * Bean para injeção de dependência do Charset padrão
     */
    @Bean("defaultCharset")
    public Charset defaultCharset() {
        return Charset.forName(defaultCharset);
    }
    
    /**
     * Configuração do Spring MVC para usar UTF-8 em todas as respostas HTTP
     */
    @Override
    public void configureMessageConverters(@NonNull List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setDefaultCharset(getDefaultCharset());
        converters.add(0, stringConverter);
    }
    
    /**
     * Utilitário para obter o Charset padrão estaticamente
     */
    public static Charset getDefaultCharset() {
        return StandardCharsets.UTF_8;
    }
    
    /**
     * Utilitário para codificar string com charset padrão
     */
    public static byte[] encode(String text) {
        return text != null ? text.getBytes(getDefaultCharset()) : new byte[0];
    }
    
    /**
     * Utilitário para decodificar bytes com charset padrão
     */
    public static String decode(byte[] bytes) {
        return bytes != null ? new String(bytes, getDefaultCharset()) : "";
    }
    
    /**
     * Utilitário para garantir que uma string está em UTF-8
     */
    public static String ensureUtf8(String text) {
        if (text == null) return null;
        
        // Se já está em UTF-8, retorna como está
        if (getDefaultCharset().newEncoder().canEncode(text)) {
            return text;
        }
        
        // Tenta converter de ISO-8859-1 para UTF-8 (caso comum)
        try {
            byte[] bytes = text.getBytes(StandardCharsets.ISO_8859_1);
            return new String(bytes, getDefaultCharset());
        } catch (Exception e) {
            return text; // Retorna original se não conseguir converter
        }
    }
    
    /**
     * Utilitário para limpar caracteres especiais se necessário
     */
    public static String sanitize(String text) {
        if (text == null) return null;
        
        // Normaliza caracteres especiais para equivalentes ASCII se necessário
        return text
            .replace("ç", "c")
            .replace("Ç", "C")
            .replace("ã", "a")
            .replace("Ã", "A")
            .replace("õ", "o")
            .replace("Õ", "O")
            .replace("ü", "u")
            .replace("Ü", "U");
    }
}
