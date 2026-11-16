package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasTipoMovimentacaoAtivaDto;
import br.com.apifinanca.dto.BasTipoMovimentacaoDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasTipoMovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-movimentacao")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Tipos de movimentacao")
public class BasTipoMovimentacaoController extends AbstractCrudController<BasTipoMovimentacaoDto> {

    private final BasTipoMovimentacaoService service;

    public BasTipoMovimentacaoController(BasTipoMovimentacaoService service) {
        super(service, "Tipos de movimentacao");
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastra um tipo de movimentacao")
    @Override
    public ResponseEntity<RetornoDto<BasTipoMovimentacaoDto>> criar(
            @RequestBody BasTipoMovimentacaoDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RetornoDto<>(
                true,
                "Tipo de movimentacao cadastrado com sucesso",
                service.criar(dto)
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de movimentacao")
    @Override
    public ResponseEntity<RetornoDto<BasTipoMovimentacaoDto>> atualizar(
            @PathVariable Long id,
            @RequestBody BasTipoMovimentacaoDto dto
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Tipo de movimentacao atualizado com sucesso",
                service.atualizar(id, dto)
        ));
    }

    @PostMapping("/salvar")
    @Operation(summary = "Cadastra ou atualiza um tipo de movimentacao")
    public ResponseEntity<RetornoDto<BasTipoMovimentacaoDto>> salvar(
            @RequestBody BasTipoMovimentacaoDto dto
    ) {
        boolean atualizacao = dto.id() != null;
        BasTipoMovimentacaoDto resultado = atualizacao
                ? service.atualizar(dto.id(), dto)
                : service.criar(dto);

        return ResponseEntity.status(atualizacao ? HttpStatus.OK : HttpStatus.CREATED).body(new RetornoDto<>(
                true,
                atualizacao
                        ? "Tipo de movimentacao atualizado com sucesso"
                        : "Tipo de movimentacao cadastrado com sucesso",
                resultado
        ));
    }

    @GetMapping("/ativos")
    @Operation(summary = "Lista tipos de movimentacao ativos")
    public ResponseEntity<RetornoDto<List<BasTipoMovimentacaoAtivaDto>>> listarAtivos() {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Tipos de movimentacao ativos listados com sucesso",
                service.listarAtivos()
        ));
    }
}
