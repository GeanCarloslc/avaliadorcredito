package io.github.geancarloslc.avaliadorcredito.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadosCliente {
    private Long id;
    private String nome;
    private Integer idade;
}
