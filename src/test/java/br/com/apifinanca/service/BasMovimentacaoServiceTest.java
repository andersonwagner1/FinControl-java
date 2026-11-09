package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasMovimentacaoLancamentoRequest;
import br.com.apifinanca.dto.BasMovimentacaoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.enumerador.EnumTipoConta;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCompetencia;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasMovimentacaoServiceTest {

    @Mock
    private BasMovimentacaoRepository movimentacaoRepository;
    @Mock
    private BasBancoContaRepository bancoContaRepository;
    @Mock
    private BasTipoMovimentacaoRepository tipoMovimentacaoRepository;
    @Mock
    private BasCompetenciaRepository competenciaRepository;
    @Mock
    private BasInvestimentoRepository investimentoRepository;
    @Mock
    private BasMovimentacaoFinalRepository movimentacaoFinalRepository;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BasMovimentacaoService service;

    private BasBancoConta bancoConta;
    private BasCompetencia competencia;

    @BeforeEach
    void prepararDependencias() {
        bancoConta = new BasBancoConta();
        bancoConta.setId(10L);
        competencia = new BasCompetencia();
        competencia.setId(20L);
        competencia.setNrAno(2026);
        competencia.setNrMes(11);

        lenient().when(bancoContaRepository.findById(10L)).thenReturn(Optional.of(bancoConta));
        lenient().when(competenciaRepository.findByNrAnoAndNrMes(2026, 11))
                .thenReturn(List.of(competencia));
        lenient().when(movimentacaoRepository.save(any())).thenAnswer(invocation -> {
            var movimentacao = invocation.getArgument(0, br.com.apifinanca.model.BasMovimentacao.class);
            movimentacao.setId(30L);
            return movimentacao;
        });
    }

    @Test
    void deveRegistrarCreditoECalcularDiaECompetencia() {
        BasTipoMovimentacao tipo = novoTipo(EnumTipoConta.CREDITO);
        when(tipoMovimentacaoRepository.findById(40L)).thenReturn(Optional.of(tipo));

        BasMovimentacaoDto resultado = service.lancar(novoRequest(null));

        ArgumentCaptor<br.com.apifinanca.model.BasMovimentacao> captor =
                ArgumentCaptor.forClass(br.com.apifinanca.model.BasMovimentacao.class);
        verify(movimentacaoRepository).save(captor.capture());
        var salvo = captor.getValue();

        assertEquals(new BigDecimal("125.50"), salvo.getVlCredito());
        assertEquals(null, salvo.getVlDebito());
        assertEquals(4, salvo.getNrDia());
        assertEquals(competencia, salvo.getCompetencia());
        assertEquals(30L, resultado.id());
        verify(competenciaRepository).findByNrAnoAndNrMes(2026, 11);
    }

    @Test
    void deveRegistrarAplicacaoComoDebito() {
        BasTipoMovimentacao tipo = novoTipo(EnumTipoConta.APLICACAO);
        when(tipoMovimentacaoRepository.findById(40L)).thenReturn(Optional.of(tipo));

        service.lancar(novoRequest(null));

        ArgumentCaptor<br.com.apifinanca.model.BasMovimentacao> captor =
                ArgumentCaptor.forClass(br.com.apifinanca.model.BasMovimentacao.class);
        verify(movimentacaoRepository).save(captor.capture());
        assertEquals(new BigDecimal("125.50"), captor.getValue().getVlDebito());
        assertEquals(null, captor.getValue().getVlCredito());
    }

    @Test
    void deveRegistrarLancamentoTambemNaContaDestinoQuandoInformada() {
        BasBancoConta bancoContaDestino = new BasBancoConta();
        bancoContaDestino.setId(11L);
        when(bancoContaRepository.findById(11L)).thenReturn(Optional.of(bancoContaDestino));
        when(tipoMovimentacaoRepository.findById(40L))
                .thenReturn(Optional.of(novoTipo(EnumTipoConta.TRANSFERENCIA)));

        service.lancar(novoRequest(null, 11L));

        ArgumentCaptor<br.com.apifinanca.model.BasMovimentacao> captor =
                ArgumentCaptor.forClass(br.com.apifinanca.model.BasMovimentacao.class);
        verify(movimentacaoRepository, times(2)).save(captor.capture());
        var lancamentoOrigem = captor.getAllValues().get(0);
        var lancamentoDestino = captor.getAllValues().get(1);

        assertEquals(bancoConta, lancamentoOrigem.getBancoConta());
        assertEquals(bancoContaDestino, lancamentoDestino.getBancoConta());
        assertEquals(lancamentoOrigem.getNrPosicao(), lancamentoDestino.getNrPosicao());
        assertEquals(lancamentoOrigem.getDtMovimentacao(), lancamentoDestino.getDtMovimentacao());
        assertEquals(lancamentoOrigem.getNrDia(), lancamentoDestino.getNrDia());
        assertEquals(lancamentoOrigem.getVlDebito(), lancamentoDestino.getVlDebito());
        assertEquals(lancamentoOrigem.getVlCredito(), lancamentoDestino.getVlCredito());
        assertEquals(lancamentoOrigem.getDsObservacao(), lancamentoDestino.getDsObservacao());
        assertEquals(lancamentoOrigem.getCompetencia(), lancamentoDestino.getCompetencia());
        assertEquals(lancamentoOrigem.getTipoMovimentacao(), lancamentoDestino.getTipoMovimentacao());
    }

    @Test
    void deveListarSomenteMovimentacoesAtivasPorCompetenciaBancoEConta() {
        BasMovimentacao movimentacao = new BasMovimentacao();
        movimentacao.setId(50L);
        movimentacao.setIcSituacao(EnumSimNao.SIM);
        when(movimentacaoRepository.buscarPorCompetenciaEBancoConta(
                2026, 11, 2L, 3L, EnumSimNao.SIM
        )).thenReturn(List.of(movimentacao));

        List<BasMovimentacaoDto> resultado =
                service.listarPorCompetenciaEBancoConta(2026, 11, 2L, 3L);

        assertEquals(1, resultado.size());
        assertEquals(50L, resultado.get(0).id());
        verify(movimentacaoRepository).buscarPorCompetenciaEBancoConta(
                2026, 11, 2L, 3L, EnumSimNao.SIM
        );
    }

    @Test
    void deveBloquearAlteracaoIndividualDeMovimentacaoVinculada() {
        BasMovimentacao movimentacaoVinculada = new BasMovimentacao();
        movimentacaoVinculada.setVinculado(new BasMovimentacao());
        when(movimentacaoRepository.findById(50L)).thenReturn(Optional.of(movimentacaoVinculada));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> service.atualizarMovimentacao(50L, novoDto(50L))
        );

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void deveBloquearExclusaoIndividualDeMovimentacaoVinculada() {
        BasMovimentacao movimentacaoVinculada = new BasMovimentacao();
        movimentacaoVinculada.setVinculado(new BasMovimentacao());
        when(movimentacaoRepository.findById(50L)).thenReturn(Optional.of(movimentacaoVinculada));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> service.excluirMovimentacao(50L)
        );

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
    }

    @Test
    void deveRecalcularSaldoAoExcluirMovimentacaoSemVinculo() {
        BasMovimentacao movimentacao = new BasMovimentacao();
        movimentacao.setCompetencia(competencia);
        movimentacao.setBancoConta(bancoConta);
        when(movimentacaoRepository.findById(50L)).thenReturn(Optional.of(movimentacao));
        when(competenciaRepository.findByNrAnoAndNrMes(2026, 10)).thenReturn(List.of());
        when(movimentacaoRepository.buscarAtivasPorCompetenciaEConta(20L, 10L, EnumSimNao.SIM))
                .thenReturn(List.of());
        when(movimentacaoFinalRepository.findByCompetencia_IdAndBancoConta_Id(20L, 10L))
                .thenReturn(Optional.empty());

        service.excluirMovimentacao(50L);

        verify(movimentacaoRepository).delete(movimentacao);
        verify(movimentacaoRepository).saveAll(List.of());
        verify(movimentacaoFinalRepository).save(any(BasMovimentacaoFinal.class));
    }

    @Test
    void deveCalcularSaldosEAtualizarMovimentacaoFinalAoLancar() {
        BasCompetencia competenciaAnterior = new BasCompetencia();
        competenciaAnterior.setId(19L);
        competenciaAnterior.setNrAno(2026);
        competenciaAnterior.setNrMes(10);
        BasMovimentacaoFinal movimentacaoFinalAnterior = new BasMovimentacaoFinal();
        movimentacaoFinalAnterior.setVlSaldoFinal(new BigDecimal("1000.00"));
        when(competenciaRepository.findByNrAnoAndNrMes(2026, 10))
                .thenReturn(List.of(competenciaAnterior));
        when(movimentacaoFinalRepository.findByCompetencia_IdAndBancoConta_Id(19L, 10L))
                .thenReturn(Optional.of(movimentacaoFinalAnterior));

        BasMovimentacao primeiro = new BasMovimentacao();
        primeiro.setVlCredito(new BigDecimal("200.00"));
        primeiro.setVlDebito(new BigDecimal("50.00"));
        BasMovimentacao segundo = new BasMovimentacao();
        segundo.setVlCredito(new BigDecimal("25.00"));
        segundo.setVlDebito(new BigDecimal("10.00"));
        when(movimentacaoRepository.buscarAtivasPorCompetenciaEConta(20L, 10L, EnumSimNao.SIM))
                .thenReturn(List.of(primeiro, segundo));
        when(tipoMovimentacaoRepository.findById(40L))
                .thenReturn(Optional.of(novoTipo(EnumTipoConta.DEBITO)));

        service.lancar(novoRequest(null));

        assertEquals(new BigDecimal("1150.00"), primeiro.getVlSaldo());
        assertEquals(new BigDecimal("1165.00"), segundo.getVlSaldo());
        ArgumentCaptor<BasMovimentacaoFinal> captor =
                ArgumentCaptor.forClass(BasMovimentacaoFinal.class);
        verify(movimentacaoFinalRepository).save(captor.capture());
        BasMovimentacaoFinal salvo = captor.getValue();
        assertEquals(new BigDecimal("1000.00"), salvo.getVlSaldoInicial());
        assertEquals(new BigDecimal("225.00"), salvo.getVlTotalCredito());
        assertEquals(new BigDecimal("60.00"), salvo.getVlTotalDebito());
        assertEquals(new BigDecimal("1165.00"), salvo.getVlSaldoFinal());
    }

    private BasTipoMovimentacao novoTipo(EnumTipoConta tipo) {
        BasTipoMovimentacao movimentacao = new BasTipoMovimentacao();
        movimentacao.setId(40L);
        movimentacao.setIcTipoMovimentacao(tipo);
        return movimentacao;
    }

    private BasMovimentacaoDto novoDto(Long id) {
        return new BasMovimentacaoDto(
                id, 1, null, 4, null, null, null, "Descricao",
                EnumSimNao.SIM, EnumSimNao.SIM, 10L, 20L, 40L, null, null
        );
    }

    private BasMovimentacaoLancamentoRequest novoRequest(Long investimentoId) {
        return novoRequest(investimentoId, null);
    }

    private BasMovimentacaoLancamentoRequest novoRequest(Long investimentoId, Long transferenciaId) {
        return new BasMovimentacaoLancamentoRequest(
                10L,
                40L,
                3,
                LocalDate.of(2026, 11, 4),
                new BigDecimal("125.50"),
                "Lancamento de teste",
                investimentoId,
                transferenciaId
        );
    }
}
