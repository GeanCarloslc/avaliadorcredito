package io.github.geancarloslc.avaliadorcredito.infra.dto;

import io.github.geancarloslc.avaliadorcredito.domain.model.CartaoCliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartaoClienteDTO {
    private String nome;
    private String bandeira;
    private BigDecimal limiteLiberado;

    public static List<CartaoCliente> toModel(List<CartaoClienteDTO> cartaoClienteDTOLista) {
        return cartaoClienteDTOLista.stream().map(cartaoClienteDTO -> new CartaoCliente(
                cartaoClienteDTO.getNome(),
                cartaoClienteDTO.getBandeira(),
                cartaoClienteDTO.getLimiteLiberado()
        )).collect(Collectors.toList());
    }
}
