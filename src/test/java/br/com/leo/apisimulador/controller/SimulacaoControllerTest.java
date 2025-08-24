package br.com.leo.apisimulador.controller;

import br.com.leo.apisimulador.dto.*;
import br.com.leo.apisimulador.enums.TipoSimulacaoEnum;
import br.com.leo.apisimulador.model.h2.Simulacao;
import br.com.leo.apisimulador.repository.h2.SimulacaoRepository;
import br.com.leo.apisimulador.service.SimulacaoService;
import br.com.leo.apisimulador.service.TelemetriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SimulacaoController.class)
class SimulacaoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private SimulacaoService simulacaoService;

        @MockBean
        private SimulacaoRepository simulacaoRepository;

        @MockBean
        private TelemetriaService telemetriaService;

        private SimulacaoResponseDTO responseMock;
        private Simulacao simulacaoMock;

        @BeforeEach
        void setUp() {
                // Criar ParcelaDTO mock
                ParcelaDTO parcela = new ParcelaDTO(
                                1,
                                new BigDecimal("480"),
                                new BigDecimal("20"),
                                new BigDecimal("500"));

                // Criar ResultadoSimulacaoDTO mock
                ResultadoSimulacaoDTO resultado = new ResultadoSimulacaoDTO(
                                TipoSimulacaoEnum.SAC,
                                List.of(parcela));

                // Criar SimulacaoResponseDTO mock
                responseMock = new SimulacaoResponseDTO(
                                1L,
                                1,
                                "Crediário Pessoa Física",
                                new BigDecimal("0.025"),
                                List.of(resultado));

                // Criar Simulacao mock
                simulacaoMock = new Simulacao();
                simulacaoMock.setValorDesejado(new BigDecimal("10000"));
                simulacaoMock.setPrazo(24);
                simulacaoMock.setProdutoId(1);
                simulacaoMock.setDataReferencia(LocalDate.now());
                // Simular JSON válido para evitar erro no parser
                simulacaoMock.setResultadoJson(
                                "{\"idSimulacao\":1,\"codigoProduto\":1,\"descricaoProduto\":\"Teste\",\"taxaJuros\":0.025,\"resultadosSimulacao\":[{\"tipo\":\"SAC\",\"parcelas\":[{\"numero\":1,\"valorAmortizacao\":480,\"valorJuros\":20,\"valorPrestacao\":500}]}]}");
        }

        @Test
        void testSimularEmprestimo_DeveRetornarSimulacao() throws Exception {
                // Arrange
                when(telemetriaService.medirTempoExecucao(anyString(), any()))
                                .thenAnswer(invocation -> {
                                        java.util.function.Supplier<?> supplier = invocation.getArgument(1);
                                        return supplier.get();
                                });

                when(simulacaoService.simular(any(SimulacaoRequestDTO.class)))
                                .thenReturn(responseMock);

                SimulacaoRequestDTO request = new SimulacaoRequestDTO(
                                new BigDecimal("10000"),
                                24);

                // Act & Assert
                mockMvc.perform(post("/simulacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.idSimulacao").value(1))
                                .andExpect(jsonPath("$.codigoProduto").value(1))
                                .andExpect(jsonPath("$.descricaoProduto").value("Crediário Pessoa Física"));
        }

        @Test
        void testSimularEmprestimo_ComExcecao_DeveRetornar500() throws Exception {
                // Arrange
                when(telemetriaService.medirTempoExecucao(anyString(), any()))
                                .thenAnswer(invocation -> {
                                        java.util.function.Supplier<?> supplier = invocation.getArgument(1);
                                        return supplier.get();
                                });

                when(simulacaoService.simular(any(SimulacaoRequestDTO.class)))
                                .thenThrow(new RuntimeException("Erro no processamento"));

                SimulacaoRequestDTO request = new SimulacaoRequestDTO(
                                new BigDecimal("10000"),
                                24);

                // Act & Assert
                mockMvc.perform(post("/simulacoes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("Erro interno da aplicação"))
                                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                                .andExpect(jsonPath("$.details").isArray());
        }

        @Test
        void testListarSimulacoes_DeveRetornarPaginacao() throws Exception {
                // Arrange
                List<Simulacao> simulacoes = List.of(simulacaoMock);
                Page<Simulacao> page = new PageImpl<>(simulacoes, PageRequest.of(0, 200), 1);

                when(telemetriaService.medirTempoExecucao(anyString(), any()))
                                .thenAnswer(invocation -> {
                                        java.util.function.Supplier<?> supplier = invocation.getArgument(1);
                                        return supplier.get();
                                });

                when(simulacaoRepository.findAll(any(PageRequest.class)))
                                .thenReturn(page);

                // Act & Assert
                mockMvc.perform(get("/simulacoes")
                                .param("page", "0")
                                .param("size", "200"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.registros").isArray())
                                .andExpect(jsonPath("$.qtdRegistros").value(1))
                                .andExpect(jsonPath("$.pagina").value(1))
                                .andExpect(jsonPath("$.qtdRegistrosPagina").value(200));
        }

        @Test
        void testListarSimulacoes_SemParametros_DeveUsarPadrao() throws Exception {
                // Arrange
                List<Simulacao> simulacoes = List.of(simulacaoMock);
                Page<Simulacao> page = new PageImpl<>(simulacoes, PageRequest.of(0, 200), 1);

                when(telemetriaService.medirTempoExecucao(anyString(), any()))
                                .thenAnswer(invocation -> {
                                        java.util.function.Supplier<?> supplier = invocation.getArgument(1);
                                        return supplier.get();
                                });

                when(simulacaoRepository.findAll(any(PageRequest.class)))
                                .thenReturn(page);

                // Act & Assert
                mockMvc.perform(get("/simulacoes"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
}
