package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;

import java.math.BigDecimal;

public record BasMovimentacaoFinalConsultaDto(
        BigDecimal vlSaldoInicial,
        BigDecimal vlSaldoFinal,
        BigDecimal vlTotalCredito,
        BigDecimal vlTotalDebito,
        EnumSimNao icFechado
) {
    public static BasMovimentacaoFinalConsultaDto from(
            br.com.apifinanca.model.BasMovimentacaoFinal movimentacaoFinal
    ) {
        return new BasMovimentacaoFinalConsultaDto(
                movimentacaoFinal.getVlSaldoInicial(),
                movimentacaoFinal.getVlSaldoFinal(),
                movimentacaoFinal.getVlTotalCredito(),
                movimentacaoFinal.getVlTotalDebito(),
                movimentacaoFinal.getIcFechado()
        );
    }
}
