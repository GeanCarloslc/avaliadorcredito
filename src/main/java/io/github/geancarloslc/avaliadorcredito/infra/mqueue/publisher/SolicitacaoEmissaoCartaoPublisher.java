package io.github.geancarloslc.avaliadorcredito.infra.mqueue.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.geancarloslc.avaliadorcredito.infra.dto.DadosSolicitacaoEmissaoCartaoDTO;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//AMQP este protocolo envia as mensagens em formato STRING
public class SolicitacaoEmissaoCartaoPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Queue queueEmissaoCartoes;

    public void solicitarCartao(DadosSolicitacaoEmissaoCartaoDTO dados)
            throws JsonProcessingException {
        String json = converterIntoJson(dados);
        rabbitTemplate.convertAndSend(queueEmissaoCartoes.getName(), json);
    }

    private String converterIntoJson(DadosSolicitacaoEmissaoCartaoDTO dadosSolicitacaoEmissaoCartao)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dadosSolicitacaoEmissaoCartao);
        return json;
    }
}
