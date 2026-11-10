package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasMovimentacaoLancamentoRequest;
import br.com.apifinanca.dto.BasMovimentacaoAtualizarDto;
import br.com.apifinanca.dto.BasMovimentacaoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.enumerador.EnumTipoConta;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasInvestimento;
import br.com.apifinanca.model.BasMovimentacao;
import br.com.apifinanca.model.BasMovimentacaoFinal;
import br.com.apifinanca.model.BasTipoMovimentacao;
import br.com.apifinanca.repository.BasBancoContaRepository;
import br.com.apifinanca.repository.BasCompetenciaRepository;
import br.com.apifinanca.repository.BasInvestimentoRepository;
import br.com.apifinanca.repository.BasMovimentacaoRepository;
import br.com.apifinanca.repository.BasMovimentacaoFinalRepository;
import br.com.apifinanca.repository.BasTipoMovimentacaoRepository;
import jakarta.persistence.EntityManager;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Service
public class BasMovimentacaoService extends AbstractCrudService<BasMovimentacao, Long, BasMovimentacaoDto> {

    private final BasMovimentacaoRepository movimentacaoRepository;
    private final BasBancoContaRepository bancoContaRepository;
    private final BasTipoMovimentacaoRepository tipoMovimentacaoRepository;
    private final BasCompetenciaRepository competenciaRepository;
    private final BasInvestimentoRepository investimentoRepository;
    private final BasMovimentacaoFinalRepository movimentacaoFinalRepository;
    private final EntityManager entityManager;

