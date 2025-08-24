package br.com.leo.apisimulador.repository.h2;

import br.com.leo.apisimulador.model.h2.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {
    List<Simulacao> findByDataReferencia(LocalDate dataReferencia);
    List<Simulacao> findByProdutoIdAndDataReferencia(Integer produtoId, LocalDate dataReferencia);
    @NonNull
    Page<Simulacao> findAll(@NonNull Pageable pageable);
}