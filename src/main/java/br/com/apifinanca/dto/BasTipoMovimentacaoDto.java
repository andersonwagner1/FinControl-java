package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.enumerador.EnumTipoConta;
import br.com.apifinanca.model.BasCategoriaMovimentacao;
import br.com.apifinanca.model.BasTipoMovimentacao;
import br.com.apifinanca.service.EntityReferences;
import jakarta.persistence.EntityManager;

public record BasTipoMovimentacaoDto(
        Long id,
        String dsTipoMovimentacao,
        String dsObservacao,
        Long categoriaId,
        EnumSimNao icSituacao,
        EnumTipoConta icTipoMovimentacao,
        String icRelatorio
) {

    public BasTipoMovimentacao toEntity(Long entityId, EntityManager entityManager) {
        BasTipoMovimentacao entity = new BasTipoMovimentacao();
        entity.setId(entityId);
        entity.setDsTipoMovimentacao(dsTipoMovimentacao);
        entity.setDsObservacao(dsObservacao);
        entity.setCategoria(EntityReferences.obter(entityManager, BasCategoriaMovimentacao.class, categoriaId));
        entity.setIcSituacao(icSituacao);
        entity.setIcTipoMovimentacao(icTipoMovimentacao);
        entity.setIcRelatorio(icRelatorio);
        return entity;
    }

    public static BasTipoMovimentacaoDto from(BasTipoMovimentacao entity) {
        return new BasTipoMovimentacaoDto(
                entity.getId(),
                entity.getDsTipoMovimentacao(),
                entity.getDsObservacao(),
                entity.getCategoria() == null ? null : entity.getCategoria().getId(),
                entity.getIcSituacao(),
                entity.getIcTipoMovimentacao(),
                entity.getIcRelatorio()
        );
    }
}
