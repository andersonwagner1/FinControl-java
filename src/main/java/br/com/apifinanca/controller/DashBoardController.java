package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasBancoContaDisponivelDto;
import br.com.apifinanca.dto.BasFluxoFinanceiroMensalDto;
import br.com.apifinanca.dto.BasTransferenciaDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasTransferenciaService;
import br.com.apifinanca.service.DashBoardService;
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
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "DashBoardController")
public class DashBoardController {

    private final DashBoardService service;

    public DashBoardController(DashBoardService service) {
        this.service = service;
    }

    

    @GetMapping("/tabela/{ano}")
    @Operation(summary = "Lista movimentações da tabela")
    public ResponseEntity<RetornoDto<List<BasFluxoFinanceiroMensalDto>>> listarTabelaMovimentacao(
            @PathVariable int ano
    ) {
         List<BasFluxoFinanceiroMensalDto> ret = service.listarTabelaMovimentacao(ano);
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Movimentações listadas com sucesso",
                ret
        ));
    }

   
}
