package br.com.apifinanca.model;

import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.enumerador.EnumTipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BAS_CATEGORIA_MOVIMENTACAO")
public class BasCategoriaMovimentacao {
   
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idCategoriaMovimentacao")
	@SequenceGenerator(name="idCategoriaMovimentacao", sequenceName="seq_BasCategoriaMovimentacao", initialValue = 1, allocationSize = 1)
	Long id;
	
	@Column(name = "DS_CATEGORIA")
	String dsCategoria;
	@Column(name = "DS_OBSERVACAO")
	String dsObservacao;
	@Enumerated(EnumType.STRING)
	@Column(name = "IC_SITUACAO")
    EnumSimNao icSituacao = EnumSimNao.SIM;

	@Column(name = "IC_TIPO")
	@Enumerated(EnumType.STRING)
    EnumTipo icTipo;



}
