package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasCartaoCredito;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

public record BasLancamentoCartaoCreditoDto(
        Long id,
        @NotNull Date dtMovimentacao,
        @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal vlDebito,
        @Size(max = 255) String dsObservacao,
        EnumSimNao icSituacao,
        @NotNull @Positive Long bancoContaId,
        @NotNull @Positive Long competenciaId,
        @NotNull @Positive Long tipoMovimentacaoId,
        @Positive Integer nrParcelas
) {

    public BasLancamentoCartaoCreditoDto {
        nrParcelas = nrParcelas == null ? 1 : nrParcelas;
        icSituacao = icSituacao == null ? EnumSimNao.SIM : icSituacao;
    }

    public static BasLancamentoCartaoCreditoDto from(BasCartaoCredito entity) {
        return new BasLancamentoCartaoCreditoDto(
                entity.getId(),
                entity.getDtMovimentacao(),
                entity.getVlCompra(),
                entity.getDsObservacao(),
                entity.getIcSituacao(),
                entity.getBancoConta() == null ? null : entity.getBancoConta().getId(),
                entity.getCompetencia() == null ? null : entity.getCompetencia().getId(),
                entity.getTipoMovimentacao() == null ? null : entity.getTipoMovimentacao().getId(),
                entity.getNrParcelas()
        );
    }
}
