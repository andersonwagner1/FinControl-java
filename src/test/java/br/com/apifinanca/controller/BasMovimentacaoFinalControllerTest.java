package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasMovimentacaoFinalConsultaDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.service.BasMovimentacaoFinalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasMovimentacaoFinalController.class)
class BasMovimentacaoFinalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasMovimentacaoFinalService service;

    @Test
    void deveFiltrarMovimentacoesFinaisPorAno() throws Exception {
        when(service.listarPorAno(2026)).thenReturn(List.of());

        mockMvc.perform(get("/api/movimentacoes-finais/ano/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor").isArray());

        verify(service).listarPorAno(2026);
    }

    @Test
    void deveConsultarSaldoFinalPorCompetenciaBancoEConta() throws Exception {
        when(service.consultar(2026, 11, 2L, 3L)).thenReturn(
                new BasMovimentacaoFinalConsultaDto(
                        new BigDecimal("100.00"),
                        new BigDecimal("250.00"),
                        new BigDecimal("200.00"),
                        new BigDecimal("50.00"),
                        EnumSimNao.SIM
                )
        );

        mockMvc.perform(get("/api/movimentacoes-finais/consulta")
                        .param("ano", "2026")
                        .param("mes", "11")
                        .param("bancoId", "2")
                        .param("contaId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor.vlSaldoInicial").value(100.00))
                .andExpect(jsonPath("$.valor.vlSaldoFinal").value(250.00))
                .andExpect(jsonPath("$.valor.vlTotalCredito").value(200.00))
                .andExpect(jsonPath("$.valor.vlTotalDebito").value(50.00))
                .andExpect(jsonPath("$.valor.icFechado").value("SIM"));

        verify(service).consultar(2026, 11, 2L, 3L);
    }

    @Test
    void deveRejeitarMesInvalidoNaConsulta() throws Exception {
        mockMvc.perform(get("/api/movimentacoes-finais/consulta")
                        .param("ano", "2026")
                        .param("mes", "13")
                        .param("bancoId", "2")
                        .param("contaId", "3"))
                .andExpect(status().isBadRequest());
    }
}
