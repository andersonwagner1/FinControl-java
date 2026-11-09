package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasContaDto;
import br.com.apifinanca.service.BasContaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contas")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Contas")
public class BasContaController extends AbstractCrudController<BasContaDto> {

    public BasContaController(BasContaService service) {
        super(service, "Contas");
    }
}
