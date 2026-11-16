package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasCompetenciaDto;
import br.com.apifinanca.dto.RetornoDto;
import br.com.apifinanca.service.BasCompetenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/competencias")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Competencias")
public class BasCompetenciaController extends AbstractCrudController<BasCompetenciaDto> {

    private final BasCompetenciaService service;

    public BasCompetenciaController(BasCompetenciaService service) {
        super(service, "Competencias");
        this.service = service;
    }

    @GetMapping("/ano/{ano}")
    @Operation(summary = "Lista competencias por ano")
    public ResponseEntity<RetornoDto<List<BasCompetenciaDto>>> listarPorAno(
            @PathVariable Integer ano
    ) {
        return ResponseEntity.ok(new RetornoDto<>(
                true,
                "Competencias listadas com sucesso",
                service.listarPorAno(ano)
        ));
    }

    @PostMapping("/ano/{ano}")
    @Operation(summary = "Cadastra as competencias de um ano")
    public ResponseEntity<RetornoDto<List<BasCompetenciaDto>>> cadastrarPorAno(
            @PathVariable Integer ano
    ) {
        if (ano == null || ano < 1) {
            return ResponseEntity.badRequest().body(new RetornoDto<>(
                    false,
                    "Informe um ano valido",
                    null
            ));
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new RetornoDto<>(
                    true,
                    "Competencias cadastradas com sucesso",
                    service.cadastrarPorAno(ano)
            ));
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RetornoDto<>(
                    false,
                    "Falha ao cadastrar competencias",
                    null
            ));
        }
    }
}
