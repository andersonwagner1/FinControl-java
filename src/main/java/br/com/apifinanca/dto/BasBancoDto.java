package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBanco;
import jakarta.persistence.EntityManager;

public record BasBancoDto(
        Long id,
        String dsBanco,
        String dsDescricao,
        EnumSimNao icSituacao,
        EnumSimNao icCartaoCredito
) {

    public BasBanco toEntity(Long entityId, EntityManager entityManager) {
        BasBanco entity = new BasBanco();
        entity.setId(entityId);
        entity.setDsBanco(dsBanco);
        entity.setDsDescricao(dsDescricao);
        entity.setIcSituacao(icSituacao);
        entity.setIcCartaoCredito(icCartaoCredito);
        return entity;
    }

    public static BasBancoDto from(BasBanco entity) {
        return new BasBancoDto(
                entity.getId(),
                entity.getDsBanco(),
                entity.getDsDescricao(),
                entity.getIcSituacao(),
                entity.getIcCartaoCredito()
        );
    }
}
