package br.com.leo.apisimulador.model.sqlserver;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {
    @Id
    @Column(name = "CO_PRODUTO")
    private Integer codigoProduto;

    @Column(name = "NO_PRODUTO", nullable = false, length = 200)
    private String descricaoProduto;

    @Column(name = "PC_TAXA_JUROS", nullable = false, precision = 10, scale = 9)
    private BigDecimal taxaJuros;

    @Column(name = "NU_MINIMO_MESES", nullable = false)
    private Short minimoMeses;

    @Column(name = "NU_MAXIMO_MESES")
    private Short maximoMeses;

    @Column(name = "VR_MINIMO", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorMinimo;

    @Column(name = "VR_MAXIMO", precision = 18, scale = 2)
    private BigDecimal valorMaximo;
}