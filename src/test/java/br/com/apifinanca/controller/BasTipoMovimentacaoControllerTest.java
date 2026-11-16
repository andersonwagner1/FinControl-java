package br.com.apifinanca.controller;

import br.com.apifinanca.dto.BasTipoMovimentacaoAtivaDto;
import br.com.apifinanca.dto.BasTipoMovimentacaoDto;
import br.com.apifinanca.enumerador.EnumSimNao;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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

    @Test
    void deveCadastrarTipoDeMovimentacaoViaPost() throws Exception {
        BasTipoMovimentacaoDto request = new BasTipoMovimentacaoDto(
                null, "Salario", "Receita mensal", 2L, EnumSimNao.SIM,
                EnumTipoConta.CREDITO, "S"
        );
        BasTipoMovimentacaoDto tipo = new BasTipoMovimentacaoDto(
                1L, "Salario", "Receita mensal", 2L, EnumSimNao.SIM,
                EnumTipoConta.CREDITO, "S"
        );
        when(service.criar(request)).thenReturn(tipo);

        mockMvc.perform(post("/api/tipos-movimentacao")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "dsTipoMovimentacao": "Salario",
                                  "dsObservacao": "Receita mensal",
                                  "categoriaId": 2,
                                  "icSituacao": "SIM",
                                  "icTipoMovimentacao": "CREDITO",
                                  "icRelatorio": "S"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor.dsTipoMovimentacao").value("Salario"));

        verify(service).criar(request);
    }

    @Test
    void deveAtualizarTipoDeMovimentacaoViaPut() throws Exception {
        BasTipoMovimentacaoDto request = new BasTipoMovimentacaoDto(
                null, "Salario atualizado", "Receita mensal", 2L, EnumSimNao.SIM,
                EnumTipoConta.CREDITO, "S"
        );
        BasTipoMovimentacaoDto tipo = new BasTipoMovimentacaoDto(
                1L, "Salario atualizado", "Receita mensal", 2L, EnumSimNao.SIM,
                EnumTipoConta.CREDITO, "S"
        );
        when(service.atualizar(1L, request)).thenReturn(tipo);

        mockMvc.perform(put("/api/tipos-movimentacao/1")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "dsTipoMovimentacao": "Salario atualizado",
                                  "dsObservacao": "Receita mensal",
                                  "categoriaId": 2,
                                  "icSituacao": "SIM",
                                  "icTipoMovimentacao": "CREDITO",
                                  "icRelatorio": "S"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor.dsTipoMovimentacao").value("Salario atualizado"));

        verify(service).atualizar(1L, request);
    }

    @Test
    void deveAtualizarTipoDeMovimentacaoViaPostSalvarQuandoIdForInformado() throws Exception {
        BasTipoMovimentacaoDto tipo = new BasTipoMovimentacaoDto(
                1L, "Salario atualizado", "Receita mensal", 2L, EnumSimNao.SIM,
                EnumTipoConta.CREDITO, "S"
        );
        when(service.atualizar(1L, tipo)).thenReturn(tipo);

        mockMvc.perform(post("/api/tipos-movimentacao/salvar")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "dsTipoMovimentacao": "Salario atualizado",
                                  "dsObservacao": "Receita mensal",
                                  "categoriaId": 2,
                                  "icSituacao": "SIM",
                                  "icTipoMovimentacao": "CREDITO",
                                  "icRelatorio": "S"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor.id").value(1));

        verify(service).atualizar(1L, tipo);
    }
}
