package io.github.geancarloslc.avaliadorcredito.infra.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private Long id;
    private String cpf;
    private String nome;
    private Integer idade;
}
