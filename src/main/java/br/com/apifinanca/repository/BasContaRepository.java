package br.com.apifinanca.repository;

import br.com.apifinanca.model.BasConta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasContaRepository extends JpaRepository<BasConta, Long> {
}
