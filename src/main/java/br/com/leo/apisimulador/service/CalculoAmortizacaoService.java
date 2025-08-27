package br.com.leo.apisimulador.service;

import br.com.leo.apisimulador.dto.simulacao.ParcelaDTO;
import br.com.leo.apisimulador.service.calculadora.PriceCalculadora;
import br.com.leo.apisimulador.service.calculadora.SACCalculadora;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço fachada para os diferentes tipos de cálculo de amortização
 */
@Service
public class CalculoAmortizacaoService {

    private final SACCalculadora sacCalculadora;
    private final PriceCalculadora priceCalculadora;

    public CalculoAmortizacaoService(SACCalculadora sacCalculadora, PriceCalculadora priceCalculadora) {
        this.sacCalculadora = sacCalculadora;
        this.priceCalculadora = priceCalculadora;
    }

    /**
     * Calcula parcelas usando o Sistema de Amortização Constante (SAC)
     */
    public List<ParcelaDTO> calcularSAC(BigDecimal valor, BigDecimal taxa, int meses) {
        return sacCalculadora.calcular(valor, taxa, meses);
    }

    /**
     * Calcula parcelas usando o Sistema Francês de Amortização (PRICE)
     */
    public List<ParcelaDTO> calcularPRICE(BigDecimal valor, BigDecimal taxa, int meses) {
        return priceCalculadora.calcular(valor, taxa, meses);
    }
}