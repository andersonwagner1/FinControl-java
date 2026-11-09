package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasContaDto;
import br.com.apifinanca.model.BasConta;
import br.com.apifinanca.repository.BasContaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class BasContaService extends AbstractCrudService<BasConta, Long, BasContaDto> {

    public BasContaService(BasContaRepository repository, EntityManager entityManager) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasContaDto::from);
    }
}
