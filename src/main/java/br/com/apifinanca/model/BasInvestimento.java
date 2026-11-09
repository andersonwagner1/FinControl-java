package br.com.apifinanca.model;

import br.com.apifinanca.enumerador.EnumSimNao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BAS_INVESTIMENTO")
public class BasInvestimento {
    public BasInvestimento() {
    }
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idInvestimento")
	@SequenceGenerator(name="idInvestimento", sequenceName="seq_BasInvestimento", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	Long id;
	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;

	@Column(name = "DS_INVESTIMENTO")
	String dsInvestimento;

	@Enumerated(EnumType.STRING)
	@Column(name = "IC_SITUACAO")
	EnumSimNao icSituacao;
	
}
