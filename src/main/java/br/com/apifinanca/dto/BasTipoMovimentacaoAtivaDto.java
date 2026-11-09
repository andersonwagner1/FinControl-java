package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumTipoConta;

public record BasTipoMovimentacaoAtivaDto(
        Long id,
        String dsTipoMovimentacao,
        String dsCategoria,
        EnumTipoConta icTipoMovimentacao
) {
}
