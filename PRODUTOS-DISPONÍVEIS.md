# Produtos Disponíveis para Simulação

## Resumo dos Produtos Cadastrados

O sistema possui 4 produtos de empréstimo cadastrados, cada um com diferentes faixas de valores e prazos:

### Produto 1 - Pequenos Valores
- **Código**: 1
- **Descrição**: Produto 1
- **Taxa de Juros**: 1,79% (0.0179)
- **Valor**: R$ 200,00 a R$ 10.000,00
- **Prazo**: 0 a 24 meses
- **Exemplo de Teste**: R$ 5.000,00 em 20 meses

### Produto 2 - Valores Médios
- **Código**: 2
- **Descrição**: Produto 2
- **Taxa de Juros**: 1,75% (0.0175)
- **Valor**: R$ 10.000,01 a R$ 100.000,00
- **Prazo**: 25 a 48 meses
- **Exemplo de Teste**: R$ 50.000,00 em 36 meses

### Produto 3 - Valores Altos
- **Código**: 3
- **Descrição**: Produto 3
- **Taxa de Juros**: 1,82% (0.0182)
- **Valor**: R$ 100.000,01 a R$ 1.000.000,00
- **Prazo**: 49 a 96 meses
- **Exemplo de Teste**: R$ 500.000,00 em 60 meses

### Produto 4 - Valores Muito Altos
- **Código**: 4
- **Descrição**: Produto 4
- **Taxa de Juros**: 1,51% (0.0151)
- **Valor**: Acima de R$ 1.000.000,01 (sem limite máximo)
- **Prazo**: Acima de 97 meses (sem limite máximo)
- **Exemplo de Teste**: R$ 2.000.000,00 em 120 meses

## Regras de Negócio

### Elegibilidade de Produto
O sistema automaticamente seleciona o produto adequado baseado nos parâmetros informados:
- **Valor Desejado**: Deve estar dentro da faixa do produto
- **Prazo**: Deve estar dentro da faixa do produto

### Observações Importantes
1. **Limites Exatos**: Cuidado com valores exatos nos limites entre produtos
   - R$ 10.000,00 → Produto 1 (valor máximo)
   - R$ 10.000,01 → Produto 2 (valor mínimo)

2. **Tipos de Simulação**: Cada simulação retorna dois tipos de amortização:
   - **SAC** (Sistema de Amortização Constante)
   - **PRICE** (Sistema de Amortização Francês)

3. **Resposta da API**: A simulação retorna:
   - ID da simulação
   - Código e descrição do produto selecionado
   - Taxa de juros aplicada
   - Resultados detalhados para SAC e PRICE

## Comandos de Teste Disponíveis

Utilize as tarefas pré-configuradas no VS Code para testar cada produto:
- `Simulação Produto 1 - R$ 5.000 (20 meses)`
- `Simulação Produto 2 - R$ 50.000 (36 meses)`
- `Simulação Produto 3 - R$ 500.000 (60 meses)`
- `Simulação Produto 4 - R$ 2.000.000 (120 meses)`

Ou use diretamente no PowerShell:
```powershell
$body = @{ valorDesejado = 5000.00; prazo = 20 } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/simulacoes' -Method Post -Body $body -ContentType 'application/json'
```
