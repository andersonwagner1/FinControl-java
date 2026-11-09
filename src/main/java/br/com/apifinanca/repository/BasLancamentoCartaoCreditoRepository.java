package br.com.apifinanca.repository;

import br.com.apifinanca.model.BasCartaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasLancamentoCartaoCreditoRepository extends JpaRepository<BasCartaoCredito, Long> {

    List<BasCartaoCredito> findByCompetencia_IdAndBancoConta_IdOrderByDtMovimentacaoAscIdAsc(
            Long competenciaId,
            Long bancoContaId
    );
}
