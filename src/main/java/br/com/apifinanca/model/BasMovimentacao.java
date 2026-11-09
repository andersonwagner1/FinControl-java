package br.com.apifinanca.model;

import java.math.BigDecimal;
import java.util.Date;

import br.com.apifinanca.enumerador.EnumSimNao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "BAS_MOVIMENTACAO")
public class BasMovimentacao {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idMovimentacao")
	@SequenceGenerator(name="idMovimentacao", sequenceName="seq_BasMovimentacao", initialValue = 1, allocationSize = 1)
	@Column(name = "ID")
	Long id;


	@Column(name = "NR_POSICAO")
	Integer nrPosicao;

	@Column(name = "DT_MOVIMENTACAO")
	Date dtMovimentacao;

	@Column(name = "NR_DIA")
	Integer nrDia;

	@Column(name = "VL_CREDITO")
	BigDecimal vlCredito;

	@Column(name = "VL_DEBITO")
	BigDecimal vlDebito;

	@Column(name = "VL_SALDO")
	BigDecimal vlSaldo;

	@Column(name = "DS_OBSERVACAO")
	String dsObservacao;


	@Enumerated (EnumType.STRING)
	@Column(name = "IC_CALCULAR")
	EnumSimNao icCalcular = EnumSimNao.SIM;


	@Enumerated (EnumType.STRING)
	@Column(name = "IC_SITUACAO")
	EnumSimNao icSituacao = EnumSimNao.SIM;

	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;
	
	@ManyToOne
    @JoinColumn(name = "COMPETENCIA_ID")
	BasCompetencia competencia;
	
	@ManyToOne
    @JoinColumn(name = "TIPO_MOVIMENTACAO_ID")
	BasTipoMovimentacao tipoMovimentacao;
	
	
    @JoinColumn(name = "VINCULADO_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	BasMovimentacao vinculado;
	
    @JoinColumn(name = "INVESTIMENTO_ID")
   	@ManyToOne(fetch = FetchType.LAZY)
   	BasInvestimento investimento;
    
	public BasMovimentacao() {
		super();		
	}

}
