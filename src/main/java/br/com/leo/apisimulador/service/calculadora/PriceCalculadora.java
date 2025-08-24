package br.com.leo.apisimulador.service.calculadora;

import br.com.leo.apisimulador.dto.ParcelaDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação do Sistema Francês de Amortização (PRICE)
 */
@Component
public class PriceCalculadora implements AmortizacaoCalculadora {

    @Override
    public List<ParcelaDTO> calcular(BigDecimal valorEmprestimo, BigDecimal taxaJuros, int numeroParcelas) {
        List<ParcelaDTO> parcelas = new ArrayList<>();

        // Cálculo do fator de financiamento
        BigDecimal fator = (taxaJuros.add(BigDecimal.ONE)).pow(numeroParcelas).multiply(taxaJuros)
                .divide(((taxaJuros.add(BigDecimal.ONE)).pow(numeroParcelas).subtract(BigDecimal.ONE)), 10,
                        RoundingMode.HALF_UP);

        BigDecimal parcelaConstante = valorEmprestimo.multiply(fator).setScale(2, RoundingMode.HALF_UP);
        BigDecimal saldo = valorEmprestimo;

        for (int i = 1; i <= numeroParcelas; i++) {
            BigDecimal juros = saldo.multiply(taxaJuros).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = parcelaConstante.subtract(juros).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(amortizacao);
            parcelas.add(new ParcelaDTO(i, amortizacao, juros, parcelaConstante));
        }
        return parcelas;
    }
}