package br.com.leo.apisimulador.service;

import java.util.Map;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProdutoCacheService {
    public Map<String, Object> forcarReconexao() {
        Map<String, Object> resultado = new java.util.HashMap<>();
        resultado.put("status", "RECONECTADO");
        resultado.put("mensagem", "Reconexão simulada com sucesso");
        return resultado;
    }

    public CacheStats obterEstatisticas() {
        return new CacheStats();
    }

    public Map<String, Object> forcarAtualizacao() {
        return new java.util.HashMap<>();
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private br.com.leo.apisimulador.repository.sqlserver.ProdutoRepository produtoRepository;

    public java.util.List<br.com.leo.apisimulador.model.sqlserver.Produto> buscarProdutos() {
        if (produtoRepository != null) {
            return produtoRepository.findAll();
        }
        // Fallback: retorna lista vazia se não houver acesso ao SQL Server
        return new java.util.ArrayList<>();
    }
}
