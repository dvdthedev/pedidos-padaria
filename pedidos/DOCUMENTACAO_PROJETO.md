# Documentação do projeto Pedidos

## 1. O que já existia

### Visão geral

O projeto é uma aplicação Spring Boot para cadastro, consulta, edição e exclusão de pedidos da padaria.

Principais componentes:

- Backend Java/Spring Boot em `src/main/java`.
- Frontend estático em `src/main/resources/static`.
- Persistência com Spring Data JPA e MySQL.
- Maven Wrapper para compilação e execução dos testes.
- Impressão térmica implementada em `PrintServicePos`.

### Entidade `Pedido`

Antes das alterações recentes, o pedido possuía os campos:

- `id`
- `produto`
- `quantidade`
- `valorTotal`
- `dataHora`
- `descricao`
- `nomeCliente`
- `contato`
- `valorSinal`

### API

O controller utiliza a rota base `/pedidos`:

| Método | Rota | Função |
|---|---|---|
| GET | `/pedidos` | Lista pedidos futuros |
| GET | `/pedidos/passado` | Lista pedidos passados |
| GET | `/pedidos/{id}` | Busca um pedido |
| POST | `/pedidos` | Cria um pedido |
| PUT | `/pedidos/{id}` | Atualiza um pedido |
| DELETE | `/pedidos/{id}` | Exclui um pedido |

O frontend utiliza a URL `http://localhost:8091/pedidos`.

### Fluxo de criação e atualização

O frontend coleta os campos do formulário, monta um objeto JavaScript e envia JSON com `fetch`.

- Sem identificador: envia `POST`.
- Com identificador: envia `PUT`.
- O Spring/Jackson converte o JSON recebido em `Pedido`.
- O `PedidoService` utiliza `PedidoRepository` para persistir os dados.

### Frontend existente

O formulário já permitia informar:

- Produto.
- Quantidade.
- Valor total.
- Descrição.
- Nome do cliente.
- Contato.
- Valor do sinal.
- Data e hora de entrega.

O frontend também lista pedidos futuros e passados, permite edição e exclusão.

### Impressão

`PrintServicePos` recebe um objeto `Pedido` e possui dois recibos:

- Recibo do cliente.
- Recibo de produção.

Os recibos utilizam produto, quantidade, descrição, data/hora, cliente e contato. A impressão é chamada pelo método assíncrono do serviço, mas as chamadas no fluxo de POST e PUT permanecem desativadas.

### Testes existentes

O projeto possui testes de controller, serviço e contexto Spring Boot.

O teste `PedidoServiceTest.postPedido_DeveSalvarEImprimirPedido` espera chamadas de impressão que não acontecem porque a impressão está desativada no serviço. Essa falha é conhecida e não foi corrigida.

## 2. O que foi feito agora

### Nova propriedade `unidade`

Foi adicionada a propriedade `unidade` ao pedido:

```java
private Character unidade;
```

Também foram adicionados getter, setter e parâmetro no construtor.

No banco, a coluna correspondente é:

```sql
unidade CHAR(1) NOT NULL DEFAULT 'u'
```

### Valores de unidade

O frontend apresenta as seguintes opções:

| Texto exibido | Valor enviado |
|---|---|
| Unidade | `"u"` |
| Quilograma | `"k"` |
| Grama | `"g"` |
| Litro | `"l"` |

O valor padrão para novos pedidos é `"u"`.

Durante a edição, o valor retornado pelo backend seleciona a opção correspondente. Se o valor estiver ausente, o frontend utiliza `"u"` como fallback.

### JSON atual

O objeto enviado pelo frontend inclui `unidade`:

```json
{
  "produto": "Bolo",
  "quantidade": 2.5,
  "unidade": "k",
  "valorTotal": 30.0,
  "descricao": "Sem cobertura",
  "nomeCliente": "Cliente",
  "contato": "31999999999",
  "valorSinal": 0.0,
  "dataHora": "2026-08-20T14:30:00"
}
```

`unidade` é enviada como string de um caractere e `quantidade` como número JSON.

### Entrada decimal da quantidade

O campo quantidade aceita ponto ou vírgula como separador decimal:

- `2` → `2`.
- `2.5` → `2.5`.
- `2,5` → `2.5`.
- `0,5` → `0.5`.

A normalização é feita antes do envio:

```javascript
parseFloat(campos.quantidade.value.replace(',', '.'))
```

### Atualização de `unidade` no PUT

O método `PedidoService.atualizarPedido()` agora copia a unidade recebida para o pedido existente:

```java
Optional.ofNullable(pedidoAtualizado.getUnidade())
        .ifPresent(pedidoExistente::setUnidade);
```

Assim, o PUT pode alterar, por exemplo, `"k"` para `"u"`.

Foram adicionados testes unitários para confirmar a atualização com `"k"` e `"u"`.

### Resultado da última execução de testes

Comando executado:

```powershell
.\mvnw.cmd test
```

Resultado:

- 16 testes executados.
- 15 sucessos.
- 1 falha.
- 0 erros.

A única falha continua sendo `PedidoServiceTest.postPedido_DeveSalvarEImprimirPedido`, devido à impressão desativada no fluxo de criação.

## 3. Pontos pendentes

- Atualizar o teste de impressão somente quando a lógica de impressão for reativada ou o comportamento esperado for redefinido.
- Validar a unidade em chamadas externas à API, caso seja necessário restringir os valores aceitos ao conjunto `u`, `k`, `g` e `l`.
- Realizar um teste ponta a ponta usando banco de testes isolado antes de testar criação e atualização contra dados reais.
