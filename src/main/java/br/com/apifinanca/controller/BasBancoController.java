package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasBancoDto;
import br.com.apifinanca.service.BasBancoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bancos")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Bancos")
public class BasBancoController extends AbstractCrudController<BasBancoDto> {

    public BasBancoController(BasBancoService service) {
        super(service, "Bancos");
    }
}
