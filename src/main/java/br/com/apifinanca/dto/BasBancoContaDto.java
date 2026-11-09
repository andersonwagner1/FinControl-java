package br.com.apifinanca.dto;

import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBanco;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasConta;
import br.com.apifinanca.service.EntityReferences;
import jakarta.persistence.EntityManager;

public record BasBancoContaDto(
        Long id,
        Long bancoId,
        Long contaId,
        EnumSimNao icSituacao,
        String icTipoConta,
        Long dtAberturaId,
        Long dtFechamentoId
) {

    public BasBancoConta toEntity(Long entityId, EntityManager entityManager) {
        BasBancoConta entity = new BasBancoConta();
        entity.setId(entityId);
        entity.setBanco(EntityReferences.obter(entityManager, BasBanco.class, bancoId));
        entity.setConta(EntityReferences.obter(entityManager, BasConta.class, contaId));
        entity.setIcSituacao(icSituacao);
        entity.setIcTipoConta(icTipoConta);
        entity.setDtAbertura(EntityReferences.obter(entityManager, BasCompetencia.class, dtAberturaId));
        entity.setDtFechamento(EntityReferences.obter(entityManager, BasCompetencia.class, dtFechamentoId));
        return entity;
    }

    public static BasBancoContaDto from(BasBancoConta entity) {
        return new BasBancoContaDto(
                entity.getId(),
                entity.getBanco() == null ? null : entity.getBanco().getId(),
                entity.getConta() == null ? null : entity.getConta().getId(),
                entity.getIcSituacao(),
                entity.getIcTipoConta(),
                entity.getDtAbertura() == null ? null : entity.getDtAbertura().getId(),
                entity.getDtFechamento() == null ? null : entity.getDtFechamento().getId()
        );
    }
}
