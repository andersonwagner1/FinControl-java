package br.com.apifinanca.controller;

import br.com.apifinanca.service.BasCompetenciaService;
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

@WebMvcTest(BasCompetenciaController.class)
class BasCompetenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BasCompetenciaService service;

    @Test
    void deveFiltrarCompetenciasPorAno() throws Exception {
        when(service.listarPorAno(2026)).thenReturn(List.of());

        mockMvc.perform(get("/api/competencias/ano/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true))
                .andExpect(jsonPath("$.valor").isArray());

        verify(service).listarPorAno(2026);
    }
}
