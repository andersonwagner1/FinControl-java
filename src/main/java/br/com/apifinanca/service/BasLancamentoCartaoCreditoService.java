package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasCartaoCreditoDto;
import br.com.apifinanca.dto.BasLancamentoCartaoCreditoDto;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCartaoCredito;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasTipoMovimentacao;
import br.com.apifinanca.repository.BasBancoContaRepository;
import br.com.apifinanca.repository.BasCompetenciaRepository;
import br.com.apifinanca.repository.BasLancamentoCartaoCreditoRepository;
import br.com.apifinanca.repository.BasTipoMovimentacaoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class BasLancamentoCartaoCreditoService extends AbstractCrudService<
        BasCartaoCredito, Long, BasCartaoCreditoDto> {

    private final BasLancamentoCartaoCreditoRepository lancamentoRepository;
    private final BasBancoContaRepository bancoContaRepository;
    private final BasCompetenciaRepository competenciaRepository;
    private final BasTipoMovimentacaoRepository tipoMovimentacaoRepository;

    public BasLancamentoCartaoCreditoService(
            BasLancamentoCartaoCreditoRepository lancamentoRepository,
            BasBancoContaRepository bancoContaRepository,
            BasCompetenciaRepository competenciaRepository,
            BasTipoMovimentacaoRepository tipoMovimentacaoRepository,
            EntityManager entityManager
    ) {
        super(lancamentoRepository, (id, dto) -> dto.toEntity(id, entityManager), BasCartaoCreditoDto::from);
        this.lancamentoRepository = lancamentoRepository;
        this.bancoContaRepository = bancoContaRepository;
        this.competenciaRepository = competenciaRepository;
        this.tipoMovimentacaoRepository = tipoMovimentacaoRepository;
    }

    @Transactional
    public List<BasLancamentoCartaoCreditoDto> lancar(BasLancamentoCartaoCreditoDto request) {
        BasBancoConta bancoConta = bancoContaRepository.findById(request.bancoContaId())
                .orElseThrow(() -> naoEncontrado("Conta bancaria", request.bancoContaId()));
        BasTipoMovimentacao tipoMovimentacao = tipoMovimentacaoRepository.findById(request.tipoMovimentacaoId())
                .orElseThrow(() -> naoEncontrado("Tipo de movimentacao", request.tipoMovimentacaoId()));

        int totalParcelas = request.nrParcelas();
        int escala = Math.max(2, request.vlDebito().scale());
        BigDecimal valorParcela = request.vlDebito().divide(
                BigDecimal.valueOf(totalParcelas),
                escala,
                RoundingMode.DOWN
        );
        List<BasCartaoCredito> parcelasSalvas = new ArrayList<>(totalParcelas);
        BasCartaoCredito primeiraParcela = null;

        for (int numeroParcela = 1; numeroParcela <= totalParcelas; numeroParcela++) {
            long competenciaId;
            try {
                competenciaId = Math.addExact(request.competenciaId(), numeroParcela - 1L);
            } catch (ArithmeticException exception) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "O identificador da competencia excede o limite permitido"
                );
            }
            BasCompetencia competencia = competenciaRepository.findById(competenciaId)
                    .orElseThrow(() -> naoEncontrado("Competencia", competenciaId));

            BigDecimal valorDaParcela = numeroParcela == totalParcelas
                    ? request.vlDebito().subtract(
                            valorParcela.multiply(BigDecimal.valueOf(totalParcelas - 1L))
                    )
                    : valorParcela;
            String observacao = criarObservacao(
                    request.dsObservacao(),
                    numeroParcela,
                    totalParcelas
            );
            if (observacao != null && observacao.length() > 255) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A observacao da parcela excede o limite de 255 caracteres"
                );
            }

            BasCartaoCredito parcela = new BasCartaoCredito();
            parcela.setBancoConta(bancoConta);
            parcela.setCompetencia(competencia);
            parcela.setTipoMovimentacao(tipoMovimentacao);
            parcela.setDtMovimentacao(request.dtMovimentacao());
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(request.dtMovimentacao());
            parcela.setNrDia(String.valueOf(calendario.get(Calendar.DAY_OF_MONTH)));
            parcela.setVlCompra(valorDaParcela);
            parcela.setDsObservacao(observacao);
            parcela.setNrParcelas(totalParcelas);
            parcela.setIcSituacao(request.icSituacao());
            if (primeiraParcela != null) {
                parcela.setVinculo(primeiraParcela);
            }

            BasCartaoCredito parcelaSalva = lancamentoRepository.save(parcela);
            if (primeiraParcela == null) {
                primeiraParcela = parcelaSalva;
            }
            parcelasSalvas.add(parcelaSalva);
        }

        return parcelasSalvas.stream()
                .map(BasLancamentoCartaoCreditoDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BasCartaoCreditoDto> listarPorCompetenciaEBancoConta(
            Long competenciaId,
            Long bancoContaId
    ) {
        return lancamentoRepository
                .findByCompetencia_IdAndBancoConta_IdOrderByDtMovimentacaoAscIdAsc(
                        competenciaId,
                        bancoContaId
                )
                .stream()
                .map(BasCartaoCreditoDto::from)
                .toList();
    }

    private String criarObservacao(String observacao, int numeroParcela, int totalParcelas) {
        if (totalParcelas == 1) {
            return observacao;
        }
        String sufixo = "[" + numeroParcela + " / " + totalParcelas + "]";
        return observacao == null || observacao.isBlank()
                ? sufixo
                : observacao + " " + sufixo;
    }

    private ResponseStatusException naoEncontrado(String recurso, Long id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                recurso + " nao encontrado: " + id
        );
    }
}
