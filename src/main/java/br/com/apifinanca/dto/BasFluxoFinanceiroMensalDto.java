package br.com.apifinanca.dto;

import lombok.Builder;


@Builder 
public record BasFluxoFinanceiroMensalDto(
       String mes,
        Double saldoAnterior,
        Double credito,
        Double renda,
        Double totalCredito,
        Double mensal,
        Double debito,
        Double totalDebito,
        Double aplicacao,
        Double resgate,
        Double rendimento,
        Double rendimentoNegativo,
        Double totalRendimento,
        Double saldoFinal,
        Double saldoFinalMovimentacao
        
) {

}
