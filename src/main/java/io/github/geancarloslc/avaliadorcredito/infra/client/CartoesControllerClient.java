package io.github.geancarloslc.avaliadorcredito.infra.client;

import io.github.geancarloslc.avaliadorcredito.infra.dto.CartaoClienteDTO;
import io.github.geancarloslc.avaliadorcredito.infra.dto.CartaoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscartoes", path = "/cartoes")
public interface CartoesControllerClient {
    @GetMapping(params = "cpf")
    ResponseEntity<List<CartaoClienteDTO>> buscarCartoesClienteCpf(@RequestParam("cpf") String cpf);

    @GetMapping(params = "renda")
    ResponseEntity<List<CartaoDTO>> buscarCartoesRendaAteh(@RequestParam("renda") Long renda);
}
