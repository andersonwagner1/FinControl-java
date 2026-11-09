package br.com.apifinanca.model;

import java.math.BigDecimal;

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

@Getter
@Setter
@Entity
@Table(name = "BAS_MOVIMENTACAO_FINAL")
public class BasMovimentacaoFinal {
    public BasMovimentacaoFinal() {
    }
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idMovimentacaoFinal")
	@SequenceGenerator(name="idMovimentacaoFinal", sequenceName="seq_BasMovimentacaoFinal", initialValue = 1, allocationSize = 1)
	Long id;
	
	@ManyToOne
    @JoinColumn(name = "COMPETENCIA_ID")
	BasCompetencia competencia;
	
	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;
	

	@Column(name = "VL_SALDO_INICIAL")
	BigDecimal vlSaldoInicial;


	@Column(name = "VL_SALDO_FINAL")
	BigDecimal vlSaldoFinal;

	@Column(name = "VL_TOTAL_CREDITO")
	BigDecimal vlTotalCredito;

	@Column(name = "VL_TOTAL_DEBITO")
	BigDecimal vlTotalDebito;
	

	@Column(name = "VL_ESPERADO")
	BigDecimal vlEsperado;

	@Column(name = "VL_ESPERADO_CREDITO")
	BigDecimal vlEsperadoCredito;

	@Column(name = "VL_ESPERADO_DEBITO")
	BigDecimal vlEsperadoDebito;
	
	@Column(name = "IC_FECHADO")
	@Enumerated(EnumType.STRING)
	EnumSimNao icFechado = EnumSimNao.NAO;
	
	
}
