package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasLancamentoCartaoCreditoDto;
import br.com.apifinanca.dto.BasCartaoCreditoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCartaoCredito;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasTipoMovimentacao;
import br.com.apifinanca.repository.BasBancoContaRepository;
import br.com.apifinanca.repository.BasCompetenciaRepository;
import br.com.apifinanca.repository.BasLancamentoCartaoCreditoRepository;
import br.com.apifinanca.repository.BasTipoMovimentacaoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasLancamentoCartaoCreditoServiceTest {

    @Mock
    private BasLancamentoCartaoCreditoRepository lancamentoRepository;
    @Mock
    private BasBancoContaRepository bancoContaRepository;
    @Mock
    private BasCompetenciaRepository competenciaRepository;
    @Mock
    private BasTipoMovimentacaoRepository tipoMovimentacaoRepository;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BasLancamentoCartaoCreditoService service;

    @Test
    void deveSalvarUmaParcelaPorCompetenciaNoRepositoryDoCartao() {
        BasBancoConta bancoConta = new BasBancoConta();
        bancoConta.setId(10L);
        BasCompetencia competenciaAtual = novaCompetencia(20L);
        BasCompetencia competenciaSeguinte = novaCompetencia(21L);
        BasCompetencia ultimaCompetencia = novaCompetencia(22L);
        BasTipoMovimentacao tipoMovimentacao = new BasTipoMovimentacao();
        tipoMovimentacao.setId(40L);

        when(bancoContaRepository.findById(10L)).thenReturn(Optional.of(bancoConta));
        when(tipoMovimentacaoRepository.findById(40L)).thenReturn(Optional.of(tipoMovimentacao));
        when(competenciaRepository.findById(20L)).thenReturn(Optional.of(competenciaAtual));
        when(competenciaRepository.findById(21L)).thenReturn(Optional.of(competenciaSeguinte));
        when(competenciaRepository.findById(22L)).thenReturn(Optional.of(ultimaCompetencia));
        when(lancamentoRepository.save(any(BasCartaoCredito.class))).thenAnswer(invocation -> {
            BasCartaoCredito lancamento = invocation.getArgument(0);
            lancamento.setId(100L + lancamento.getCompetencia().getId() - 20L);
            return lancamento;
        });

        List<BasLancamentoCartaoCreditoDto> parcelas = service.lancar(new BasLancamentoCartaoCreditoDto(
                null,
                Date.valueOf("2026-11-04"),
                new BigDecimal("100.00"),
                "Compra",
                EnumSimNao.SIM,
                10L,
                20L,
                40L,
                3
        ));

        ArgumentCaptor<BasCartaoCredito> captor = ArgumentCaptor.forClass(BasCartaoCredito.class);
        verify(lancamentoRepository, times(3)).save(captor.capture());
        List<BasCartaoCredito> lancamentosSalvos = captor.getAllValues();

        assertEquals(List.of(20L, 21L, 22L), lancamentosSalvos.stream()
                .map(lancamento -> lancamento.getCompetencia().getId())
                .toList());
        assertEquals(List.of(
                new BigDecimal("33.33"),
                new BigDecimal("33.33"),
                new BigDecimal("33.34")
        ), lancamentosSalvos.stream().map(BasCartaoCredito::getVlCompra).toList());
        assertEquals(List.of("Compra [1 / 3]", "Compra [2 / 3]", "Compra [3 / 3]"),
                lancamentosSalvos.stream().map(BasCartaoCredito::getDsObservacao).toList());
        assertEquals(Date.valueOf("2026-11-04"), lancamentosSalvos.get(2).getDtMovimentacao());
        assertEquals("4", lancamentosSalvos.get(0).getNrDia());
        assertEquals(lancamentosSalvos.get(0), lancamentosSalvos.get(1).getVinculo());
        assertEquals(3, parcelas.size());
        assertEquals(new BigDecimal("33.34"), parcelas.get(2).vlDebito());
    }

    @Test
    void deveUsarUmaParcelaComoPadrao() {
        BasBancoConta bancoConta = new BasBancoConta();
        bancoConta.setId(10L);
        BasCompetencia competencia = novaCompetencia(20L);
        BasTipoMovimentacao tipoMovimentacao = new BasTipoMovimentacao();
        tipoMovimentacao.setId(40L);
        when(bancoContaRepository.findById(10L)).thenReturn(Optional.of(bancoConta));
        when(tipoMovimentacaoRepository.findById(40L)).thenReturn(Optional.of(tipoMovimentacao));
        when(competenciaRepository.findById(20L)).thenReturn(Optional.of(competencia));
        when(lancamentoRepository.save(any(BasCartaoCredito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<BasLancamentoCartaoCreditoDto> parcelas = service.lancar(new BasLancamentoCartaoCreditoDto(
                null,
                Date.valueOf("2026-11-04"),
                new BigDecimal("125.50"),
                "Compra",
                null,
                10L,
                20L,
                40L,
                null
        ));

        assertEquals(1, parcelas.size());
        assertEquals(1, parcelas.get(0).nrParcelas());
        assertEquals("Compra", parcelas.get(0).dsObservacao());
        assertEquals(EnumSimNao.SIM, parcelas.get(0).icSituacao());
    }

    @Test
    void deveConsultarLancamentosPorCompetenciaEContaBancaria() {
        BasBancoConta bancoConta = new BasBancoConta();
        bancoConta.setId(10L);
        BasCompetencia competencia = novaCompetencia(20L);
        BasCartaoCredito lancamento = new BasCartaoCredito();
        lancamento.setId(55L);
        lancamento.setBancoConta(bancoConta);
        lancamento.setCompetencia(competencia);
        lancamento.setDtMovimentacao(Date.valueOf("2026-11-04"));
        lancamento.setVlCompra(new BigDecimal("50.00"));
        lancamento.setDsObservacao("Compra [1 / 2]");
        lancamento.setNrParcelas(2);
        lancamento.setIcSituacao(EnumSimNao.SIM);
        when(lancamentoRepository.findByCompetencia_IdAndBancoConta_IdOrderByDtMovimentacaoAscIdAsc(
                20L,
                10L
        )).thenReturn(List.of(lancamento));

        List<BasCartaoCreditoDto> resultado = service.listarPorCompetenciaEBancoConta(20L, 10L);

        assertEquals(1, resultado.size());
        assertEquals(55L, resultado.get(0).id());
        assertEquals(20L, resultado.get(0).competenciaId());
        assertEquals(10L, resultado.get(0).bancoContaId());
        assertEquals(new BigDecimal("50.00"), resultado.get(0).vlCompra());
        verify(lancamentoRepository)
                .findByCompetencia_IdAndBancoConta_IdOrderByDtMovimentacaoAscIdAsc(20L, 10L);
    }

    private BasCompetencia novaCompetencia(Long id) {
        BasCompetencia competencia = new BasCompetencia();
        competencia.setId(id);
        return competencia;
    }
}
