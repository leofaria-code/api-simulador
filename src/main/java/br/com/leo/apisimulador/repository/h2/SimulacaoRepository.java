package br.com.leo.apisimulador.repository.h2;

import br.com.leo.apisimulador.model.h2.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
    
    // Método para buscar por produtoId e data de referência
    List<Simulacao> findByProdutoIdAndDataReferencia(Integer produtoId, LocalDate dataReferencia);
    
    // Método para buscar apenas por data de referência
    List<Simulacao> findByDataReferencia(LocalDate dataReferencia);
    
    // Método para buscar por produto específico
    List<Simulacao> findByProdutoId(Integer produtoId);
}