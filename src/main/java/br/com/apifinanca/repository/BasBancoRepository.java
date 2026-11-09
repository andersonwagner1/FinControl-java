package br.com.apifinanca.repository;

import br.com.apifinanca.model.BasBanco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasBancoRepository extends JpaRepository<BasBanco, Long> {
}
