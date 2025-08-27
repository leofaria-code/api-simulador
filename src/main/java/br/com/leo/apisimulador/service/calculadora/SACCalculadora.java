package br.com.leo.apisimulador.service.calculadora;

import org.springframework.stereotype.Component;

import br.com.leo.apisimulador.dto.simulacao.ParcelaDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do Sistema de Amortização Constante (SAC)
 */
@Component
public class SACCalculadora implements AmortizacaoCalculadora {

    @Override
    public List<ParcelaDTO> calcular(BigDecimal valorEmprestimo, BigDecimal taxaJuros, int numeroParcelas) {
        List<ParcelaDTO> parcelas = new ArrayList<>();

        BigDecimal amortizacao = valorEmprestimo.divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);
        BigDecimal saldo = valorEmprestimo;

        for (int i = 1; i <= numeroParcelas; i++) {
            BigDecimal juros = saldo.multiply(taxaJuros).setScale(2, RoundingMode.HALF_UP);
            BigDecimal parcela = amortizacao.add(juros).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(amortizacao);
            parcelas.add(new ParcelaDTO(i, amortizacao, juros, parcela));
        }
        return parcelas;
    }
}