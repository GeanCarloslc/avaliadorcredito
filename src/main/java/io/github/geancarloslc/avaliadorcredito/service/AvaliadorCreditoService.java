package io.github.geancarloslc.avaliadorcredito.service;

import feign.FeignException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.ErroComunicacaoMicroservicesException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.ErroSolicitacaoCartaoException;
import io.github.geancarloslc.avaliadorcredito.domain.exception.HttpStatusNotFoundException;
import io.github.geancarloslc.avaliadorcredito.domain.model.CartaoAprovado;
import io.github.geancarloslc.avaliadorcredito.domain.model.DadosCliente;
import io.github.geancarloslc.avaliadorcredito.domain.model.SituacaoCliente;
import io.github.geancarloslc.avaliadorcredito.infra.client.CartoesControllerClient;
import io.github.geancarloslc.avaliadorcredito.infra.client.ClienteControlerClient;
import io.github.geancarloslc.avaliadorcredito.infra.dto.*;
import io.github.geancarloslc.avaliadorcredito.infra.mqueue.publisher.SolicitacaoEmissaoCartaoPublisher;
import io.github.geancarloslc.avaliadorcredito.infra.dto.DadosSolicitacaoEmissaoCartaoDTO;
import io.github.geancarloslc.avaliadorcredito.infra.mqueue.model.ProtocoloSolicitacaoCartao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AvaliadorCreditoService {

    @Autowired
    private ClienteControlerClient clienteControlerClient;
    @Autowired
    private CartoesControllerClient cartoesControllerClient;
    @Autowired
    private SolicitacaoEmissaoCartaoPublisher solicitacaoEmissaoCartaoPublisher;

    public SituacaoCliente obterSituacaoCliente(String cpf)
            throws HttpStatusNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<ClienteDTO> clienteDtoResponse = clienteControlerClient.buscarCliente(cpf);

            DadosCliente dadosCliente = new DadosCliente(
                    Objects.requireNonNull(clienteDtoResponse.getBody()).getId(),
                    Objects.requireNonNull(clienteDtoResponse.getBody()).getNome(),
                    Objects.requireNonNull(clienteDtoResponse.getBody()).getIdade()
                    );

            ResponseEntity<List<CartaoClienteDTO>> cartaoClienteDTOListaResponseEntity
                    = cartoesControllerClient.buscarCartoesClienteCpf(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosCliente)
                    .cartoes(CartaoClienteDTO.toModel(Objects.requireNonNull(cartaoClienteDTOListaResponseEntity.getBody())))
                    .build();

        } catch (FeignException.FeignClientException ex) {
            int status = ex.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new HttpStatusNotFoundException("Dados do cliente não encontrado para o cpf informado");
            }
            throw new ErroComunicacaoMicroservicesException(ex.contentUTF8(), status);
        }
    }

    public DadosAvaliacaoDTO realizarAvaliacao(String cpf, Long renda)
            throws HttpStatusNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<ClienteDTO> clienteDtoResponse = clienteControlerClient.buscarCliente(cpf);
            ResponseEntity<List<CartaoDTO>> cartaoDtoResponse = cartoesControllerClient.buscarCartoesRendaAteh(renda);
            List<CartaoDTO> cartaoDTOLista = cartaoDtoResponse.getBody();

            List<CartaoAprovado> cartaoAprovadoLista = cartaoDTOLista.stream().map(cartaoDTO -> {
                ClienteDTO clienteDTO = clienteDtoResponse.getBody();
                BigDecimal limiteBasico = cartaoDTO.getLimiteBasico();

                BigDecimal idadeBigDecimal = BigDecimal.valueOf(clienteDTO.getIdade());
                BigDecimal fator = idadeBigDecimal.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartaoDTO.getNome());
                aprovado.setBandeira(cartaoDTO.getBandeira());
                aprovado.setLimiteAproado(limiteAprovado);
                return aprovado;
            }).collect(Collectors.toList());

            return new DadosAvaliacaoDTO(cartaoAprovadoLista);
        } catch (FeignException.FeignClientException ex) {
            int status = ex.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new HttpStatusNotFoundException("Dados do cliente não encontrado para o cpf informado");
            } else {
                throw new ErroComunicacaoMicroservicesException(ex.contentUTF8(), status);
            }
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartaoDTO dadosSolicitacaoEmissaoCartao) {
        try {
            solicitacaoEmissaoCartaoPublisher.solicitarCartao(dadosSolicitacaoEmissaoCartao);
            String protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        } catch (Exception ex) {
            throw new ErroSolicitacaoCartaoException(ex.getMessage());
        }
    }
}
