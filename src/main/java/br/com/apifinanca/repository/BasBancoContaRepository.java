package br.com.apifinanca.repository;

import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.enumerador.EnumSimNao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasBancoContaRepository extends JpaRepository<BasBancoConta, Long> {

    @Query("""
            select bancoConta
from BasBancoConta bancoConta
join fetch bancoConta.banco banco
join fetch bancoConta.conta conta
left join fetch bancoConta.dtAbertura abertura
left join fetch bancoConta.dtFechamento fechamento
where bancoConta.icSituacao = :situacaoAtiva
  and banco.icSituacao = :situacaoAtiva
  and conta.icSituacao = :situacaoAtiva
  and (abertura.nrAno * 100 + abertura.nrMes) <= (:ano * 100 + :mes)
  and (fechamento.id is null or (fechamento.nrAno * 100 + fechamento.nrMes) >= (:ano * 100 + :mes))
            """)
    List<BasBancoConta> listarDisponiveisNoPeriodo(
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("situacaoAtiva") EnumSimNao situacaoAtiva
    );
}
