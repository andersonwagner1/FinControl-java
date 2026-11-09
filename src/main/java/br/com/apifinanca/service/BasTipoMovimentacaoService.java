package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasTipoMovimentacaoAtivaDto;
import br.com.apifinanca.dto.BasTipoMovimentacaoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasTipoMovimentacao;
import br.com.apifinanca.repository.BasTipoMovimentacaoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BasTipoMovimentacaoService extends AbstractCrudService<
        BasTipoMovimentacao, Long, BasTipoMovimentacaoDto> {

    private final BasTipoMovimentacaoRepository repository;

    public BasTipoMovimentacaoService(BasTipoMovimentacaoRepository repository, EntityManager entityManager) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasTipoMovimentacaoDto::from);
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BasTipoMovimentacaoAtivaDto> listarAtivos() {
        List<BasTipoMovimentacaoAtivaDto> listar = repository.listarPorSituacao(EnumSimNao.SIM);
        return listar;
    }
}
