package br.com.apifinanca.controller;

import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.AbstractCrudService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class AbstractCrudController<D> {

    private final AbstractCrudService<?, Long, D> service;
    private final String nomeRecurso;

    protected AbstractCrudController(
            AbstractCrudService<?, Long, D> service,
            String nomeRecurso
    ) {
        this.service = service;
        this.nomeRecurso = nomeRecurso;
    }

    @GetMapping
    @Operation(summary = "Lista registros")
    public ResponseEntity<RetornoDto<List<D>>> listar() {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                nomeRecurso + " listados com sucesso",
                service.listar()
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro pelo ID")
    public ResponseEntity<RetornoDto<D>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Registro encontrado com sucesso",
                service.buscarPorId(id)
        ));
    }

    @PostMapping
    @Operation(summary = "Cadastra um registro")
    public ResponseEntity<RetornoDto<D>> criar(@RequestBody D dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RetornoDto<>(
                true,
                nomeRecurso + " cadastrado com sucesso",
                service.criar(dto)
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um registro")
    public ResponseEntity<RetornoDto<D>> atualizar(
            @PathVariable Long id,
            @RequestBody D dto
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Registro atualizado com sucesso",
                service.atualizar(id, dto)
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um registro")
    public ResponseEntity<RetornoDto<Void>> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                nomeRecurso + " excluido com sucesso",
                null
        ));
    }
}
