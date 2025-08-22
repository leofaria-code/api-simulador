package br.com.leo.apisimulador.service;

import br.com.leo.apisimulador.dto.Parcela;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoAmortizacaoService {
    
    public List<Parcela> calcularSAC(BigDecimal valor, BigDecimal taxa, int meses) {
        List<Parcela> parcelas = new ArrayList<>();
        
        BigDecimal amortizacao = valor.divide(BigDecimal.valueOf(meses), 2, RoundingMode.HALF_UP);
        BigDecimal saldo = valor;
        
        for (int i = 1; i <= meses; i++) {
            BigDecimal juros = saldo.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
            BigDecimal parcela = amortizacao.add(juros).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(amortizacao);
            parcelas.add(new Parcela(i, amortizacao.doubleValue(), juros.doubleValue(), parcela.doubleValue()));
        }
        return parcelas;
    }
    
    public List<Parcela> calcularPRICE(BigDecimal valor, BigDecimal taxa, int meses) {
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal fator = (taxa.add(BigDecimal.ONE)).pow(meses).multiply(taxa)
            .divide(((taxa.add(BigDecimal.ONE)).pow(meses).subtract(BigDecimal.ONE)), 10, RoundingMode.HALF_UP);
        
        BigDecimal parcelaConstante = valor.multiply(fator).setScale(2, RoundingMode.HALF_UP);
        BigDecimal saldo = valor;
        
        for (int i = 1; i <= meses; i++) {
            BigDecimal juros = saldo.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = parcelaConstante.subtract(juros).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(amortizacao);
            parcelas.add(new Parcela(i, amortizacao.doubleValue(), juros.doubleValue(), parcelaConstante.doubleValue()));
        }
        return parcelas;
    }
}