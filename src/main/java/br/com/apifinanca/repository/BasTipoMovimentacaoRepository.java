package br.com.apifinanca.repository;

import br.com.apifinanca.dto.BasTipoMovimentacaoAtivaDto;
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasTipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasTipoMovimentacaoRepository extends JpaRepository<BasTipoMovimentacao, Long> {

    @Query("""
            select new br.com.apifinanca.dto.BasTipoMovimentacaoAtivaDto(
                tipo.id,
                tipo.dsTipoMovimentacao,
                categoria.dsCategoria,
                tipo.icTipoMovimentacao
            )
            from BasTipoMovimentacao tipo
            left join tipo.categoria categoria
            where tipo.icSituacao = :situacao
            """)
    List<BasTipoMovimentacaoAtivaDto> listarPorSituacao(
            @Param("situacao") EnumSimNao situacao
    );
}
