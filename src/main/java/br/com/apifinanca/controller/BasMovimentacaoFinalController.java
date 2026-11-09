package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasMovimentacaoFinalConsultaDto;
import br.com.apifinanca.dto.BasMovimentacaoFinalDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasMovimentacaoFinalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes-finais")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Movimentacoes finais")
public class BasMovimentacaoFinalController extends AbstractCrudController<BasMovimentacaoFinalDto> {

    private final BasMovimentacaoFinalService service;

    public BasMovimentacaoFinalController(BasMovimentacaoFinalService service) {
        super(service, "Movimentacoes finais");
        this.service = service;
    }

    @GetMapping("/ano/{ano}")
    @Operation(summary = "Lista movimentacoes finais por ano")
    public ResponseEntity<RetornoDto<List<BasMovimentacaoFinalDto>>> listarPorAno(
            @PathVariable Integer ano
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Movimentacoes finais listadas com sucesso",
                service.listarPorAno(ano)
        ));
    }

    @GetMapping("/consulta")
    @Operation(summary = "Consulta saldo final por competencia, banco e conta")
    public ResponseEntity<RetornoDto<BasMovimentacaoFinalConsultaDto>> consultar(
            @RequestParam Integer ano,
            @RequestParam Integer mes,
            @RequestParam Long bancoId,
            @RequestParam Long contaId
    ) {
        if (ano < 1 || mes < 1 || mes > 12 || bancoId < 1 || contaId < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe ano, banco e conta positivos e mes entre 1 e 12"
            );
        }

        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Movimentacao final consultada com sucesso",
                service.consultar(ano, mes, bancoId, contaId)
        ));
    }
}
