package io.github.geancarloslc.avaliadorcredito.infra.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartaoDTO {
    private Long id;
    private String nome;
    private String bandeira;
    private BigDecimal renda;
    private BigDecimal limiteBasico;
}
