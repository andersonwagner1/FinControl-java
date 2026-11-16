package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasMovimentacaoFinal;

import java.math.BigDecimal;

public record BasMovimentacaoFinalFiltroDto(
        Integer nrMes,
        BigDecimal vlSaldoInicial,
        BigDecimal vlSaldoFinal,
        BigDecimal vlEsperado,
        EnumSimNao icFechado
) {

    public static BasMovimentacaoFinalFiltroDto from(BasMovimentacaoFinal entity) {
        return new BasMovimentacaoFinalFiltroDto(
                entity.getCompetencia() == null ? null : entity.getCompetencia().getNrMes(),
                entity.getVlSaldoInicial(),
                entity.getVlSaldoFinal(),
                entity.getVlEsperado(),
                entity.getIcFechado()
        );
    }
}
