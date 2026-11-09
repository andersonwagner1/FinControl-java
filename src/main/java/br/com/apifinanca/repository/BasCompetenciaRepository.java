package br.com.apifinanca.repository;

import br.com.apifinanca.model.BasCompetencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasCompetenciaRepository extends JpaRepository<BasCompetencia, Long> {

    List<BasCompetencia> findByNrAno(Integer ano);

    List<BasCompetencia> findByNrAnoAndNrMes(Integer ano, Integer mes);
}
