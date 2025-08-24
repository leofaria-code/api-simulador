package br.com.leo.apisimulador.service;

import br.com.leo.apisimulador.config.TestEventHubConfig;
import br.com.leo.apisimulador.dto.SimulacaoRequestDTO;
import br.com.leo.apisimulador.dto.SimulacaoResponseDTO;
import br.com.leo.apisimulador.model.sqlserver.Produto;
import br.com.leo.apisimulador.repository.sqlserver.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestEventHubConfig.class)
class SimulacaoServiceTest {

    @Autowired
    private SimulacaoService simulacaoService;

    @MockBean
    private ProdutoRepository produtoRepository;

    private Produto produtoTeste;

    @BeforeEach
    void setUp() {
        produtoTeste = Produto.builder()
                .codigoProduto(1)
                .descricaoProduto("Crediário Pessoa Física")
                .taxaJuros(new BigDecimal("0.025"))
                .valorMinimo(new BigDecimal("1000"))
                .valorMaximo(new BigDecimal("50000"))
                .minimoMeses((short) 12)
                .maximoMeses((short) 60)
                .build();
    }

    @Test
    void testSimular_ComDadosValidos_DeveRetornarSimulacao() {
        // Arrange
        when(produtoRepository.findAll())
                .thenReturn(List.of(produtoTeste));

        SimulacaoRequestDTO request = new SimulacaoRequestDTO(
                new BigDecimal("10000"),
                24
        );

        // Act
        SimulacaoResponseDTO response = simulacaoService.simular(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.resultadosSimulacao());
        assertFalse(response.resultadosSimulacao().isEmpty());
        assertEquals(produtoTeste.getCodigoProduto(), response.codigoProduto());
        assertEquals(produtoTeste.getDescricaoProduto(), response.descricaoProduto());
        assertEquals(produtoTeste.getTaxaJuros(), response.taxaJuros());
    }

    @Test
    void testSimular_ComProdutoInexistente_DeveLancarExcecao() {
        // Arrange
        when(produtoRepository.findAll())
                .thenReturn(List.of());

        SimulacaoRequestDTO request = new SimulacaoRequestDTO(
                new BigDecimal("10000"),
                24
        );

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            simulacaoService.simular(request);
        });
        
        assertTrue(exception.getMessage().contains("Nenhum produto encontrado"));
    }

    @Test
    void testSimular_ComValorAbaixoMinimo_DeveLancarExcecao() {
        // Arrange - produto com valor mínimo alto
        Produto produtoComMinimoAlto = Produto.builder()
                .codigoProduto(1)
                .descricaoProduto("Produto Premium")
                .taxaJuros(new BigDecimal("0.025"))
                .valorMinimo(new BigDecimal("50000"))
                .valorMaximo(new BigDecimal("100000"))
                .minimoMeses((short) 12)
                .maximoMeses((short) 60)
                .build();

        when(produtoRepository.findAll())
                .thenReturn(List.of(produtoComMinimoAlto));

        SimulacaoRequestDTO request = new SimulacaoRequestDTO(
                new BigDecimal("500"), // Muito abaixo do mínimo
                24
        );

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            simulacaoService.simular(request);
        });
        
        assertTrue(exception.getMessage().contains("Nenhum produto encontrado"));
    }

    @Test
    void testSimular_ComPrazoForaLimite_DeveLancarExcecao() {
        // Arrange - produto com prazo limitado
        Produto produtoComPrazoLimitado = Produto.builder()
                .codigoProduto(1)
                .descricaoProduto("Produto Limitado")
                .taxaJuros(new BigDecimal("0.025"))
                .valorMinimo(new BigDecimal("1000"))
                .valorMaximo(new BigDecimal("50000"))
                .minimoMeses((short) 12)
                .maximoMeses((short) 24)
                .build();

        when(produtoRepository.findAll())
                .thenReturn(List.of(produtoComPrazoLimitado));

        SimulacaoRequestDTO request = new SimulacaoRequestDTO(
                new BigDecimal("10000"),
                48 // Acima do máximo de 24
        );

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            simulacaoService.simular(request);
        });
        
        assertTrue(exception.getMessage().contains("Nenhum produto encontrado"));
    }
}
