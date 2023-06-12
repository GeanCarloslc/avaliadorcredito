package io.github.geancarloslc.avaliadorcredito.service;

import feign.FeignException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.ErroComunicacaoMicroservicesException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.HttpStatusNotFoundException;
import io.github.geancarloslc.avaliadorcredito.domain.model.DadosCliente;
import io.github.geancarloslc.avaliadorcredito.domain.model.SituacaoCliente;
import io.github.geancarloslc.avaliadorcredito.infra.client.CartoesControllerClient;
import io.github.geancarloslc.avaliadorcredito.infra.client.ClienteControlerClient;
import io.github.geancarloslc.avaliadorcredito.infra.dto.CartaoClienteDTO;
import io.github.geancarloslc.avaliadorcredito.infra.dto.ClienteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AvaliadorCreditoService {

    @Autowired
    private ClienteControlerClient clienteControlerClient;

    @Autowired
    private CartoesControllerClient cartoesControllerClient;

    public SituacaoCliente obterSituacaoCliente(String cpf)
            throws HttpStatusNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<ClienteDTO> clienteDtoResponseEntity = clienteControlerClient.buscarCliente(cpf);

            DadosCliente dadosCliente = new DadosCliente(
                            clienteDtoResponseEntity.getBody().getId(),
                            clienteDtoResponseEntity.getBody().getNome());

            ResponseEntity<List<CartaoClienteDTO>> cartaoClienteDTOListaResponseEntity
                    = cartoesControllerClient.buscarCartoesClienteCpf(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosCliente)
                    .cartoes(CartaoClienteDTO.toModel(Objects.requireNonNull(cartaoClienteDTOListaResponseEntity.getBody())))
                    .build();

        } catch (FeignException.FeignClientException ex) {
            int status = ex.status();
            if(HttpStatus.NOT_FOUND.value() == status) {
                throw new HttpStatusNotFoundException("Dados do cliente não encontrado para o cpf informado");
            }
            throw new ErroComunicacaoMicroservicesException(ex.contentUTF8(), status);
        }

    }
}
