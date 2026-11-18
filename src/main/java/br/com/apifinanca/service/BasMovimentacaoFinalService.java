package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasMovimentacaoDto;
import br.com.apifinanca.dto.BasMovimentacaoFinalConsultaDto;
import br.com.apifinanca.dto.BasMovimentacaoFinalDto;
import br.com.apifinanca.dto.BasMovimentacaoFinalFiltroDto;
import br.com.apifinanca.dto.BasMovimentacaoLancamentoRequest;
import br.com.apifinanca.dto.BasSaldoFinalDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasMovimentacao;
import br.com.apifinanca.model.BasMovimentacaoFinal;
import br.com.apifinanca.repository.BasBancoContaRepository;
import br.com.apifinanca.repository.BasCompetenciaRepository;
import br.com.apifinanca.repository.BasMovimentacaoFinalRepository;
import br.com.apifinanca.repository.BasMovimentacaoRepository;
import br.com.apifinanca.repository.BasTipoMovimentacaoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class BasMovimentacaoFinalService extends AbstractCrudService<
        BasMovimentacaoFinal, Long, BasMovimentacaoFinalDto> {

    private final BasMovimentacaoFinalRepository repository;
    private final BasMovimentacaoRepository movimentacaoRespository;
    private final BasCompetenciaRepository competenciaRespository;
    private final BasBancoContaRepository bancoContaRepository;

    private final BasMovimentacaoService movimentacaoService;


    public BasMovimentacaoFinalService(
            BasMovimentacaoFinalRepository repository,
            BasMovimentacaoRepository movimentacaoRespository,
            BasCompetenciaRepository competenciaRespository,
        BasTipoMovimentacaoRepository tipoMovimentacaoRepository,
         BasBancoContaRepository bancoContaRepository,
         BasMovimentacaoService movimentacaoService,
            EntityManager entityManager
    ) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasMovimentacaoFinalDto::from);
        this.repository = repository;
        this.movimentacaoRespository = movimentacaoRespository;
        this.competenciaRespository = competenciaRespository;
        this.bancoContaRepository = bancoContaRepository;
        this.movimentacaoService = movimentacaoService;
        
    }

    public List<BasMovimentacaoFinalDto> listarPorAno(Integer ano) {
        return repository.findByCompetenciaNrAno(ano).stream()
                .map(BasMovimentacaoFinalDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BasMovimentacaoFinalFiltroDto> filtrarPorBancoContaEAno(
            Integer ano,
            Long bancoId,
            Long contaId
    ) {
        return repository.buscarPorAnoEBancoConta(ano, bancoId, contaId).stream()
                .map(BasMovimentacaoFinalFiltroDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BasMovimentacaoFinalConsultaDto consultar(
            Integer ano,
            Integer mes,
            Long bancoId,
            Long contaId
    ) {
        List<BasMovimentacaoFinal> resultados = repository.buscarPorCompetenciaEBancoConta(
                ano,
                mes,
                bancoId,
                contaId
        );

        if (resultados.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Movimentacao final nao encontrada para a competencia e conta informadas"
            );
        }
        if (resultados.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ha mais de uma movimentacao final para a competencia e conta informadas"
            );
        }

        return BasMovimentacaoFinalConsultaDto.from(resultados.get(0));
    }

    /**
     * Saldo 
     */
  public void saldoFinal(BasSaldoFinalDto saldoFinalDto) {

    // Buscar competência
    List<BasCompetencia> competenciaModel = competenciaRespository.findByNrAnoAndNrMes( saldoFinalDto.getAno(),saldoFinalDto.getMes());

    if (competenciaModel == null || competenciaModel.isEmpty()) {
        throw new RuntimeException("Competência não encontrada.");
    }

    BasCompetencia competencia = competenciaModel.get(0);

    // Buscar movimentação final da conta na competência
    Optional<BasMovimentacaoFinal> movimentacaoFinalModel =repository.findByCompetencia_IdAndBancoConta_Id(competencia.getId(),saldoFinalDto.getBancoContaId());

    BasMovimentacaoFinal movimentacaoFinal;

    //VERIFICA SE EXISTE MOVIMENTAÇÃO FINAL CADATRADO
    if (movimentacaoFinalModel.isPresent()) {
        movimentacaoFinal = movimentacaoFinalModel.get();
    } else {

        // Buscar registro da competência anterior
        Optional<BasMovimentacaoFinal> registroAnterior =repository.findByCompetencia_IdAndBancoConta_Id(competencia.getId() - 1, saldoFinalDto.getBancoContaId()                );

        movimentacaoFinal = new BasMovimentacaoFinal();

        movimentacaoFinal.setVlSaldoInicial(registroAnterior.map(BasMovimentacaoFinal::getVlSaldoFinal).orElse(BigDecimal.ZERO));

        movimentacaoFinal.setVlSaldoFinal(movimentacaoFinal.getVlSaldoInicial());

        // IMPORTANTE: associar competência e conta
        // Ajuste os setters conforme sua entidade.
        movimentacaoFinal.setCompetencia(competencia);
        movimentacaoFinal.setBancoConta(movimentacaoFinalModel.get().getBancoConta());
        movimentacaoFinal.setIcFechado(EnumSimNao.NAO);
        
        // Salve a conta se a entidade exigir esse relacionamento.
        movimentacaoFinal = repository.save(movimentacaoFinal);
    }

    // Saldo atual
    BigDecimal saldoAtual = movimentacaoFinal.getVlSaldoFinal() != null
            ? movimentacaoFinal.getVlSaldoFinal()
            : BigDecimal.ZERO;

    // Saldo informado pelo usuário
    BigDecimal saldoInformado = BigDecimal.valueOf(saldoFinalDto.getSaldoFinal());

    // Diferença entre saldo informado e saldo atual
    BigDecimal valorRendimento = saldoInformado.subtract(saldoAtual);

    if (valorRendimento.compareTo(BigDecimal.ZERO) == 0) {
        return;
    }

    // Identificar tipo de movimentação
    Long tipoMovimentacaoId = valorRendimento.compareTo(BigDecimal.ZERO) > 0 ? 6L: 21L;

    // Buscar rendimentos já lançados na competência
    List<BasMovimentacao> listarRendimentos =movimentacaoRespository.buscarSeExisteRendimento(saldoFinalDto.getBancoContaId(),competencia.getId());

    // Buscar conta bancária
    BasBancoConta bancoConta = bancoContaRepository.findById(saldoFinalDto.getBancoContaId()).orElseThrow(() ->
                    new RuntimeException("Conta bancária não encontrada.")
            );

    if (listarRendimentos != null && !listarRendimentos.isEmpty()) {

        // Último rendimento cadastrado
        BasMovimentacao rendimento = listarRendimentos.get(listarRendimentos.size() - 1);

        BigDecimal creditoAnterior = rendimento.getVlCredito() != null
                ? rendimento.getVlCredito()
                : BigDecimal.ZERO;

        BigDecimal debitoAnterior = rendimento.getVlDebito() != null
                ? rendimento.getVlDebito()
                : BigDecimal.ZERO;

        // Somar o rendimento existente à diferença encontrada
        valorRendimento = valorRendimento
                .add(creditoAnterior)
                .subtract(debitoAnterior);

        if (valorRendimento.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        BigDecimal vlCredito = BigDecimal.ZERO;
        BigDecimal vlDebito = BigDecimal.ZERO;

        if (valorRendimento.compareTo(BigDecimal.ZERO) > 0) {

            vlCredito = valorRendimento;
            tipoMovimentacaoId = 6L;

        } else {

            vlDebito = valorRendimento.abs();
            tipoMovimentacaoId = 21L;
        }

        BasMovimentacaoDto dto = BasMovimentacaoDto.builder()
                .id(rendimento.getId())
                .bancoContaId(rendimento.getBancoConta().getId())
                .competenciaId(competencia.getId())
                .dsObservacao(rendimento.getDsObservacao())
                .dtMovimentacao(rendimento.getDtMovimentacao())
                .icSituacao(EnumSimNao.SIM)
                .icCalcular(EnumSimNao.SIM)
                .tipoMovimentacaoId(tipoMovimentacaoId)
                .vlDebito(vlDebito)
                .vlCredito(vlCredito)
                .build();

        movimentacaoService.atualizarMovimentacao(
                rendimento.getId(),
                dto
        );

    } else {

        // Criar novo lançamento de rendimento
        BigDecimal valorLancamento = valorRendimento.abs();

        BasMovimentacaoLancamentoRequest request =
                BasMovimentacaoLancamentoRequest.builder()
                        .observacao("Rendimento gerado automaticamente")
                        .bancoContaId(bancoConta.getId())
                        .dtMovimentacao(
                                pegarUltimoDia(
                                        competencia.getNrMes(),
                                        competencia.getNrAno()
                                )
                        )
                        .nrPosicao(50)
                        .tipoMovimentacaoId(tipoMovimentacaoId)
                        .valor(valorLancamento)
                        .build();

        movimentacaoService.lancar(request);
    }
}

    private LocalDate pegarUltimoDia(Integer mes, Integer ano) {
     return YearMonth.of(ano, mes).atEndOfMonth();
}
}
