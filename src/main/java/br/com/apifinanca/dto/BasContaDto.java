package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasConta;
import jakarta.persistence.EntityManager;

public record BasContaDto(
        Long id,
        String dsConta,
        String dsObservacao,
        EnumSimNao icSituacao
) {

    public BasConta toEntity(Long entityId, EntityManager entityManager) {
        BasConta entity = new BasConta();
        entity.setId(entityId);
        entity.setDsConta(dsConta);
        entity.setDsObservacao(dsObservacao);
        entity.setIcSituacao(icSituacao);
        return entity;
    }

    public static BasContaDto from(BasConta entity) {
        return new BasContaDto(
                entity.getId(),
                entity.getDsConta(),
                entity.getDsObservacao(),
                entity.getIcSituacao()
        );
    }
}
