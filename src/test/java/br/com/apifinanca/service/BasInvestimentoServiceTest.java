package br.com.apifinanca.service;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.repository.BasInvestimentoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasInvestimentoServiceTest {

    @Mock
    private BasInvestimentoRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BasInvestimentoService service;

    @Test
    void deveConsultarInvestimentosAtivosDaContaBancaria() {
        when(repository.findByBancoConta_IdAndIcSituacao(10L, EnumSimNao.SIM)).thenReturn(List.of());

        assertTrue(service.listarAtivosPorBancoConta(10L).isEmpty());

        verify(repository).findByBancoConta_IdAndIcSituacao(10L, EnumSimNao.SIM);
    }
}
