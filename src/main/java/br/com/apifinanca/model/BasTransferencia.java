package br.com.apifinanca.model;

import br.com.apifinanca.enumerador.EnumSimNao;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BAS_TRANSFERENCIA")
public class BasTransferencia {
	
	@EmbeddedId
	BasTransferenciaBancoContaId transferenciaBancoContaId;
	
	@Column(name = "IC_SITUACAO")
	@Enumerated(EnumType.STRING)
	EnumSimNao icSituacao;

	
		
}
