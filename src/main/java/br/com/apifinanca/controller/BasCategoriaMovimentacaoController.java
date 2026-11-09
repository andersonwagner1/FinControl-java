package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasCategoriaMovimentacaoDto;
import br.com.apifinanca.service.BasCategoriaMovimentacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias-movimentacao")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"})
@Tag(name = "Categorias de movimentacao")
public class BasCategoriaMovimentacaoController extends AbstractCrudController<BasCategoriaMovimentacaoDto> {

    public BasCategoriaMovimentacaoController(BasCategoriaMovimentacaoService service) {
        super(service, "Categorias de movimentacao");
    }
}
