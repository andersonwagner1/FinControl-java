package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasInvestimentoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.service.BasInvestimentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasInvestimentoController.class)
class BasInvestimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasInvestimentoService service;

    @Test
    void deveListarInvestimentosAtivosDaContaBancaria() throws Exception {
        when(service.listarAtivosPorBancoConta(10L)).thenReturn(List.of(
                new BasInvestimentoDto(1L, 10L, "CDB", EnumSimNao.SIM)
        ));

        mockMvc.perform(get("/api/investimentos/banco-conta/10/ativos")
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor[0].id").value(1))
                .andExpect(jsonPath("$.valor[0].bancoContaId").value(10))
                .andExpect(jsonPath("$.valor[0].dsInvestimento").value("CDB"))
                .andExpect(jsonPath("$.valor[0].icSituacao").value("SIM"));

        verify(service).listarAtivosPorBancoConta(10L);
    }
}
