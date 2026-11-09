package br.com.apifinanca.dto;

import br.com.apifinanca.model.BasCompetencia;
import jakarta.persistence.EntityManager;

public record BasCompetenciaDto(Long id, Integer nrAno, Integer nrMes, String dsMesAno) {

    public BasCompetencia toEntity(Long entityId, EntityManager entityManager) {
        BasCompetencia entity = new BasCompetencia();
        entity.setId(entityId);
        entity.setNrAno(nrAno);
        entity.setNrMes(nrMes);
        entity.setDsMesAno(dsMesAno);
        return entity;
    }

    public static BasCompetenciaDto from(BasCompetencia entity) {
        return new BasCompetenciaDto(
                entity.getId(),
                entity.getNrAno(),
                entity.getNrMes(),
                entity.getDsMesAno()
        );
    }
}
