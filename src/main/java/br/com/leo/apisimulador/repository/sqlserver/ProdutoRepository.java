package br.com.leo.apisimulador.repository.sqlserver;

import br.com.leo.apisimulador.model.sqlserver.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}
