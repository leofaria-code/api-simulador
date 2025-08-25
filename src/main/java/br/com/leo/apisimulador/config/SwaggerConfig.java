package br.com.leo.apisimulador.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

        @Value("${server.port:8080}")
        private String serverPort;

        @Bean
        public OpenAPI apiDoc() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API Simulador de Empréstimos - Hackathon")
                                                .description("""
                                                                ## 📊 API para Simulação de Empréstimos

                                                                Esta API oferece um sistema completo para simulação de empréstimos com múltiplos sistemas de amortização e telemetria avançada.

                                                                ### ✨ Principais Funcionalidades:

                                                                #### 🏦 Simulações de Empréstimo
                                                                * **Cálculo SAC** (Sistema de Amortização Constante)
                                                                * **Cálculo PRICE** (Sistema Francês de Amortização)
                                                                * Validação automática de parâmetros de entrada
                                                                * Persistência de simulações para auditoria

                                                                #### 📈 Relatórios e Consultas
                                                                * Listagem paginada de todas as simulações realizadas
                                                                * Consulta de volume simulado por data específica
                                                                * Agregação de dados por produto e período
                                                                * Histórico completo de operações

                                                                #### 🔍 Monitoramento e Telemetria
                                                                * Métricas de performance de endpoints
                                                                * Telemetria de tempo de resposta
                                                                * Dados de volume de requisições
                                                                * Taxa de sucesso por operação
                                                                * Integração com Azure Event Hub

                                                                ### 🛠️ Arquitetura e Tecnologias:

                                                                * **Backend**: Java 17 + Spring Boot 3.5.x
                                                                * **Persistência**:
                                                                  - SQL Server (Azure) - dados principais
                                                                  - H2 Database (em memória) - simulações temporárias
                                                                * **Mensageria**: Azure Event Hub para telemetria
                                                                * **Documentação**: OpenAPI 3.0 + SpringDoc
                                                                * **Monitoramento**: Actuator + Métricas
                                                                * **Validação**: Bean Validation (JSR-303)

                                                                ### 📋 Regras de Negócio:

                                                                * **Valor mínimo**: R$ 200,00
                                                                * **Prazo mínimo**: 1 mês
                                                                * **Sistemas suportados**: SAC e PRICE
                                                                * **Paginação**: Máximo 200 registros por página
                                                                """)
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Leonardo Faria")
                                                                .email("leofaria.email@gmail.com")
                                                                .url("https://github.com/leofaria-code"))
                                                .license(new License()
                                                                .name("Apache License 2.0")
                                                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:" + serverPort)
                                                                .description("🏠 Ambiente de Desenvolvimento Local"),
                                                new Server()
                                                                .url("https://api-simulador.azurewebsites.net")
                                                                .description("☁️ Ambiente de Produção Azure"),
                                                new Server()
                                                                .url("https://api-simulador-staging.azurewebsites.net")
                                                                .description("🧪 Ambiente de Homologação Azure")))
                                .externalDocs(new ExternalDocumentation()
                                                .description("📚 Repositório do Projeto no GitHub")
                                                .url("https://github.com/leofaria-code/api-simulador"));
        }
}
