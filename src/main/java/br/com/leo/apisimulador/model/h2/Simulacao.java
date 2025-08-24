package br.com.leo.apisimulador.model.h2;

import br.com.leo.apisimulador.model.sqlserver.Produto;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "SIMULACAO", schema = "PUBLIC")
@Data
public class Simulacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSIMULACAO", nullable = false)
    private Long idSimulacao;

    @Column(name = "DATA_REFERENCIA")
    private LocalDate dataReferencia = LocalDate.now();

    @Column(name = "VALOR_DESEJADO")
    private BigDecimal valorDesejado;

    @Column(name = "PRAZO")
    private Integer prazo;

    @Column(name = "PRODUTO_ID")
    private Integer produtoId;

    @Column(name = "RESULTADO_JSON", columnDefinition = "TEXT")
    private String resultadoJson;

    // Relacionamento com Produto (transient para não persistir no H2)
    @Transient
    private Produto produto;
}