package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasLancamentoCartaoCreditoDto;
import br.com.apifinanca.dto.BasCartaoCreditoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.service.BasLancamentoCartaoCreditoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasLancamentoCartaoCreditoController.class)
class BasLancamentoCartaoCreditoControllerTest {

    private static final String ORIGEM_FRONTEND = "http://localhost:4200";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasLancamentoCartaoCreditoService service;

    @Test
    void devePermitirConsultaDeCartoesPeloFrontendLocal() throws Exception {
        mockMvc.perform(get("/api/cartoes-credito")
                        .header("Origin", ORIGEM_FRONTEND))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGEM_FRONTEND));
    }

    @Test
    void deveReceberLancamentoParceladoEEncaminharAoService() throws Exception {
        when(service.lancar(any(BasLancamentoCartaoCreditoDto.class)))
                .thenReturn(List.of(new BasLancamentoCartaoCreditoDto(
                        1L,
                        Date.valueOf("2026-11-04"),
                        new BigDecimal("50.00"),
                        "Compra [1 / 2]",
                        EnumSimNao.SIM,
                        10L,
                        20L,
                        30L,
                        2
                )));

        mockMvc.perform(post("/api/cartoes-credito/lancamento-cartao")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "dtMovimentacao": "2026-11-04T00:00:00.000Z",
                                  "vlDebito": 100.00,
                                  "dsObservacao": "Compra",
                                  "icSituacao": "SIM",
                                  "bancoContaId": 10,
                                  "competenciaId": 20,
                                  "tipoMovimentacaoId": 30,
                                  "nrParcelas": 2
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor").isArray())
                .andExpect(jsonPath("$.valor[0].vlDebito").value(50.00))
                .andExpect(jsonPath("$.valor[0].nrParcelas").value(2));

        verify(service).lancar(any(BasLancamentoCartaoCreditoDto.class));
    }

    @Test
    void deveConsultarLancamentosPorCompetenciaEConta() throws Exception {
        when(service.listarPorCompetenciaEBancoConta(20L, 10L))
                .thenReturn(List.of(new BasCartaoCreditoDto(
                        55L,
                        10L,
                        20L,
                        30L,
                        "4",
                        Date.valueOf("2026-11-04"),
                        new BigDecimal("50.00"),
                        "Compra [1 / 2]",
                        2,
                        EnumSimNao.SIM,
                        null
                )));

        mockMvc.perform(get("/api/cartoes-credito/consulta")
                        .param("competenciaId", "20")
                        .param("bancoContaId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor").isArray())
                .andExpect(jsonPath("$.valor[0].id").value(55))
                .andExpect(jsonPath("$.valor[0].competenciaId").value(20))
                .andExpect(jsonPath("$.valor[0].bancoContaId").value(10))
                .andExpect(jsonPath("$.valor[0].vlCompra").value(50.00));

        verify(service).listarPorCompetenciaEBancoConta(20L, 10L);
    }

    @Test
    void deveRejeitarIdsInvalidosNaConsultaDeLancamentos() throws Exception {
        mockMvc.perform(get("/api/cartoes-credito/consulta")
                        .param("competenciaId", "0")
                        .param("bancoContaId", "10"))
                .andExpect(status().isBadRequest());
    }
}
