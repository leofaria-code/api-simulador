package br.com.leo.apisimulador.service;

import br.com.leo.apisimulador.dto.*;
import br.com.leo.apisimulador.model.h2.Simulacao;
import br.com.leo.apisimulador.model.sqlserver.Produto;
import br.com.leo.apisimulador.repository.h2.SimulacaoRepository;
import br.com.leo.apisimulador.repository.sqlserver.ProdutoRepository;
import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulacaoService {
    
    private final ProdutoRepository produtoRepository;
    private final SimulacaoRepository simulacaoRepository;
    private final CalculoAmortizacaoService calculoService;
    private final EventHubProducerClient eventHubProducerClient;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public SimulacaoResponse simular(SimulacaoRequest requisicao) {
        try {
            Produto produto = buscarProdutoElegivel(requisicao);
            List<ResultadoSimulacao> resultados = calcularSimulacoes(requisicao, produto);
            
            // Criar e persistir a simulação
            Simulacao simulacao = criarSimulacao(requisicao, produto);
            
            // Criar a resposta com o record
            SimulacaoResponse resposta = new SimulacaoResponse(
                null, // ID será atualizado após persistência
                produto.getCodigoProduto(),
                produto.getDescricaoProduto(),
                produto.getTaxaJuros(),
                resultados
            );
            
            // Persistir a simulação
            try {
                simulacao.setResultadoJson(objectMapper.writeValueAsString(resposta));
                simulacao = simulacaoRepository.save(simulacao);
                
                // Criar nova resposta com ID atualizado
                resposta = new SimulacaoResponse(
                    simulacao.getIdSimulacao(),
                    produto.getCodigoProduto(),
                    produto.getDescricaoProduto(),
                    produto.getTaxaJuros(),
                    resultados
                );
                
                // Atualizar o JSON com o ID correto
                simulacao.setResultadoJson(objectMapper.writeValueAsString(resposta));
                simulacaoRepository.save(simulacao);
                
            } catch (Exception e) {
                log.error("Erro ao persistir simulação: {}", e.getMessage(), e);
                throw new RuntimeException("Falha ao persistir simulação", e);
            }
            
            enviarParaEventHub(simulacao.getResultadoJson());
            
            return resposta;
            
        } catch (Exception e) {
            log.error("Erro ao processar simulação: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao processar simulação: " + e.getMessage(), e);
        }
    }
    
    private Produto buscarProdutoElegivel(SimulacaoRequest requisicao) {
        return produtoRepository.findAll().stream()
            .filter(p -> isProdutoElegivel(p, requisicao))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("Nenhum produto encontrado para os parâmetros informados."));
    }
    
    private boolean isProdutoElegivel(Produto produto, SimulacaoRequest requisicao) {
        return requisicao.valorDesejado().compareTo(produto.getValorMinimo()) >= 0 &&
            requisicao.valorDesejado().compareTo(produto.getValorMaximo()) <= 0 &&
            requisicao.prazo() >= produto.getMinimoMeses() &&
            requisicao.prazo() <= produto.getMaximoMeses();
    }
    
    private List<ResultadoSimulacao> calcularSimulacoes(SimulacaoRequest requisicao, Produto produto) {
        List<Parcela> parcelasSAC = calculoService.calcularSAC(
            requisicao.valorDesejado(),
            produto.getTaxaJuros(),
            requisicao.prazo());
        
        List<Parcela> parcelasPRICE = calculoService.calcularPRICE(
            requisicao.valorDesejado(),
            produto.getTaxaJuros(),
            requisicao.prazo());
        
        return List.of(
            new ResultadoSimulacao(TipoSimulacao.SAC, parcelasSAC),
            new ResultadoSimulacao(TipoSimulacao.PRICE, parcelasPRICE));
    }
    
    private Simulacao criarSimulacao(SimulacaoRequest requisicao, Produto produto) {
        Simulacao simulacao = new Simulacao();
//        simulacao.setProduto(produto);
        simulacao.setProdutoId(produto.getCodigoProduto());
        simulacao.setValorDesejado(requisicao.valorDesejado());
        simulacao.setPrazo(requisicao.prazo());
        return simulacao;
    }
    
    private void enviarParaEventHub(String mensagem) {
        try {
            eventHubProducerClient.send(List.of(new EventData(mensagem)));
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para EventHub: {}", e.getMessage(), e);
        }
    }
}