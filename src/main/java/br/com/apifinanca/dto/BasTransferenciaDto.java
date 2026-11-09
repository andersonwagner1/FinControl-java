package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasTransferencia;
import br.com.apifinanca.model.BasTransferenciaBancoContaId;
import br.com.apifinanca.service.EntityReferences;
import jakarta.persistence.EntityManager;

public record BasTransferenciaDto(
        Long bancoContaId,
        Long transferirParaId,
        EnumSimNao icSituacao
) {

    public BasTransferenciaBancoContaId toId(EntityManager entityManager) {
        BasTransferenciaBancoContaId id = new BasTransferenciaBancoContaId();
        id.setBancoConta(EntityReferences.obter(entityManager, BasBancoConta.class, bancoContaId));
        id.setTransferirPara(EntityReferences.obter(entityManager, BasBancoConta.class, transferirParaId));
        return id;
    }

    public BasTransferencia toEntity(EntityManager entityManager) {
        BasTransferencia entity = new BasTransferencia();
        entity.setTransferenciaBancoContaId(toId(entityManager));
        entity.setIcSituacao(icSituacao);
        return entity;
    }

    public static BasTransferenciaDto from(BasTransferencia entity) {
        return new BasTransferenciaDto(
                entity.getTransferenciaBancoContaId().getBancoConta().getId(),
                entity.getTransferenciaBancoContaId().getTransferirPara().getId(),
                entity.getIcSituacao()
        );
    }
}
