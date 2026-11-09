# API Financa

API REST de transacoes financeiras feita com Java 17, Spring Boot, Spring Data JPA e PostgreSQL.

## Requisitos

- Java 17
- Maven 3.9+
- Docker com Docker Compose

## Executar

1. Inicie o PostgreSQL:

   ```bash
   docker compose up -d
   ```

2. Inicie a API:

   ```bash
   mvn spring-boot:run
   ```

A aplicacao fica disponivel em `http://localhost:8080`.

## Swagger

Documentacao interativa: `http://localhost:8080/swagger-ui.html`

Especificacao OpenAPI: `http://localhost:8080/v3/api-docs`

## Endpoints

| Metodo | Rota | Descricao |
|---|---|---|
| GET | `/` | Dashboard financeiro (importacao JSON ou fonte JSON configurada) |
| GET | `/api/transacoes` | Lista transacoes |
| GET | `/api/transacoes/{id}` | Busca uma transacao |
| POST | `/api/transacoes` | Cadastra uma transacao |
| PUT | `/api/transacoes/{id}` | Atualiza uma transacao |
| DELETE | `/api/transacoes/{id}` | Exclui uma transacao |
| GET/POST | `/api/aplicacoes` | Lista ou cadastra aplicacoes |
| GET/PUT/DELETE | `/api/aplicacoes/{id}` | Consulta, atualiza ou exclui uma aplicacao |
| GET/POST | `/api/bancos` | Lista ou cadastra bancos |
| GET/PUT/DELETE | `/api/bancos/{id}` | Consulta, atualiza ou exclui um banco |
| GET/POST | `/api/bancos-contas` | Lista ou cadastra contas bancarias |
| GET | `/api/bancos-contas/disponiveis?mes={mes}&ano={ano}` | Lista bancos e contas ativos no periodo |
| GET/PUT/DELETE | `/api/bancos-contas/{id}` | Consulta, atualiza ou exclui uma conta bancaria |
| GET/POST | `/api/cartoes-credito` | Lista ou cadastra cartoes de credito |
| GET | `/api/cartoes-credito/consulta?competenciaId={competenciaId}&bancoContaId={bancoContaId}` | Lista lancamentos do cartao por competencia e conta |
| GET/PUT/DELETE | `/api/cartoes-credito/{id}` | Consulta, atualiza ou exclui um cartao |
| GET/POST | `/api/categorias-movimentacao` | Lista ou cadastra categorias |
| GET/PUT/DELETE | `/api/categorias-movimentacao/{id}` | Consulta, atualiza ou exclui uma categoria |
| GET/POST | `/api/competencias` | Lista ou cadastra competencias |
| GET | `/api/competencias/ano/{ano}` | Filtra competencias pelo ano |
| GET/PUT/DELETE | `/api/competencias/{id}` | Consulta, atualiza ou exclui uma competencia |
| GET/POST | `/api/contas` | Lista ou cadastra contas |
| GET/PUT/DELETE | `/api/contas/{id}` | Consulta, atualiza ou exclui uma conta |
| GET/POST | `/api/investimentos` | Lista ou cadastra investimentos |
| GET/PUT/DELETE | `/api/investimentos/{id}` | Consulta, atualiza ou exclui um investimento |
| GET/POST | `/api/movimentacoes` | Lista ou cadastra movimentacoes |
| GET | `/api/movimentacoes/consulta?ano={ano}&mes={mes}&bancoId={bancoId}&contaId={contaId}` | Lista movimentacoes ativas por competencia e conta |
| POST | `/api/movimentacoes/lancamento` | Registra lancamento e define credito/debito e competencia pela data |
| POST | `/api/cartoes-credito/lancamento-cartao` | Registra debito no cartao e gera as parcelas |
| GET/PUT/DELETE | `/api/movimentacoes/{id}` | Consulta, atualiza ou exclui uma movimentacao |
| GET/POST | `/api/movimentacoes-finais` | Lista ou cadastra movimentacoes finais |
| GET | `/api/movimentacoes-finais/ano/{ano}` | Filtra movimentacoes finais pelo ano da competencia |
| GET | `/api/movimentacoes-finais/consulta?ano={ano}&mes={mes}&bancoId={bancoId}&contaId={contaId}` | Consulta saldos e situacao de fechamento por competencia e conta |
| GET/PUT/DELETE | `/api/movimentacoes-finais/{id}` | Consulta, atualiza ou exclui uma movimentacao final |
| GET/POST | `/api/tipos-movimentacao` | Lista ou cadastra tipos de movimentacao |
| GET | `/api/tipos-movimentacao/ativos` | Lista tipos ativos com categoria e tipo |
| GET/PUT/DELETE | `/api/tipos-movimentacao/{id}` | Consulta, atualiza ou exclui um tipo de movimentacao |
| GET/POST | `/api/transferencias` | Lista ou cadastra transferencias |
| GET/PUT/DELETE | `/api/transferencias/{bancoContaId}/{transferirParaId}` | Consulta, atualiza ou exclui uma transferencia |

Exemplo de cadastro:

```json
{
  "descricao": "Salario",
  "valor": 5000.00,
  "tipo": "ENTRADA",
  "data": "2026-11-01"
}
```

`tipo` aceita `ENTRADA` ou `SAIDA`; `valor` deve ser maior que zero.

Os DTOs de entidades relacionadas recebem e retornam os IDs das relacoes. A transferencia usa uma chave composta por `bancoContaId` e `transferirParaId`. `icRelatorio` e armazenado como texto ate que os valores validos do dominio sejam definidos.

`GET /api/bancos-contas/disponiveis` recebe `mes` (1 a 12) e `ano`. A resposta inclui somente vínculos ativos de banco/conta para os quais a abertura ocorre até a competência informada e o fechamento é nulo ou ocorre na competência informada ou depois dela.

O lancamento recebe `bancoContaId`, `tipoMovimentacaoId`, `nrPosicao`, `dtMovimentacao`, `valor`, `observacao` e, opcionalmente, `investimentoId`. A competencia e localizada pelo mes e ano da data informada.

O lancamento no cartao recebe `dtMovimentacao`, `vlDebito`, `dsObservacao`, `icSituacao`, `bancoContaId`, `competenciaId`, `tipoMovimentacaoId` e, opcionalmente, `nrParcelas` (padrao `1`). Para compras parceladas, o valor e dividido entre as parcelas, as competencias avancam a partir do `competenciaId` informado, a data e mantida e cada observacao recebe `[parcela / total]`.

O dashboard em `/` consome uma lista JSON de movimentacoes. Os dados podem ser carregados por arquivo ou URL, como lista direta ou dentro de `valor`, `data`, `content` ou `movimentacoes`. Ele reconhece os nomes em maiusculas da View e aliases camelCase. A fonte pode ser configurada no proprio dashboard; se estiver em outro dominio, precisa permitir CORS.

## Configuracao do banco

As configuracoes padrao apontam para o PostgreSQL do `docker-compose.yml`. Podem ser sobrescritas pelas variaveis `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`. O schema e atualizado automaticamente pelo Hibernate durante o desenvolvimento.

## Testes

```bash
mvn test
```
