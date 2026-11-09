package br.com.apifinanca.dto;

public record RetornoDto<T>(
        boolean sucesso,
        String mensagem,
        T valor
) {
}
