package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasMovimentacaoLancamentoRequest;
import br.com.apifinanca.dto.BasMovimentacaoAtualizarDto;
import br.com.apifinanca.dto.BasMovimentacaoDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasMovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Movimentacoes")
public class BasLancamentoMovimentacaoController extends AbstractCrudController<BasMovimentacaoDto> {

    private final BasMovimentacaoService service;

    public BasLancamentoMovimentacaoController(BasMovimentacaoService service) {
        super(service, "Movimentacoes");
        this.service = service;
    }

    @GetMapping("/consulta")
    @Operation(summary = "Lista movimentacoes ativas por competencia, banco e conta")
    public ResponseEntity<RetornoDto<List<BasMovimentacaoDto>>> listarPorCompetenciaEBancoConta(
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
                "Movimentacoes listadas com sucesso",
                service.listarPorCompetenciaEBancoConta(ano, mes, bancoId, contaId)
        ));
    }

    @PostMapping("/lancamento")
    @Operation(summary = "Registra um lancamento financeiro")
    public ResponseEntity<RetornoDto<BasMovimentacaoDto>> lancar(
            @Valid @RequestBody BasMovimentacaoLancamentoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new RetornoDto<>(
                true,
                "Lancamento registrado com sucesso",
                service.lancar(request)
        ));
    }


    @PostMapping({"/atualizarSaldo", "/atualizacaoSaldo"})
    @Operation(summary = "Atualiza o saldo financeiro")
    public ResponseEntity<RetornoDto<BasMovimentacaoDto>> atualizarSaldo(
            @Valid @RequestBody BasMovimentacaoAtualizarDto request
    ) {

        service.atualizarSaldo(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RetornoDto<>(
                true,
                "Saldo Atualizado Com sucesso",null
                
        ));
    }


    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma movimentacao e recalcula os saldos")
    public ResponseEntity<RetornoDto<BasMovimentacaoDto>> atualizar(
            @PathVariable Long id,
            @RequestBody BasMovimentacaoDto dto
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Movimentacao atualizada com sucesso",
                service.atualizarMovimentacao(id, dto)
        ));
    }

    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma movimentacao e recalcula os saldos")
    public ResponseEntity<RetornoDto<Void>> excluir(@PathVariable Long id) {
        service.excluirMovimentacao(id);
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Movimentacao excluida com sucesso",
                null
        ));
    }
}
