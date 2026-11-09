package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasInvestimento;
import br.com.apifinanca.service.EntityReferences;
import jakarta.persistence.EntityManager;

public record BasInvestimentoDto(
        Long id,
        Long bancoContaId,
        String dsInvestimento,
        EnumSimNao icSituacao
) {

    public BasInvestimento toEntity(Long entityId, EntityManager entityManager) {
        BasInvestimento entity = new BasInvestimento();
        entity.setId(entityId);
        entity.setBancoConta(EntityReferences.obter(entityManager, BasBancoConta.class, bancoContaId));
        entity.setDsInvestimento(dsInvestimento);
        entity.setIcSituacao(icSituacao);
        return entity;
    }

    public static BasInvestimentoDto from(BasInvestimento entity) {
        return new BasInvestimentoDto(
                entity.getId(),
                entity.getBancoConta() == null ? null : entity.getBancoConta().getId(),
                entity.getDsInvestimento(),
                entity.getIcSituacao()
        );
    }
}
