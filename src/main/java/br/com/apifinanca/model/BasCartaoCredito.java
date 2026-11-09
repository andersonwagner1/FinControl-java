package br.com.apifinanca.model;

import java.math.BigDecimal;
import java.util.Date;

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
@Table(name = "BAS_CARTAO_CREDITO")
public class BasCartaoCredito {
    public BasCartaoCredito() {
    }

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "idCartaoCredito")
	@SequenceGenerator(name="idCartaoCredito", sequenceName="seq_BasCartaoCredito", initialValue = 1, allocationSize = 1)
	Long id;
	
	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;
	
	@ManyToOne
    @JoinColumn(name = "COMPETENCIA_ID")	
    BasCompetencia competencia;
	
	@ManyToOne
    @JoinColumn(name = "TIPO_MOVIMENTACAO_ID")	
    BasTipoMovimentacao tipoMovimentacao;
	

    @Column(name = "NR_DIA")
    String nrDia;

    @Column(name = "DT_MOVIMENTACAO")
    Date dtMovimentacao;

    @Column(name = "VL_COMPRA")
    BigDecimal vlCompra;

    @Column(name = "DS_OBSERVACAO")
    String dsObservacao;

    @Column(name = "NR_PARCELAS")
    Integer nrParcelas;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "IC_SITUACAO")
    EnumSimNao icSituacao = EnumSimNao.SIM;
    
    @ManyToOne
    @JoinColumn(name = "CARTAO_CREDITO_ID")
    BasCartaoCredito vinculo;

}
