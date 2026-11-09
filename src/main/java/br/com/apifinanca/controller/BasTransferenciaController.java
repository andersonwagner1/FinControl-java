package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasBancoContaDisponivelDto;
import br.com.apifinanca.dto.BasTransferenciaDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasTransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transferencias")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Transferencias")
public class BasTransferenciaController {

    private final BasTransferenciaService service;

    public BasTransferenciaController(BasTransferenciaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista transferencias")
    public ResponseEntity<RetornoDto<List<BasTransferenciaDto>>> listar() {
        return ResponseEntity.ok(new RetornoDto<>(true, "Transferencias listadas com sucesso", service.listar()));
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Lista bancos e contas para os quais a conta de origem pode transferir")
    public ResponseEntity<RetornoDto<List<BasBancoContaDisponivelDto>>> listarDestinosDisponiveis(
            @RequestParam Long bancoContaId
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Destinos de transferencia listados com sucesso",
                service.listarDestinosDisponiveis(bancoContaId)
        ));
    }

    @GetMapping("/{bancoContaId}/{transferirParaId}")
    @Operation(summary = "Busca uma transferencia pela chave composta")
    public ResponseEntity<RetornoDto<BasTransferenciaDto>> buscar(
            @PathVariable Long bancoContaId,
            @PathVariable Long transferirParaId
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Transferencia encontrada com sucesso",
                service.buscar(bancoContaId, transferirParaId)
        ));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma transferencia")
    public ResponseEntity<RetornoDto<BasTransferenciaDto>> criar(@RequestBody BasTransferenciaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RetornoDto<>(
                true,
                "Transferencia cadastrada com sucesso",
                service.criar(dto)
        ));
    }

    @PutMapping("/{bancoContaId}/{transferirParaId}")
    @Operation(summary = "Atualiza uma transferencia")
    public ResponseEntity<RetornoDto<BasTransferenciaDto>> atualizar(
            @PathVariable Long bancoContaId,
            @PathVariable Long transferirParaId,
            @RequestBody BasTransferenciaDto dto
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Transferencia atualizada com sucesso",
                service.atualizar(bancoContaId, transferirParaId, dto)
        ));
    }

    @DeleteMapping("/{bancoContaId}/{transferirParaId}")
    @Operation(summary = "Exclui uma transferencia")
    public ResponseEntity<RetornoDto<Void>> excluir(
            @PathVariable Long bancoContaId,
            @PathVariable Long transferirParaId
    ) {
        service.excluir(bancoContaId, transferirParaId);
        return ResponseEntity.ok(new RetornoDto<>(true, "Transferencia excluida com sucesso", null));
    }
}
