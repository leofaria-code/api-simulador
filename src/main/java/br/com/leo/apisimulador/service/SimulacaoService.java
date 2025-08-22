package br.com.leo.apisimulador.service;

import br.com.leo.apisimulador.dto.Parcela;
import br.com.leo.apisimulador.dto.ResultadoSimulacao;
import br.com.leo.apisimulador.dto.SimulacaoRequest;
import br.com.leo.apisimulador.dto.SimulacaoResponse;
import br.com.leo.apisimulador.dto.TipoSimulacao;
import br.com.leo.apisimulador.model.h2.Simulacao;
import br.com.leo.apisimulador.model.sqlserver.Produto;
import br.com.leo.apisimulador.repository.sqlserver.ProdutoRepository;
import br.com.leo.apisimulador.repository.h2.SimulacaoRepository;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulacaoService {
    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final CalculoAmortizacaoService calculoService;
    private final EventHubProducerClient eventHubProducerClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public SimulacaoResponse simular(SimulacaoRequest request) {
        try {
            // 1. Buscar produto válido
            Produto produto = produtoRepository.findAll().stream()
                .filter(p ->
                    request.getValorDesejado().doubleValue() >= p.getValorMinimo().doubleValue() &&
                        request.getValorDesejado().doubleValue() <= p.getValorMaximo().doubleValue() &&
                        request.getPrazo() >= p.getMinimoMeses() &&
                        request.getPrazo() <= p.getMaximoMeses())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum produto encontrado para os parâmetros informados."));
            
            // 2. Calcular simulações
            List<Parcela> parcelasSAC = calculoService.calcularSAC(request.getValorDesejado(), produto.getTaxaJuros(), request.getPrazo());
            List<Parcela> parcelasPRICE = calculoService.calcularPRICE(request.getValorDesejado(), produto.getTaxaJuros(), request.getPrazo());
            
            // 3. Montar a resposta no novo formato JSON
            var response = getSimulacaoResponse(parcelasSAC, parcelasPRICE, produto);
            
            // 4. Persistir no banco local
            Simulacao sim = new Simulacao();
            sim.setProduto(produto); // Agora usa a entidade Produto completa
            sim.setValorDesejado(request.getValorDesejado());
            sim.setPrazo(request.getPrazo());
            sim.setResultadoJson(objectMapper.writeValueAsString(response));
            simulacaoRepository.save(sim);
            
            // 5. Enviar para EventHub
            enviarParaEventHub(sim.getResultadoJson());
            
            return response;
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao serializar resposta: " + e.getMessage(), e);
        } catch (Exception e) {
            // Log do erro e tratamento apropriado
            throw new RuntimeException("Erro ao processar simulação: " + e.getMessage(), e);
        }
    }
    
    @NotNull
    private static SimulacaoResponse getSimulacaoResponse(List<Parcela> parcelasSAC, List<Parcela> parcelasPRICE, Produto produto) {
        ResultadoSimulacao resultadoSac = new ResultadoSimulacao(TipoSimulacao.SAC, parcelasSAC);
        ResultadoSimulacao resultadoPrice = new ResultadoSimulacao(TipoSimulacao.PRICE, parcelasPRICE);
        List<ResultadoSimulacao> resultados = List.of(resultadoSac, resultadoPrice);
        
        return new SimulacaoResponse(
            
            produto.getCodigoProduto(),
            produto.getDescricaoProduto(),
            produto.getTaxaJuros(),
            resultados
        );
    }
    
    private void enviarParaEventHub(String mensagem) {
        try {
            EventData eventData = new EventData(mensagem);
            eventHubProducerClient.send(List.of(eventData));
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para EventHub: " + e.getMessage());
        }
    }
}