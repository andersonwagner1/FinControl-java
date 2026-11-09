package br.com.apifinanca.service;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.repository.BasTransferenciaRepository;
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
class BasTransferenciaServiceTest {

    @Mock
    private BasTransferenciaRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BasTransferenciaService service;

    @Test
    void deveConsultarSomenteDestinosDeTransferenciaAtivos() {
        when(repository.listarDestinosDisponiveis(10L, EnumSimNao.SIM)).thenReturn(List.of());

        assertTrue(service.listarDestinosDisponiveis(10L).isEmpty());

        verify(repository).listarDestinosDisponiveis(10L, EnumSimNao.SIM);
    }
}
