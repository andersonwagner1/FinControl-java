package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasBancoDto;
import br.com.apifinanca.model.BasBanco;
import br.com.apifinanca.repository.BasBancoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class BasBancoService extends AbstractCrudService<BasBanco, Long, BasBancoDto> {

    public BasBancoService(BasBancoRepository repository, EntityManager entityManager) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasBancoDto::from);
    }
}
