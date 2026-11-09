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
@Table(name = "BAS_CONTA")
public class BasConta {
    public BasConta() {
    }
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idConta")
	@SequenceGenerator(name="idConta", sequenceName="seq_BasConta", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	Long id;

	@Column(name = "DS_CONTA")
	String dsConta;

	@Column(name = "DS_OBSERVACAO")
	String dsObservacao;

	@Column(name = "IC_SITUACAO")
	@Enumerated(EnumType.STRING)
	EnumSimNao icSituacao;
	
	

}