    public BasMovimentacaoService(
            BasMovimentacaoRepository repository,
            BasBancoContaRepository bancoContaRepository,
            BasTipoMovimentacaoRepository tipoMovimentacaoRepository,
            BasCompetenciaRepository competenciaRepository,
            BasInvestimentoRepository investimentoRepository,
            BasMovimentacaoFinalRepository movimentacaoFinalRepository,
            EntityManager entityManager
    ) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasMovimentacaoDto::from);
        this.movimentacaoRepository = repository;
        this.bancoContaRepository = bancoContaRepository;
        this.tipoMovimentacaoRepository = tipoMovimentacaoRepository;
        this.competenciaRepository = competenciaRepository;
        this.investimentoRepository = investimentoRepository;
        this.movimentacaoFinalRepository = movimentacaoFinalRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public BasMovimentacaoDto lancar(BasMovimentacaoLancamentoRequest request) {


        BasBancoConta bancoConta = bancoContaRepository.findById(request.bancoContaId()).orElseThrow(() -> naoEncontrado("Conta bancaria", request.bancoContaId()));
        BasBancoConta bancoContaDestino = request.transferenciaId() == null
                ? null
                : bancoContaRepository.findById(request.transferenciaId()).orElseThrow(() -> naoEncontrado("Conta bancaria de destino", request.transferenciaId()));
        

        

        BasTipoMovimentacao tipoMovimentacao = tipoMovimentacaoRepository.findById(request.tipoMovimentacaoId())
                .orElseThrow(() -> naoEncontrado("Tipo de movimentacao", request.tipoMovimentacaoId()));

        if(tipoMovimentacao.getIcTipoMovimentacao() == EnumTipoConta.TRANSFERENCIA || tipoMovimentacao.getIcTipoMovimentacao() == EnumTipoConta.APLICACAO || tipoMovimentacao.getIcTipoMovimentacao() == EnumTipoConta.RESGATE    ) {
            if(bancoContaDestino == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Tipo de movimentacao " + tipoMovimentacao.getDsTipoMovimentacao() + " requer conta bancaria de destino"
                );
            }
        } else {
            if(bancoContaDestino != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Tipo de movimentacao " + tipoMovimentacao.getDsTipoMovimentacao() + " nao permite conta bancaria de destino"
                );
            }
        }


        if((tipoMovimentacao.getIcTipoMovimentacao() == EnumTipoConta.APLICACAO || tipoMovimentacao.getIcTipoMovimentacao() == EnumTipoConta.RESGATE) && request.investimentoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de movimentacao " + tipoMovimentacao.getDsTipoMovimentacao() + " requer investimento"
            );
        }


        List<BasCompetencia> competencias = competenciaRepository.findByNrAnoAndNrMes(
                request.dtMovimentacao().getYear(),
                request.dtMovimentacao().getMonthValue()
        );

        if (competencias.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Competencia nao encontrada para " + request.dtMovimentacao().getMonthValue()
                            + "/" + request.dtMovimentacao().getYear()
            );
        }
        if (competencias.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mais de uma competencia encontrada para "
                            + request.dtMovimentacao().getMonthValue()
                            + "/" + request.dtMovimentacao().getYear()
            );
        }

        EnumTipoConta tipo = tipoMovimentacao.getIcTipoMovimentacao();
        if (tipo == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de movimentacao sem classificacao de Credito ou Debito"
            );
        }

        BasInvestimento investimento = null;
        if (request.investimentoId() != null) {
            investimento = investimentoRepository.findById(request.investimentoId()).orElseThrow(
                    () -> naoEncontrado("Investimento", request.investimentoId()));
        }

        BasMovimentacao movimentacao = criarMovimentacao(request, bancoConta, tipoMovimentacao, competencias.get(0), investimento, tipo, null);
        BasMovimentacao movimentacaoSalva = movimentacaoRepository.save(movimentacao);

        if (bancoContaDestino != null) {
            BasMovimentacao movimentacaoDestino = criarMovimentacao(request, bancoContaDestino, tipoMovimentacao, competencias.get(0), investimento, tipo, movimentacaoSalva);
            //Fazer o vinculo da movimentacao de origem com a movimentacao de destino
            movimentacaoRepository.save(movimentacaoDestino);
           
            //Fazer o vinculo da movimentacao de origem com a movimentacao de destino
            movimentacao.setVinculado(movimentacaoDestino);
            movimentacaoRepository.save(movimentacao);

            calcularSaldoDaConta(competencias.get(0), bancoContaDestino);
        }

        calcularSaldoDaConta(competencias.get(0), bancoConta);
        return BasMovimentacaoDto.from(movimentacaoSalva);
    }

    @Transactional
    public BasMovimentacaoDto atualizarMovimentacao(Long id, BasMovimentacaoDto dto) {
        BasMovimentacao original = movimentacaoRepository.findById(id)
                .orElseThrow(() -> naoEncontrado("Movimentacao", id));
        if (original.getVinculado() != null || dto.vinculadoId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Movimentacoes vinculadas nao podem ser alteradas individualmente"
            );
        }
        if (original.getCompetencia() == null || original.getBancoConta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A movimentacao nao possui competencia ou conta");
        }

        BasMovimentacao atualizada = dto.toEntity(id, entityManager);
        if (atualizada.getBancoConta() == null || atualizada.getCompetencia() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A movimentacao deve possuir banco, conta e competencia"
            );
        }

        movimentacaoRepository.save(atualizada);
        calcularSaldoDaConta(original.getCompetencia(), original.getBancoConta());
        if (!Objects.equals(original.getCompetencia().getId(), atualizada.getCompetencia().getId())
                || !Objects.equals(original.getBancoConta().getId(), atualizada.getBancoConta().getId())) {
            calcularSaldoDaConta(atualizada.getCompetencia(), atualizada.getBancoConta());
        }
        return BasMovimentacaoDto.from(atualizada);
    }

    @Transactional
    public void excluirMovimentacao(Long id) {
        BasMovimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> naoEncontrado("Movimentacao", id));
        if (movimentacao.getVinculado() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Movimentacoes vinculadas nao podem ser excluidas individualmente"
            );
        }
        if (movimentacao.getCompetencia() == null || movimentacao.getBancoConta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A movimentacao nao possui competencia ou conta");
        }

        BasCompetencia competencia = movimentacao.getCompetencia();
        BasBancoConta bancoConta = movimentacao.getBancoConta();
        movimentacaoRepository.delete(movimentacao);
        calcularSaldoDaConta(competencia, bancoConta);
    }


    /* public void adicionarRendimento(BasMovimentacaoAtualizarDto request) {
        BasBancoConta bancoConta = bancoContaRepository.findById(request.bancoContaId()).orElseThrow(() -> naoEncontrado("Conta bancaria", request.bancoContaId()));
        List<BasCompetencia> competencias = competenciaRepository.findByNrAnoAndNrMes(
                request.ano(),
                request.mes()
        );

        if (competencias.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Competencia nao encontrada para " + request.mes()
                            + "/" + request.ano()
            );
        }
        if (competencias.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mais de uma competencia encontrada para "
                            + request.mes()
                            + "/" + request.ano()
            );
        }

        calcularSaldoDaConta(competencias.get(0), bancoConta);
    }*/
     public void atualizarSaldo(BasMovimentacaoAtualizarDto request) {
        BasBancoConta bancoConta = bancoContaRepository.findById(request.bancoContaId()).orElseThrow(() -> naoEncontrado("Conta bancaria", request.bancoContaId()));
        List<BasCompetencia> competencias = competenciaRepository.findByNrAnoAndNrMes(
                request.ano(),
                request.mes()
        );

        if (competencias.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Competencia nao encontrada para " + request.mes()
                            + "/" + request.ano()
            );
        }
        if (competencias.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mais de uma competencia encontrada para "
                            + request.mes()
                            + "/" + request.ano()
            );
        }

        calcularSaldoDaConta(competencias.get(0), bancoConta);
    }

    private void calcularSaldoDaConta(BasCompetencia competencia, BasBancoConta bancoConta) {
        BigDecimal saldoInicial = obterSaldoFinalAnterior(competencia, bancoConta);
        BigDecimal saldo = saldoInicial;
        BigDecimal totalCredito = BigDecimal.ZERO;
        BigDecimal totalDebito = BigDecimal.ZERO;
        List<BasMovimentacao> movimentacoes = movimentacaoRepository.buscarAtivasPorCompetenciaEConta(
                competencia.getId(),
                bancoConta.getId(),
                EnumSimNao.SIM
        );

        for (BasMovimentacao movimentacao : movimentacoes) {
            BigDecimal credito = valorOuZero(movimentacao.getVlCredito());
            BigDecimal debito = valorOuZero(movimentacao.getVlDebito());
            saldo = saldo.add(credito).subtract(debito);
            totalCredito = totalCredito.add(credito);
            totalDebito = totalDebito.add(debito);
            movimentacao.setVlSaldo(saldo);
        }

        movimentacaoRepository.saveAll(movimentacoes);

        BasMovimentacaoFinal movimentacaoFinal = movimentacaoFinalRepository
                .findByCompetencia_IdAndBancoConta_Id(competencia.getId(), bancoConta.getId())
                .orElseGet(BasMovimentacaoFinal::new);
        movimentacaoFinal.setCompetencia(competencia);
        movimentacaoFinal.setBancoConta(bancoConta);
        movimentacaoFinal.setVlSaldoInicial(saldoInicial);
        movimentacaoFinal.setVlTotalCredito(totalCredito);
        movimentacaoFinal.setVlTotalDebito(totalDebito);
        movimentacaoFinal.setVlSaldoFinal(saldo);
        movimentacaoFinalRepository.save(movimentacaoFinal);
    }

    private BigDecimal obterSaldoFinalAnterior(BasCompetencia competencia, BasBancoConta bancoConta) {
        if (competencia.getId() == null || competencia.getId() <= 1) {
            return BigDecimal.ZERO;
        }

        int anoAnterior = competencia.getNrMes() == 1
                ? competencia.getNrAno() - 1
                : competencia.getNrAno();
        int mesAnterior = competencia.getNrMes() == 1 ? 12 : competencia.getNrMes() - 1;
        List<BasCompetencia> competenciasAnteriores = competenciaRepository.findByNrAnoAndNrMes(
                anoAnterior,
                mesAnterior
        );
        if (competenciasAnteriores.isEmpty()) {
            return BigDecimal.ZERO;
        }
        if (competenciasAnteriores.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mais de uma competencia encontrada para " + mesAnterior + "/" + anoAnterior
            );
        }

        Long competenciaAnteriorId = competenciasAnteriores.get(0).getId();
        if (competenciaAnteriorId != null && competenciaAnteriorId.equals(competencia.getId() - 1)) {
            return movimentacaoFinalRepository.findByCompetencia_IdAndBancoConta_Id(
                            competencia.getId() - 1,
                            bancoConta.getId()
                    )
                    .map(BasMovimentacaoFinal::getVlSaldoFinal)
                    .map(this::valorOuZero)
                    .orElse(BigDecimal.ZERO);
        }

        return movimentacaoFinalRepository.findByCompetencia_IdAndBancoConta_Id(
                        competenciaAnteriorId,
                        bancoConta.getId()
                )
                .map(BasMovimentacaoFinal::getVlSaldoFinal)
                .map(this::valorOuZero)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    @Transactional(readOnly = true)
    public List<BasMovimentacaoDto> listarPorCompetenciaEBancoConta(
            Integer ano,
            Integer mes,
            Long bancoId,
            Long contaId
    ) {
        return movimentacaoRepository.buscarPorCompetenciaEBancoConta(
                        ano,
                        mes,
                        bancoId,
                        contaId,
                        EnumSimNao.SIM
                ).stream()
                .map(BasMovimentacaoDto::from)
                .toList();
    }

    private BasMovimentacao criarMovimentacao(
            BasMovimentacaoLancamentoRequest request,
            BasBancoConta bancoConta,
            BasTipoMovimentacao tipoMovimentacao,
            BasCompetencia competencia,
            BasInvestimento investimento,
            EnumTipoConta tipo, 
            BasMovimentacao movimentacaoDestino           
    ) {
        BasMovimentacao movimentacao = new BasMovimentacao();
        movimentacao.setBancoConta(bancoConta);
        movimentacao.setTipoMovimentacao(tipoMovimentacao);
        movimentacao.setCompetencia(competencia);
        movimentacao.setNrPosicao(request.nrPosicao());
        movimentacao.setDtMovimentacao(Date.valueOf(request.dtMovimentacao()));
        movimentacao.setNrDia(request.dtMovimentacao().getDayOfMonth());
        movimentacao.setDsObservacao(request.observacao());
        movimentacao.setInvestimento(investimento);
        movimentacao.setVinculado(movimentacaoDestino);

        if(movimentacaoDestino == null) {
           switch (tipo) {
            case CREDITO, RESGATE -> {
                movimentacao.setVlCredito(request.valor());
                movimentacao.setVlDebito(BigDecimal.ZERO);
                }
            case TRANSFERENCIA, DEBITO, APLICACAO -> {
                movimentacao.setVlDebito(request.valor());
                movimentacao.setVlCredito(BigDecimal.ZERO);
            }
        }
        } else {
            switch (tipo) {
            case CREDITO, RESGATE -> {
                movimentacao.setVlDebito(request.valor());
                movimentacao.setVlCredito(BigDecimal.ZERO);                
            }
            case TRANSFERENCIA, DEBITO, APLICACAO -> {
                movimentacao.setVlCredito(request.valor());
                movimentacao.setVlDebito(BigDecimal.ZERO);               
            }
        }
        }
        return movimentacao;
    }

    private ResponseStatusException naoEncontrado(String recurso, Long id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                recurso + " nao encontrado: " + id
        );
    }

   
}
