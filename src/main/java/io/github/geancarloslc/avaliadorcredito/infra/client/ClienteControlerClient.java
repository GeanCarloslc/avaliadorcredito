package io.github.geancarloslc.avaliadorcredito.infra.client;

import io.github.geancarloslc.avaliadorcredito.infra.dto.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "msclientes", path = "/clientes") //Cliente HTTP
public interface ClienteControlerClient {

    @GetMapping(params = "cpf")
    ResponseEntity<ClienteDTO> buscarCliente(@RequestParam("cpf") String cpf);
}
