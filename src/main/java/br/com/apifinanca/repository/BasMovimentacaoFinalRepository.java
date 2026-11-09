package br.com.apifinanca.repository;

import br.com.apifinanca.model.BasMovimentacaoFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BasMovimentacaoFinalRepository extends JpaRepository<BasMovimentacaoFinal, Long> {

    List<BasMovimentacaoFinal> findByCompetenciaNrAno(Integer ano);

    Optional<BasMovimentacaoFinal> findByCompetencia_IdAndBancoConta_Id(
            Long competenciaId,
            Long bancoContaId
    );

    @Query("""
            select movimentacaoFinal
            from BasMovimentacaoFinal movimentacaoFinal
            join movimentacaoFinal.competencia competencia
            join movimentacaoFinal.bancoConta bancoConta
            where competencia.nrAno = :ano
              and competencia.nrMes = :mes
              and bancoConta.banco.id = :bancoId
              and bancoConta.conta.id = :contaId
            """)
    List<BasMovimentacaoFinal> buscarPorCompetenciaEBancoConta(
            @Param("ano") Integer ano,
            @Param("mes") Integer mes,
            @Param("bancoId") Long bancoId,
            @Param("contaId") Long contaId
    );
}
