package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasBancoContaDisponivelDto;
import br.com.apifinanca.dto.BasTransferenciaDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasTransferencia;
import br.com.apifinanca.model.BasTransferenciaBancoContaId;
import br.com.apifinanca.repository.BasTransferenciaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BasTransferenciaService {

    private final BasTransferenciaRepository repository;
    private final EntityManager entityManager;

    public BasTransferenciaService(
            BasTransferenciaRepository repository,
            EntityManager entityManager
    ) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<BasTransferenciaDto> listar() {
        return repository.findAll().stream().map(BasTransferenciaDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<BasBancoContaDisponivelDto> listarDestinosDisponiveis(Long bancoContaId) {
        return repository.listarDestinosDisponiveis(bancoContaId, EnumSimNao.SIM)
                .stream()
                .map(BasBancoContaDisponivelDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BasTransferenciaDto buscar(Long bancoContaId, Long transferirParaId) {
        return BasTransferenciaDto.from(repository.findById(criarId(bancoContaId, transferirParaId))
                .orElseThrow(() -> naoEncontrada(bancoContaId, transferirParaId)));
    }

    @Transactional
    public BasTransferenciaDto criar(BasTransferenciaDto dto) {
        return BasTransferenciaDto.from(repository.save(dto.toEntity(entityManager)));
    }

    @Transactional
    public BasTransferenciaDto atualizar(
            Long bancoContaId,
            Long transferirParaId,
            BasTransferenciaDto dto
    ) {
        BasTransferenciaBancoContaId id = criarId(bancoContaId, transferirParaId);
        repository.findById(id).orElseThrow(() -> naoEncontrada(bancoContaId, transferirParaId));
        BasTransferencia entity = dto.toEntity(entityManager);
        entity.setTransferenciaBancoContaId(id);
        return BasTransferenciaDto.from(repository.save(entity));
    }

    @Transactional
    public void excluir(Long bancoContaId, Long transferirParaId) {
        repository.delete(repository.findById(criarId(bancoContaId, transferirParaId))
                .orElseThrow(() -> naoEncontrada(bancoContaId, transferirParaId)));
    }

    private BasTransferenciaBancoContaId criarId(Long bancoContaId, Long transferirParaId) {
        BasTransferenciaBancoContaId id = new BasTransferenciaBancoContaId();
        id.setBancoConta(EntityReferences.obter(entityManager, br.com.apifinanca.model.BasBancoConta.class, bancoContaId));
        id.setTransferirPara(EntityReferences.obter(entityManager, br.com.apifinanca.model.BasBancoConta.class, transferirParaId));
        return id;
    }

    private ResponseStatusException naoEncontrada(Long bancoContaId, Long transferirParaId) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Transferencia nao encontrada para as contas " + bancoContaId + " e " + transferirParaId
        );
    }
}
