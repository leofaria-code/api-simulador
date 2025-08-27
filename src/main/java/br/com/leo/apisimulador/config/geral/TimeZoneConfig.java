package br.com.leo.apisimulador.config.geral;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import jakarta.annotation.PostConstruct;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Configuração centralizada de fuso horário para toda a aplicação.
 * Define o fuso horário padrão como horário de São Paulo (America/Sao_Paulo).
 */
@Configuration
@Order(1) // Executar esta configuração primeiro
public class TimeZoneConfig {
    
    /**
     * Fuso horário padrão da aplicação configurável via application.yml
     */
    @Value("${app.timezone:America/Sao_Paulo}")
    private String defaultTimeZone;
    
    /**
     * Define o fuso horário padrão da JVM para toda a aplicação.
     * Isso afeta todos os LocalDateTime.now(), Date, Calendar, etc.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(defaultTimeZone));
        System.setProperty("user.timezone", defaultTimeZone);
    }
    
    /**
     * Bean para injeção de dependência do ZoneId padrão
     */
    @Bean("defaultZoneId")
    public ZoneId defaultZoneId() {
        return ZoneId.of(defaultTimeZone);
    }
    
    /**
     * Bean para injeção de dependência do TimeZone padrão
     */
    @Bean("defaultTimeZone")
    public TimeZone defaultTimeZone() {
        return TimeZone.getTimeZone(defaultTimeZone);
    }
    
    /**
     * Utilitário para obter o ZoneId padrão estaticamente
     */
    public static ZoneId getDefaultZoneId() {
        return ZoneId.systemDefault();
    }
    
    /**
     * Utilitário para obter timestamp atual no fuso horário padrão
     */
    public static java.time.LocalDateTime now() {
        return java.time.LocalDateTime.now(getDefaultZoneId());
    }
    
    /**
     * Utilitário para obter data atual no fuso horário padrão
     */
    public static java.time.LocalDate today() {
        return java.time.LocalDate.now(getDefaultZoneId());
    }
}
