package br.com.apifinanca.enumerador;

public enum EnumTipo {
    C ("Credito"),
    D ("Debito"),
    A ("Aplicacao"),
    R ("Resgate"),
    TD("Trasferencia Debito"),
    TC("trasferencia Credito");
    
    private String tipo;

    EnumTipo(String tipo) {
        this.tipo = tipo;
    }
}
