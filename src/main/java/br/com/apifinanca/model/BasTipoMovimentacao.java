package br.com.apifinanca.model;

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
import br.com.apifinanca.enumerador.EnumSimNao;
import br.com.apifinanca.enumerador.EnumTipoConta;

@Getter
@Setter
@Entity
@Table(name = "BAS_TIPO_MOVIMENTACAO")
public class BasTipoMovimentacao {
    public BasTipoMovimentacao() {
    }
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idTipoMovimentacao")
	@SequenceGenerator(name="idTipoMovimentacao", sequenceName="seq_BasTipMovimentacao", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	Long id;

	@Column(name = "DS_TIPO_MOVIMENTACAO")
	String dsTipoMovimentacao;

	@Column(name = "DS_OBSERVACAO")
	String dsObservacao;
	
	@ManyToOne
    @JoinColumn(name = "CATEGORIA_ID")
	BasCategoriaMovimentacao categoria;
	
	@Column(name = "IC_SITUACAO")
	@Enumerated(EnumType.STRING)
	EnumSimNao icSituacao;
	
	
	@Column(name = "IC_TIPO_MOVIMENTACAO")
	@Enumerated(EnumType.STRING)
	EnumTipoConta icTipoMovimentacao;
	
	@Column(name = "IC_RELATORIO")
	String icRelatorio;
	
	
}
