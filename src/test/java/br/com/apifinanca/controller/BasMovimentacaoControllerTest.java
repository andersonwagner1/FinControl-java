package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasMovimentacaoDto;
import br.com.apifinanca.dto.BasMovimentacaoLancamentoRequest;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.service.BasMovimentacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasLancamentoMovimentacaoController.class)
class BasMovimentacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasMovimentacaoService service;

    @Test
    void deveListarMovimentacoesPorCompetenciaBancoEConta() throws Exception {
        when(service.listarPorCompetenciaEBancoConta(2026, 11, 2L, 3L)).thenReturn(List.of());

        mockMvc.perform(get("/api/movimentacoes/consulta")
                        .param("ano", "2026")
                        .param("mes", "11")
                        .param("bancoId", "2")
                        .param("contaId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor").isArray());

        verify(service).listarPorCompetenciaEBancoConta(2026, 11, 2L, 3L);
    }

    @Test
    void deveRejeitarMesInvalidoNaConsultaDeMovimentacoes() throws Exception {
        mockMvc.perform(get("/api/movimentacoes/consulta")
                        .param("ano", "2026")
                        .param("mes", "13")
                        .param("bancoId", "2")
                        .param("contaId", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveReceberLancamentoEEncaminharAoService() throws Exception {
        when(service.lancar(any(BasMovimentacaoLancamentoRequest.class)))
                .thenReturn(new BasMovimentacaoDto(
                        1L, 2, null, 4, new BigDecimal("125.50"), null, null,
                        "Compra", EnumSimNao.SIM, EnumSimNao.SIM, 10L, 20L, 30L, null, null
                ));

        mockMvc.perform(post("/api/movimentacoes/lancamento")
                        .header("Origin", "http://localhost:4200")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "bancoContaId": 10,
                                  "tipoMovimentacaoId": 30,
                                  "nrPosicao": 2,
                                  "dtMovimentacao": "2026-11-04",
                                  "valor": 125.50,
                                  "observacao": "Compra"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor.vlCredito").value(125.50));

        verify(service).lancar(any(BasMovimentacaoLancamentoRequest.class));
    }

    @Test
    void deveAtualizarMovimentacaoEEncaminharAoService() throws Exception {
        BasMovimentacaoDto movimentacao = new BasMovimentacaoDto(
                33L, 1, null, 5, new BigDecimal("125.50"), BigDecimal.ZERO, new BigDecimal("1125.50"),
                "Descrição alterada", EnumSimNao.SIM, EnumSimNao.SIM, 7L, 18L, 12L, null, null
        );
        when(service.atualizarMovimentacao(eq(33L), any(BasMovimentacaoDto.class))).thenReturn(movimentacao);

        mockMvc.perform(put("/api/movimentacoes/33")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 33,
                                  "nrPosicao": 1,
                                  "dtMovimentacao": "2026-11-05T00:00:00.000Z",
                                  "nrDia": 5,
                                  "vlCredito": 125.50,
                                  "vlDebito": 0,
                                  "vlSaldo": 1125.50,
                                  "dsObservacao": "Descrição alterada",
                                  "icCalcular": "SIM",
                                  "icSituacao": "SIM",
                                  "bancoContaId": 7,
                                  "competenciaId": 18,
                                  "tipoMovimentacaoId": 12,
                                  "vinculadoId": null,
                                  "investimentoId": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor.id").value(33));

        verify(service).atualizarMovimentacao(eq(33L), any(BasMovimentacaoDto.class));
    }

    @Test
    void deveExcluirMovimentacaoEEncaminharAoService() throws Exception {
        mockMvc.perform(delete("/api/movimentacoes/33"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true));

        verify(service).excluirMovimentacao(33L);
    }
}
