package io.github.geancarloslc.avaliadorcredito.infra.dto;

import io.github.geancarloslc.avaliadorcredito.domain.model.CartaoAprovado;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DadosAvaliacaoDTO {
    private List<CartaoAprovado> cartaoAprovadosLista;
}
