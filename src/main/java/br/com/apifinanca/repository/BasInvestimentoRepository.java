package br.com.apifinanca.repository;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.model.BasInvestimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasInvestimentoRepository extends JpaRepository<BasInvestimento, Long> {

    List<BasInvestimento> findByBancoConta_IdAndIcSituacao(Long bancoContaId, EnumSimNao icSituacao);
}
