package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasBancoContaDto;
import br.com.apifinanca.dto.BasBancoContaDisponivelDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasBancoContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/bancos-contas")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Contas bancarias")
public class BasBancoContaController extends AbstractCrudController<BasBancoContaDto> {

    private final BasBancoContaService service;

    public BasBancoContaController(BasBancoContaService service) {
        super(service, "Contas bancarias");
        this.service = service;
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Lista bancos e contas disponíveis em uma competência")
    public ResponseEntity<RetornoDto<List<BasBancoContaDisponivelDto>>> listarDisponiveis(
            @RequestParam int mes,
            @RequestParam int ano
    ) {
        if (mes < 1 || mes > 12 || ano < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um mes entre 1 e 12 e um ano valido");
        }

        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Bancos e contas disponiveis listados com sucesso",
                service.listarDisponiveis(mes, ano)
        ));
    }
}
