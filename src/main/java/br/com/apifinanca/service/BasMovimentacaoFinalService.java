package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasMovimentacaoFinalConsultaDto;
import br.com.apifinanca.dto.BasMovimentacaoFinalDto;
import br.com.apifinanca.model.BasMovimentacaoFinal;
import br.com.apifinanca.repository.BasMovimentacaoFinalRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BasMovimentacaoFinalService extends AbstractCrudService<
        BasMovimentacaoFinal, Long, BasMovimentacaoFinalDto> {

    private final BasMovimentacaoFinalRepository repository;

    public BasMovimentacaoFinalService(
            BasMovimentacaoFinalRepository repository,
            EntityManager entityManager
    ) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasMovimentacaoFinalDto::from);
        this.repository = repository;
    }

    public List<BasMovimentacaoFinalDto> listarPorAno(Integer ano) {
        return repository.findByCompetenciaNrAno(ano).stream()
                .map(BasMovimentacaoFinalDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BasMovimentacaoFinalConsultaDto consultar(
            Integer ano,
            Integer mes,
            Long bancoId,
            Long contaId
    ) {
        List<BasMovimentacaoFinal> resultados = repository.buscarPorCompetenciaEBancoConta(
                ano,
                mes,
                bancoId,
                contaId
        );

        if (resultados.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Movimentacao final nao encontrada para a competencia e conta informadas"
            );
        }
        if (resultados.size() > 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ha mais de uma movimentacao final para a competencia e conta informadas"
            );
        }

        return BasMovimentacaoFinalConsultaDto.from(resultados.get(0));
    }
}
