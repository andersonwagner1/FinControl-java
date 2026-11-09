package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.enumerador.EnumTipo;
import br.com.apifinanca.model.BasCategoriaMovimentacao;
import jakarta.persistence.EntityManager;

public record BasCategoriaMovimentacaoDto(
        Long id,
        String dsCategoria,
        String dsObservacao,
        EnumSimNao icSituacao,
        EnumTipo icTipo
) {

    public BasCategoriaMovimentacao toEntity(Long entityId, EntityManager entityManager) {
        BasCategoriaMovimentacao entity = new BasCategoriaMovimentacao();
        entity.setId(entityId);
        entity.setDsCategoria(dsCategoria);
        entity.setDsObservacao(dsObservacao);
        entity.setIcSituacao(icSituacao);
        entity.setIcTipo(icTipo);
        return entity;
    }

    public static BasCategoriaMovimentacaoDto from(BasCategoriaMovimentacao entity) {
        return new BasCategoriaMovimentacaoDto(
                entity.getId(),
                entity.getDsCategoria(),
                entity.getDsObservacao(),
                entity.getIcSituacao(),
                entity.getIcTipo()
        );
    }
}
