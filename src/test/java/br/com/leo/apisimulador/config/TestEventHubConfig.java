package br.com.leo.apisimulador.config;

import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestEventHubConfig {

    @Bean
    @Primary
    public EventHubProducerClient eventHubProducerClient() {
        return mock(EventHubProducerClient.class);
    }
}
