package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasCartaoCreditoDto;
import br.com.apifinanca.dto.BasLancamentoCartaoCreditoDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasLancamentoCartaoCreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cartoes-credito")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Cartoes de credito")
public class BasLancamentoCartaoCreditoController extends AbstractCrudController<BasCartaoCreditoDto> {

    private final BasLancamentoCartaoCreditoService service;

    public BasLancamentoCartaoCreditoController(BasLancamentoCartaoCreditoService service) {
        super(service, "Cartoes de credito");
        this.service = service;
    }

    @GetMapping("/consulta")
    @Operation(summary = "Lista lancamentos do cartao por competencia e conta bancaria")
    public ResponseEntity<RetornoDto<List<BasCartaoCreditoDto>>> listarPorCompetenciaEBancoConta(
            @RequestParam Long competenciaId,
            @RequestParam Long bancoContaId
    ) {
        if (competenciaId < 1 || bancoContaId < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe competenciaId e bancoContaId positivos"
            );
        }
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Lancamentos do cartao listados com sucesso",
                service.listarPorCompetenciaEBancoConta(competenciaId, bancoContaId)
        ));
    }

    @PostMapping("/lancamento-cartao")
    @Operation(summary = "Registra uma compra no cartao e gera as parcelas")
    public ResponseEntity<RetornoDto<List<BasLancamentoCartaoCreditoDto>>> lancarCartaoCredito(
            @Valid @RequestBody BasLancamentoCartaoCreditoDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RetornoDto<>(
                true,
                "Lancamento no cartao de credito registrado com sucesso",
                service.lancar(request)
        ));
    }
}
