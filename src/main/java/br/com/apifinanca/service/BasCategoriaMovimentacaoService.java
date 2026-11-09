package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasCategoriaMovimentacaoDto;
import br.com.apifinanca.model.BasCategoriaMovimentacao;
import br.com.apifinanca.repository.BasCategoriaMovimentacaoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

@Service
public class BasCategoriaMovimentacaoService extends AbstractCrudService<
        BasCategoriaMovimentacao, Long, BasCategoriaMovimentacaoDto> {

    public BasCategoriaMovimentacaoService(
            BasCategoriaMovimentacaoRepository repository,
            EntityManager entityManager
    ) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasCategoriaMovimentacaoDto::from);
    }
}
