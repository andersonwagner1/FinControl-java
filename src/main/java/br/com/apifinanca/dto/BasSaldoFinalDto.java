package br.com.apifinanca.dto;





public class BasSaldoFinalDto {

    Long bancoContaId;
  
    Integer mes;
    Integer ano;
    Double saldoFinal;
    public Long getBancoContaId() {
        return bancoContaId;
    }
    public void setBancoContaId(Long bancoContaId) {
        this.bancoContaId = bancoContaId;
    }
    public Integer getMes() {
        return mes;
    }
    public void setMes(Integer mes) {
        this.mes = mes;
    }
    public Integer getAno() {
        return ano;
    }
    public void setAno(Integer ano) {
        this.ano = ano;
    }
    public Double getSaldoFinal() {
        return saldoFinal;
    }
    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    


}


    
