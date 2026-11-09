package br.com.apifinanca.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import br.com.apifinanca.enumerador.EnumSimNao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BAS_BANCO")
public class BasBanco {
    public BasBanco() {
    }

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idBanco")
	@SequenceGenerator(name="idBanco", sequenceName="seq_BasBanco", initialValue = 1, allocationSize = 1)
	Long id;
	@Column(name = "DS_BANCO")
	String dsBanco;
	@Column(name = "DS_DESCRICAO")
	String dsDescricao;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "IC_SITUACAO")
	EnumSimNao icSituacao;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "IC_CARTAO_CREDITO")
	EnumSimNao icCartaoCredito;
	
	

}
