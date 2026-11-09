package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasBancoContaDto;
import br.com.apifinanca.dto.BasBancoContaDisponivelDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.repository.BasBancoContaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BasBancoContaService extends AbstractCrudService<BasBancoConta, Long, BasBancoContaDto> {

    private final BasBancoContaRepository repository;

    public BasBancoContaService(BasBancoContaRepository repository, EntityManager entityManager) {
        super(repository, (id, dto) -> dto.toEntity(id, entityManager), BasBancoContaDto::from);
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BasBancoContaDisponivelDto> listarDisponiveis(int mes, int ano) {
        List<BasBancoContaDisponivelDto> listar = repository.listarDisponiveisNoPeriodo(mes, ano, EnumSimNao.SIM)
                .stream()
                .map(BasBancoContaDisponivelDto::from)
                .toList();

            return listar;
    }
}
