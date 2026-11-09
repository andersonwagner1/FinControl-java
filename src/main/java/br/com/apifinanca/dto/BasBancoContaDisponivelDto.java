package br.com.apifinanca.dto;

import br.com.apifinanca.model.BasBancoConta;

public record BasBancoContaDisponivelDto(
        Long bancoContaId,
        Long bancoId,
        String nomeBanco,
        Long contaId,
        String nomeConta
) {

    public static BasBancoContaDisponivelDto from(BasBancoConta bancoConta) {
        return new BasBancoContaDisponivelDto(
                bancoConta.getId(),
                bancoConta.getBanco().getId(),
                bancoConta.getBanco().getDsBanco(),
                bancoConta.getConta().getId(),
                bancoConta.getConta().getDsConta()
        );
    }
}