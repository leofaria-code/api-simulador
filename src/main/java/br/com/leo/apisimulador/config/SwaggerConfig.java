package br.com.leo.apisimulador.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiDoc() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Simulador de Empréstimos")
                        .description("""
                            API para simulação de empréstimos com as seguintes funcionalidades:
                            
                            * Simulação de empréstimos com cálculo SAC e PRICE
                            * Consulta de simulações realizadas
                            * Agregação de dados por dia e produto
                            * Monitoramento e telemetria
                            
                            ### Tecnologias utilizadas:
                            * Java 17
                            * Spring Boot 3.x
                            * SQL Server (Azure)
                            * H2 Database
                            * Azure Event Hub
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("leofaria-code")
                                .email("leofaria.email@gmail.com")
                                .url("https://github.com/leofaria-code/api-simulador"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Ambiente Local"),
                        //TODO: Atualizar URL do servidor de produção
                        new Server().url("https://sua-api.azurewebsites.net").description("Ambiente Azure")
                ));
    }
}
