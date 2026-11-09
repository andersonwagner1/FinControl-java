package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasTipoMovimentacaoAtivaDto;
import br.com.apifinanca.dto.BasTipoMovimentacaoDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasTipoMovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
