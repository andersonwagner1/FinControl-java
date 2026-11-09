package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasBancoContaDisponivelDto;
import br.com.apifinanca.service.BasBancoContaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasBancoContaController.class)
class BasBancoContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasBancoContaService service;

    @Test
    void deveListarBancosEContasDisponiveisNoPeriodo() throws Exception {
        when(service.listarDisponiveis(11, 2026)).thenReturn(List.of(
                new BasBancoContaDisponivelDto(1L, 2L, "Banco", 3L, "Conta")
        ));

        mockMvc.perform(get("/api/bancos-contas/disponiveis")
                        .param("mes", "11")
                        .param("ano", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor[0].bancoContaId").value(1))
                .andExpect(jsonPath("$.valor[0].nomeBanco").value("Banco"))
                .andExpect(jsonPath("$.valor[0].nomeConta").value("Conta"));

        verify(service).listarDisponiveis(11, 2026);
    }

    @Test
    void deveRejeitarMesInvalido() throws Exception {
        mockMvc.perform(get("/api/bancos-contas/disponiveis")
                        .param("mes", "13")
                        .param("ano", "2026"))
                .andExpect(status().isBadRequest());
    }
}
