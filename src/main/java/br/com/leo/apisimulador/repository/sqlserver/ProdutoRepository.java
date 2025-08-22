package br.com.leo.apisimulador.repository.sqlserver;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.leo.apisimulador.model.sqlserver.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
}