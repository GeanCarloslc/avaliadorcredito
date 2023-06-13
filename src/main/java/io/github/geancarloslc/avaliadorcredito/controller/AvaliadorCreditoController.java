package io.github.geancarloslc.avaliadorcredito.controller;

import io.github.geancarloslc.avaliadorcredito.domain.exception.ErroComunicacaoMicroservicesException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.ErroSolicitacaoCartaoException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.HttpStatusNotFoundException;
import io.github.geancarloslc.avaliadorcredito.domain.model.DadosAvaliacao;
import io.github.geancarloslc.avaliadorcredito.domain.model.SituacaoCliente;
import io.github.geancarloslc.avaliadorcredito.infra.dto.DadosAvaliacaoDTO;
import io.github.geancarloslc.avaliadorcredito.infra.dto.DadosSolicitacaoEmissaoCartaoDTO;
import io.github.geancarloslc.avaliadorcredito.infra.mqueue.model.ProtocoloSolicitacaoCartao;
import io.github.geancarloslc.avaliadorcredito.service.AvaliadorCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dadosAvaliacao) {
        try {
            DadosAvaliacaoDTO dadosAvaliacaoDTO = avaliadorCreditoService.realizarAvaliacao(dadosAvaliacao.getCpf(), dadosAvaliacao.getRenda());
            return ResponseEntity.ok(dadosAvaliacaoDTO);
        } catch (HttpStatusNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (ErroComunicacaoMicroservicesException ex) {
            return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
        }
    }

    @PostMapping("solicitacoes-cartao")
    public ResponseEntity solicitarCartao(@RequestBody DadosSolicitacaoEmissaoCartaoDTO dadosSolicitacaoEmissaoCartaoDTO) {
        try {
            ProtocoloSolicitacaoCartao protocoloSolicitacaoCartao
                    = avaliadorCreditoService.solicitarEmissaoCartao(dadosSolicitacaoEmissaoCartaoDTO);
            return ResponseEntity.ok(protocoloSolicitacaoCartao);
        } catch (ErroSolicitacaoCartaoException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }
}
