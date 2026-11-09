package br.com.apifinanca.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "BAS_COMPETENCIA")
public class BasCompetencia {
    public BasCompetencia() {
    }
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "id_Competencia")
	@SequenceGenerator(name="id_Competencia", sequenceName="seq_Bas_Competencia", initialValue = 1, allocationSize = 1)
	
	Long id;
	@Column(name = "NR_ANO")
	Integer nrAno;
	@Column(name = "NR_MES")
	Integer nrMes;
	@Column(name = "DS_MES_ANO")
	String dsMesAno;
	
	public void proximoMes(int proximo) {
		nrMes++;
		if (nrMes > 12) {
			nrAno++;
			nrMes = 1;
		}
	}
	
	
	public void anteriorMes() {
		nrMes--;
		if (nrMes == 0) {
			nrAno--;
			nrMes = 12;
		}
	}

}
