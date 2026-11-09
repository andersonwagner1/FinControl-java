package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasInvestimentoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasInvestimento;
import br.com.apifinanca.repository.BasInvestimentoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BasInvestimentoService extends AbstractCrudService<BasInvestimento, Long, BasInvestimentoDto> {

    private final BasInvestimentoRepository repository;

    public BasInvestimentoService(BasInvestimentoRepository repository, EntityManager entityManager) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasInvestimentoDto::from);
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BasInvestimentoDto> listarAtivosPorBancoConta(Long bancoContaId) {
        return repository.findByBancoConta_IdAndIcSituacao(bancoContaId, EnumSimNao.SIM)
                .stream()
                .map(BasInvestimentoDto::from)
                .toList();
    }
}
