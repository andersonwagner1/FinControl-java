package br.com.apifinanca.repository;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasMovimentacaoRepository extends JpaRepository<BasMovimentacao, Long> {

    @Query("""
            select movimentacao
            from BasMovimentacao movimentacao
            where movimentacao.competencia.id = :competenciaId
              and movimentacao.bancoConta.id = :bancoContaId
              and movimentacao.icSituacao = :icSituacao
            order by movimentacao.dtMovimentacao, movimentacao.nrPosicao,  movimentacao.id
            """)
    List<BasMovimentacao> buscarAtivasPorCompetenciaEConta(
            @Param("competenciaId") Long competenciaId,
            @Param("bancoContaId") Long bancoContaId,
            @Param("icSituacao") EnumSimNao icSituacao
    );

    @Query("""
            select movimentacao
            from BasMovimentacao movimentacao
            join movimentacao.competencia competencia
            join movimentacao.bancoConta bancoConta
            where competencia.nrAno = :ano
              and competencia.nrMes = :mes
              and bancoConta.banco.id = :bancoId
              and bancoConta.conta.id = :contaId
              and movimentacao.icSituacao = :icSituacao
              order by movimentacao.dtMovimentacao, movimentacao.nrPosicao,  movimentacao.id
            """)
    List<BasMovimentacao> buscarPorCompetenciaEBancoConta(
            @Param("ano") Integer ano,
            @Param("mes") Integer mes,
            @Param("bancoId") Long bancoId,
            @Param("contaId") Long contaId,
            @Param("icSituacao") EnumSimNao icSituacao
    );



    /**
     * Emitir relatorios para saber a movimentação final
     */
 @Query(value = """
        SELECT 
    NR_MES, 
	SUM(CASE WHEN IC_RELATORIO = 'RENDA' THEN VL_CREDITO ELSE 0 END) AS RENDA,
    SUM(CASE WHEN IC_RELATORIO = 'CREDITO' THEN VL_CREDITO ELSE 0 END) AS CREDITO,
	SUM(CASE WHEN IC_RELATORIO = 'CREDITO' OR IC_RELATORIO = 'RENDA' THEN VL_CREDITO ELSE 0 END) AS TOTAL_CREDITO,
	SUM(CASE WHEN IC_RELATORIO = 'DEBITO' THEN VL_DEBITO ELSE 0 END) AS dEBITO,
	SUM(CASE WHEN IC_RELATORIO = 'MENSAL' THEN VL_DEBITO ELSE 0 END) AS MENSAL,
	SUM(CASE WHEN IC_RELATORIO = 'MENSAL' OR IC_RELATORIO = 'DEBITO' THEN VL_DEBITO ELSE 0 END) AS TOTAL_DEBITO,
	SUM(CASE WHEN IC_RELATORIO = 'APLICACAO' THEN VL_CREDITO ELSE 0 END) AS APLICACAO,
	SUM(CASE WHEN IC_RELATORIO = 'RESGATE' THEN VL_DEBITO ELSE 0 END) AS RESGATE,
	SUM(CASE WHEN IC_RELATORIO = 'RENDIMENTO' THEN VL_CREDITO ELSE 0 END) AS RENDIMENTO,
	SUM(CASE WHEN IC_RELATORIO = 'RENDIMENTO_NEGATIVO' THEN VL_DEBITO ELSE 0 END) AS RENDIMENTO_NEGATIVO,
	SUM(CASE WHEN IC_RELATORIO = 'RENDIMENTO' THEN VL_CREDITO ELSE 0 END)  -
	SUM(CASE WHEN IC_RELATORIO = 'RENDIMENTO_NEGATIVO' THEN VL_DEBITO ELSE 0 END) AS TOTAL_RENDIMENTO,
	SUM(CASE WHEN IC_RELATORIO = 'CREDITO' OR IC_RELATORIO = 'RENDA' OR IC_RELATORIO = 'RESGATE' THEN VL_CREDITO ELSE 0 END) -
	SUM(CASE WHEN IC_RELATORIO = 'MENSAL' OR IC_RELATORIO = 'APLICACAO' OR IC_RELATORIO = 'DEBITO' THEN VL_CREDITO ELSE 0 END) as TOTAL
FROM VW_MOVIMENTACAO_ATIVO MA
WHERE NR_ANO = :ano
GROUP BY NR_MES
ORDER BY NR_MES
         """, nativeQuery = true)
public List<Object[]> relatorioDeMOvimentacaoMensalPorAno(Integer ano);




/**
 * Verifique se existe algum campo rendimento cadastrado no sistema
 * @return
 */
        @Query (value = """
                SELECT x FROM BasMovimentacao x
                WHERE x.bancoConta.id = :idBancoConta
                AND x.competencia.id = :idCompetencia
                AND x.tipoMovimentacao.id in(21,6)
                """)
    List<BasMovimentacao> buscarSeExisteRendimento(Long idBancoConta, Long idCompetencia);

}
