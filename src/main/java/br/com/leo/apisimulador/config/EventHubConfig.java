package br.com.leo.apisimulador.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "azure.eventhub.enabled", havingValue = "true", matchIfMissing = false)
public class EventHubConfig {

    @Value("${azure.eventhub.connection-string}")
    private String connectionString;

    @Value("${azure.eventhub.entity-path}")
    private String entityPath;

    @Bean
    public EventHubProducerClient eventHubProducerClient() {
        return new EventHubClientBuilder()
                .connectionString(connectionString, entityPath)
                .buildProducerClient();
    }
}
