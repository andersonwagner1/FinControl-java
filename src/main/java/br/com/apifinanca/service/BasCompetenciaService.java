package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasCompetenciaDto;
import br.com.apifinanca.model.BasCompetencia;
import br.com.apifinanca.repository.BasCompetenciaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BasCompetenciaService extends AbstractCrudService<BasCompetencia, Long, BasCompetenciaDto> {

    private final BasCompetenciaRepository repository;

    public BasCompetenciaService(BasCompetenciaRepository repository, EntityManager entityManager) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasCompetenciaDto::from);
        this.repository = repository;
    }

    public List<BasCompetenciaDto> listarPorAno(Integer ano) {
        return repository.findByNrAno(ano).stream()
                .map(BasCompetenciaDto::from)
                .toList();
    }
}
