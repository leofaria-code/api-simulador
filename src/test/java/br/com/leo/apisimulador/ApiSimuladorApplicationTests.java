package br.com.leo.apisimulador;

import br.com.leo.apisimulador.config.TestEventHubConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestEventHubConfig.class)
class ApiSimuladorApplicationTests {

    @Test
    void contextLoads() {
    }

}
