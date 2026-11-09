package br.com.apifinanca.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BasMovimentacaoLancamentoRequest(
        @NotNull Long bancoContaId,
        @NotNull Long tipoMovimentacaoId,
        @NotNull Integer nrPosicao,
        @NotNull LocalDate dtMovimentacao,
        @NotNull @DecimalMin(value = "0.00") BigDecimal valor,
        @Size(max = 255) String observacao,
        Long investimentoId,
        Long transferenciaId
) {
}
