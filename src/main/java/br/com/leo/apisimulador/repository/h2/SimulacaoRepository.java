package br.com.leo.apisimulador.repository.h2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.leo.apisimulador.model.h2.Simulacao;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
    
    List<Simulacao> findByDataReferenciaAfter(LocalDateTime dataReferencia);
    
    List<Simulacao> findByProdutoId(Integer produtoId);
}