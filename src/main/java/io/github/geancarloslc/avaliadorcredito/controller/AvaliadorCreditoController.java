package io.github.geancarloslc.avaliadorcredito.controller;

import io.github.geancarloslc.avaliadorcredito.domain.exception.ErroComunicacaoMicroservicesException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.HttpStatusNotFoundException;
import io.github.geancarloslc.avaliadorcredito.domain.model.SituacaoCliente;
import io.github.geancarloslc.avaliadorcredito.service.AvaliadorCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("avaliacoes-credito")
public class AvaliadorCreditoController {

    @Autowired
    private AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public String status(){
        return "ok";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity<Object> consultarSituacaoCliente(@RequestParam("cpf") String cpf) {
        try {
            SituacaoCliente situacaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);
            return ResponseEntity.ok(situacaoCliente);
        } catch (HttpStatusNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroservicesException ex) {
            return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
        }
    }
}
