package br.com.apifinanca.dto;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.model.BasMovimentacaoFinal;
import br.com.apifinanca.service.EntityReferences;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;

public record BasMovimentacaoFinalDto(
        Long id,
        Long competenciaId,
        Integer nrAno,
        Long bancoContaId,
        BigDecimal vlSaldoInicial,
        BigDecimal vlSaldoFinal,
        BigDecimal vlTotalCredito,
        BigDecimal vlTotalDebito,
        BigDecimal vlEsperado,
        BigDecimal vlEsperadoCredito,
        BigDecimal vlEsperadoDebito,
        EnumSimNao icFechado
) {

    public BasMovimentacaoFinal toEntity(Long entityId, EntityManager entityManager) {
        BasMovimentacaoFinal entity = new BasMovimentacaoFinal();
        entity.setId(entityId);
        entity.setCompetencia(EntityReferences.obter(entityManager, BasCompetencia.class, competenciaId));
        entity.setBancoConta(EntityReferences.obter(entityManager, BasBancoConta.class, bancoContaId));
        entity.setVlSaldoInicial(vlSaldoInicial);
        entity.setVlSaldoFinal(vlSaldoFinal);
        entity.setVlTotalCredito(vlTotalCredito);
        entity.setVlTotalDebito(vlTotalDebito);
        entity.setVlEsperado(vlEsperado);
        entity.setVlEsperadoCredito(vlEsperadoCredito);
        entity.setVlEsperadoDebito(vlEsperadoDebito);
        entity.setIcFechado(icFechado);
        return entity;
    }

    public static BasMovimentacaoFinalDto from(BasMovimentacaoFinal entity) {
        return new BasMovimentacaoFinalDto(
                entity.getId(),
                entity.getCompetencia() == null ? null : entity.getCompetencia().getId(),
                entity.getCompetencia() == null ? null : entity.getCompetencia().getNrAno(),
                entity.getBancoConta() == null ? null : entity.getBancoConta().getId(),
                entity.getVlSaldoInicial(),
                entity.getVlSaldoFinal(),
                entity.getVlTotalCredito(),
                entity.getVlTotalDebito(),
                entity.getVlEsperado(),
                entity.getVlEsperadoCredito(),
                entity.getVlEsperadoDebito(),
                entity.getIcFechado()
        );
    }
}
