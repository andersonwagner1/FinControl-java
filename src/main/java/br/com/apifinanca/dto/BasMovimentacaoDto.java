package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasInvestimento;
import br.com.apifinanca.model.BasMovimentacao;
import br.com.apifinanca.model.BasTipoMovimentacao;
import br.com.apifinanca.service.EntityReferences;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.Date;

public record BasMovimentacaoDto(
        Long id,
        Integer nrPosicao,
        Date dtMovimentacao,
        Integer nrDia,
        BigDecimal vlCredito,
        BigDecimal vlDebito,
        BigDecimal vlSaldo,
        String dsObservacao,
        EnumSimNao icCalcular,
        EnumSimNao icSituacao,
        Long bancoContaId,
        Long competenciaId,
        Long tipoMovimentacaoId,
        Long vinculadoId,
        Long investimentoId
) {

    public BasMovimentacao toEntity(Long entityId, EntityManager entityManager) {
        BasMovimentacao entity = new BasMovimentacao();
        entity.setId(entityId);
        entity.setNrPosicao(nrPosicao);
        entity.setDtMovimentacao(dtMovimentacao);
        entity.setNrDia(nrDia);
        entity.setVlCredito(vlCredito);
        entity.setVlDebito(vlDebito);
        entity.setVlSaldo(vlSaldo);
        entity.setDsObservacao(dsObservacao);
        entity.setIcCalcular(icCalcular);
        entity.setIcSituacao(icSituacao);
        entity.setBancoConta(EntityReferences.obter(entityManager, BasBancoConta.class, bancoContaId));
        entity.setCompetencia(EntityReferences.obter(entityManager, BasCompetencia.class, competenciaId));
        entity.setTipoMovimentacao(EntityReferences.obter(entityManager, BasTipoMovimentacao.class, tipoMovimentacaoId));
        entity.setVinculado(EntityReferences.obter(entityManager, BasMovimentacao.class, vinculadoId));
        entity.setInvestimento(EntityReferences.obter(entityManager, BasInvestimento.class, investimentoId));
        return entity;
    }

    public static BasMovimentacaoDto from(BasMovimentacao entity) {
        return new BasMovimentacaoDto(
                entity.getId(),
                entity.getNrPosicao(),
                entity.getDtMovimentacao(),
                entity.getNrDia(),
                entity.getVlCredito(),
                entity.getVlDebito(),
                entity.getVlSaldo(),
                entity.getDsObservacao(),
                entity.getIcCalcular(),
                entity.getIcSituacao(),
                entity.getBancoConta() == null ? null : entity.getBancoConta().getId(),
                entity.getCompetencia() == null ? null : entity.getCompetencia().getId(),
                entity.getTipoMovimentacao() == null ? null : entity.getTipoMovimentacao().getId(),
                entity.getVinculado() == null ? null : entity.getVinculado().getId(),
                entity.getInvestimento() == null ? null : entity.getInvestimento().getId()
        );
    }
}
