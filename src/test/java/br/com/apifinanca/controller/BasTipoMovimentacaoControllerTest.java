package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasTipoMovimentacaoAtivaDto;
import br.com.apifinanca.enumerador.EnumTipoConta;
import br.com.apifinanca.service.BasTipoMovimentacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasTipoMovimentacaoController.class)
class BasTipoMovimentacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasTipoMovimentacaoService service;

    @Test
    void deveListarTiposAtivosComCategoria() throws Exception {
        when(service.listarAtivos()).thenReturn(List.of(
                new BasTipoMovimentacaoAtivaDto(1L, "Salario", "Receitas", EnumTipoConta.CREDITO)
        ));

        mockMvc.perform(get("/api/tipos-movimentacao/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor[0].id").value(1))
                .andExpect(jsonPath("$.valor[0].dsTipoMovimentacao").value("Salario"))
                .andExpect(jsonPath("$.valor[0].dsCategoria").value("Receitas"))
                .andExpect(jsonPath("$.valor[0].icTipoMovimentacao").value("CREDITO"));

        verify(service).listarAtivos();
    }

    @Test
    void devePermitirPreflightCorsDoAngularLocal() throws Exception {
        mockMvc.perform(options("/api/tipos-movimentacao/ativos")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "http://localhost:4200"
                ));
    }
}
