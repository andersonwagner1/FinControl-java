package br.com.apifinanca.repository;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasBancoConta;
import br.com.apifinanca.model.BasTransferencia;
import br.com.apifinanca.model.BasTransferenciaBancoContaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasTransferenciaRepository
        extends JpaRepository<BasTransferencia, BasTransferenciaBancoContaId> {

    @Query("""
            select distinct transferirPara
            from BasTransferencia transferencia
            join transferencia.transferenciaBancoContaId.transferirPara transferirPara
            join fetch transferirPara.banco
            join fetch transferirPara.conta
            where transferencia.transferenciaBancoContaId.bancoConta.id = :bancoContaId
              and transferencia.icSituacao = :situacaoAtiva
            """)
    List<BasBancoConta> listarDestinosDisponiveis(
            @Param("bancoContaId") Long bancoContaId,
            @Param("situacaoAtiva") EnumSimNao situacaoAtiva
    );
}
