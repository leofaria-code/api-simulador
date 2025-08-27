package br.com.leo.apisimulador.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition(
    info = @io.swagger.v3.oas.annotations.info.Info(
        title = "Hackathon 2025 - API Simulador",
        version = "1.0.0",
        description = "API para simulação de empréstimos, monitoramento e integração com Azure Event Hub.",
        contact = @io.swagger.v3.oas.annotations.info.Contact(
            name = "Leonardo Oliveira Faria",
            email = "leofaria.email@gmail.com",
            url = "https://github.com/leofaria"
        )
    )
)
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI apiDoc() {
        return new OpenAPI()
            .info(new Info()
                .title("Hackathon 2025 - API Simulador")
                .description("""
                    💸 Simulação de empréstimos (SAC/PRICE)  
                    📊 Monitoramento avançado  
                    ☁️ Integração com Azure Event Hub  
                    ---
                    • Valor mínimo: R$ 200,00  
                    • Prazo mínimo: 1 mês  
                    • Paginação: até 200 registros/página
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Leonardo Oliveira Faria")
                    .email("leofaria.email@gmail.com")
                    .url("https://github.com/leofaria-code"))
                .license(new License()
                    .name("Apache License 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            // .tags(List.of(
            //     new Tag().name("💸 Simulações").description("Endpoints para criação e consulta de simulações"),
            //     new Tag().name("📊 Monitoramento").description("Telemetria e métricas da aplicação")
            // ))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Ambiente de Desenvolvimento Local")
            ))
            .externalDocs(new ExternalDocumentation()
                .description("🔗 Repositório do Projeto no GitHub")
                .url("https://github.com/leofaria-code/api-simulador"));
    }
}
