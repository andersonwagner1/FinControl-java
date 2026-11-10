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
}
