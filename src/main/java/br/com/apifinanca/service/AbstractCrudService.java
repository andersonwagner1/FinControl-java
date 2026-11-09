package br.com.apifinanca.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractCrudService<E, ID, D> {

    private final JpaRepository<E, ID> repository;
    private final BiFunction<ID, D, E> entityMapper;
    private final Function<E, D> dtoMapper;

    protected AbstractCrudService(
            JpaRepository<E, ID> repository,
            BiFunction<ID, D, E> entityMapper,
            Function<E, D> dtoMapper
    ) {
        this.repository = repository;
        this.entityMapper = entityMapper;
        this.dtoMapper = dtoMapper;
    }

    @Transactional(readOnly = true)
    public List<D> listar() {
        return repository.findAll().stream().map(dtoMapper).toList();
    }

    @Transactional(readOnly = true)
    public D buscarPorId(ID id) {
        return dtoMapper.apply(encontrar(id));
    }

    @Transactional
    public D criar(D dto) {
        return dtoMapper.apply(repository.save(entityMapper.apply(null, dto)));
    }

    @Transactional
    public D atualizar(ID id, D dto) {
        encontrar(id);
        return dtoMapper.apply(repository.save(entityMapper.apply(id, dto)));
    }

    @Transactional
    public void excluir(ID id) {
        repository.delete(encontrar(id));
    }

    private E encontrar(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registro nao encontrado: " + id
                ));
    }
}
