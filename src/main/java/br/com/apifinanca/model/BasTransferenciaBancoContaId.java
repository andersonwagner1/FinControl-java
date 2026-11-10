package br.com.apifinanca.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class BasTransferenciaBancoContaId implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BasTransferenciaBancoContaId() {
	}

	@ManyToOne
    @JoinColumn(name = "BANCO_CONTA_ID")
	BasBancoConta bancoConta;
	
	@ManyToOne
    @JoinColumn(name = "TRANSFERIR_ID")
	BasBancoConta transferirPara;
	public BasBancoConta getBancoConta() {
		return bancoConta;
	}
	public void setBancoConta(BasBancoConta bancoConta) {
		this.bancoConta = bancoConta;
	}
	public BasBancoConta getTransferirPara() {
		return transferirPara;
	}
	public void setTransferirPara(BasBancoConta transferirPara) {
		this.transferirPara = transferirPara;
	}
	

    // Sobrescrever equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasTransferenciaBancoContaId that = (BasTransferenciaBancoContaId) o;
        return Objects.equals(idBancoConta(bancoConta), idBancoConta(that.bancoConta)) &&
               Objects.equals(idBancoConta(transferirPara), idBancoConta(that.transferirPara));
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBancoConta(bancoConta), idBancoConta(transferirPara));
    }

    private Long idBancoConta(BasBancoConta conta) {
        return conta == null ? null : conta.getId();
    }
	
	
	
}
