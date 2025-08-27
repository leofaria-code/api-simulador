package br.com.leo.apisimulador.service.calculadora;

import java.math.BigDecimal;
import java.util.List;

import br.com.leo.apisimulador.dto.simulacao.ParcelaDTO;

/**
 * Interface para cálculos de amortização de empréstimos
 */
public interface AmortizacaoCalculadora {

    /**
     * Calcula as parcelas de um empréstimo
     * 
     * @param valorEmprestimo Valor total do empréstimo
     * @param taxaJuros       Taxa de juros por período (ex: 0.01 para 1% ao mês)
     * @param numeroParcelas  Número de parcelas do empréstimo
     * @return Lista de parcelas calculadas
     */
    List<ParcelaDTO> calcular(BigDecimal valorEmprestimo, BigDecimal taxaJuros, int numeroParcelas);
}