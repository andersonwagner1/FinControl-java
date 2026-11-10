package br.com.apifinanca.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BasMovimentacaoAtualizarDto(
        @NotNull Long bancoContaId,
        @NotNull Integer ano,
        @NotNull Integer mes,
        BigDecimal saldo
) {
}
