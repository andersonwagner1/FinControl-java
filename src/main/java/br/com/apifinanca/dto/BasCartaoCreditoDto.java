package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCartaoCredito;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasTipoMovimentacao;
import br.com.apifinanca.service.EntityReferences;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.Date;

public record BasCartaoCreditoDto(
        Long id,
        Long bancoContaId,
        Long competenciaId,
        Long tipoMovimentacaoId,
        String nrDia,
        Date dtMovimentacao,
        BigDecimal vlCompra,
        String dsObservacao,
        Integer nrParcelas,
        EnumSimNao icSituacao,
        Long vinculoId
) {

    public BasCartaoCredito toEntity(Long entityId, EntityManager entityManager) {
        BasCartaoCredito entity = new BasCartaoCredito();
        entity.setId(entityId);
        entity.setBancoConta(EntityReferences.obter(entityManager, BasBancoConta.class, bancoContaId));
        entity.setCompetencia(EntityReferences.obter(entityManager, BasCompetencia.class, competenciaId));
        entity.setTipoMovimentacao(EntityReferences.obter(entityManager, BasTipoMovimentacao.class, tipoMovimentacaoId));
        entity.setNrDia(nrDia);
        entity.setDtMovimentacao(dtMovimentacao);
        entity.setVlCompra(vlCompra);
        entity.setDsObservacao(dsObservacao);
        entity.setNrParcelas(nrParcelas);
        entity.setIcSituacao(icSituacao);
        entity.setVinculo(EntityReferences.obter(entityManager, BasCartaoCredito.class, vinculoId));
        return entity;
    }

    public static BasCartaoCreditoDto from(BasCartaoCredito entity) {
        return new BasCartaoCreditoDto(
                entity.getId(),
                entity.getBancoConta() == null ? null : entity.getBancoConta().getId(),
                entity.getCompetencia() == null ? null : entity.getCompetencia().getId(),
                entity.getTipoMovimentacao() == null ? null : entity.getTipoMovimentacao().getId(),
                entity.getNrDia(),
                entity.getDtMovimentacao(),
                entity.getVlCompra(),
                entity.getDsObservacao(),
                entity.getNrParcelas(),
                entity.getIcSituacao(),
                entity.getVinculo() == null ? null : entity.getVinculo().getId()
        );
    }
}
