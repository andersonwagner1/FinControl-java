package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasBancoContaDisponivelDto;
import br.com.apifinanca.service.BasTransferenciaService;
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

@WebMvcTest(BasTransferenciaController.class)
class BasTransferenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasTransferenciaService service;

    @Test
    void deveListarDestinosDeTransferenciaDaContaDeOrigem() throws Exception {
        when(service.listarDestinosDisponiveis(1L)).thenReturn(List.of(
                new BasBancoContaDisponivelDto(2L, 3L, "Banco destino", 4L, "Conta destino")
        ));

        mockMvc.perform(get("/api/transferencias/disponiveis")
                        .header("Origin", "http://localhost:4200")
                        .param("bancoContaId", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor[0].bancoContaId").value(2))
                .andExpect(jsonPath("$.valor[0].nomeBanco").value("Banco destino"))
                .andExpect(jsonPath("$.valor[0].nomeConta").value("Conta destino"));

        verify(service).listarDestinosDisponiveis(1L);
    }
}
