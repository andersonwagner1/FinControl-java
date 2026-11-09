package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasInvestimentoDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasInvestimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/investimentos")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Investimentos")
public class BasInvestimentoController extends AbstractCrudController<BasInvestimentoDto> {

    private final BasInvestimentoService service;

    public BasInvestimentoController(BasInvestimentoService service) {
        super(service, "Investimentos");
        this.service = service;
    }

    @GetMapping("/banco-conta/{bancoContaId}/ativos")
    @Operation(summary = "Lista investimentos ativos de uma conta bancaria")
    public ResponseEntity<RetornoDto<List<BasInvestimentoDto>>> listarAtivosPorBancoConta(
            @PathVariable Long bancoContaId
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Investimentos ativos listados com sucesso",
                service.listarAtivosPorBancoConta(bancoContaId)
        ));
    }
}
