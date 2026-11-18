package br.com.apifinanca.repository;

import br.com.apifinanca.model.BasMovimentacaoFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BasMovimentacaoFinalRepository extends JpaRepository<BasMovimentacaoFinal, Long> {

    List<BasMovimentacaoFinal> findByCompetenciaNrAno(Integer ano);

    @Query("""
            select movimentacaoFinal
            from BasMovimentacaoFinal movimentacaoFinal
            join movimentacaoFinal.competencia competencia
            join movimentacaoFinal.bancoConta bancoConta
            where competencia.nrAno = :ano
              and bancoConta.banco.id = :bancoId
              and bancoConta.conta.id = :contaId
            order by competencia.nrMes
            """)
    List<BasMovimentacaoFinal> buscarPorAnoEBancoConta(
            @Param("ano") Integer ano,
            @Param("bancoId") Long bancoId,
            @Param("contaId") Long contaId
    );

    Optional<BasMovimentacaoFinal> findByCompetencia_IdAndBancoConta_Id(
            Long bancoContaIdcompetenciaId,
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


    /**
     * Usnado para realizar consulta de relatorio do valor inicial do ano, para pegar o valor inicial do ano, que é o valor final do mes de dezembro do ano anterior
     */
     @Query(value = """
                SELECT NR_MES, SUM(VL_SALDO_INICIAL) AS Saldo_INICIAL, SUM(VL_SALDO_FINAL) AS SALDO_FINAL 
                FROM VW_RELATORIO_FINAL
                WHERE NR_ANO = :ano
                GROUP BY NR_MES
                order by NR_MES
        """, nativeQuery = true)
    List<Object[]> buscarPorcompetenciaInicial(int ano);
}
