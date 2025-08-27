package br.com.leo.apisimulador.service;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.leo.apisimulador.repository.sqlserver.ProdutoRepository;
import br.com.leo.apisimulador.model.sqlserver.Produto;

@Service
public class ProdutoCacheService {

    @Autowired(required = false)
    private ProdutoRepository produtoRepository;

    public Map<String, Object> forcarReconexao() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("status", "RECONECTADO");
        resultado.put("mensagem", "Reconexão simulada com sucesso");
        return resultado;
    }

    public Map<String, Object> obterEstatisticas() {
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("produtos_carregados", produtoRepository != null ? produtoRepository.count() : 0);
        estatisticas.put("status", produtoRepository != null ? "ATIVO" : "FALLBACK");
        estatisticas.put("observacao",
                produtoRepository != null ? "Cache funcionando normalmente" : "Cache em fallback");
        return estatisticas;
    }

    public Map<String, Object> forcarAtualizacao() {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("status", "ATUALIZADO");
        resultado.put("mensagem", "Atualização simulada com sucesso");
        return resultado;
    }

    public List<Produto> buscarProdutos() {
        if (produtoRepository != null) {
            return produtoRepository.findAll();
        }
        // Fallback: retorna lista vazia se não houver acesso ao SQL Server
        return new ArrayList<>();
    }
}
