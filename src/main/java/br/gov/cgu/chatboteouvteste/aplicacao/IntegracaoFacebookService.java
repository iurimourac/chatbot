package br.gov.cgu.chatboteouvteste.aplicacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

import static br.gov.cgu.chatboteouvteste.Constantes.CAMPO_ID_MENSAGEM;
import static br.gov.cgu.chatboteouvteste.Constantes.CAMPO_TEXTO_MENSAGEM;

@Service
public class IntegracaoFacebookService {

    @Value("${api.facebook.url}")
    private String urlFacebook;

    @Value("${page.access.token}")
    private String accessToken;

    private final Logger logger = LoggerFactory.getLogger(IntegracaoFacebookService.class);

    private RestTemplate restTemplate;

    @Autowired
    public IntegracaoFacebookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> enviarMensagem(RequisicaoMensagemDTO mensagemDTO, String conteudoMensagem) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlFacebook).queryParam("access_token", accessToken);
        String mensagem = montarMensagem(mensagemDTO, conteudoMensagem);
        HttpEntity<String> entity = new HttpEntity<>(mensagem, getHeaders());

        logger.info("Mensagem de resposta: {}", mensagem);
        return restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);
    }

    private String montarMensagem(RequisicaoMensagemDTO mensagemDTO, String conteudoMensagem) {
        try {
            Map<String, String> destinatario = new HashMap<>();
            destinatario.put(CAMPO_ID_MENSAGEM, mensagemDTO.getSenderId());

            Map<String, String> mensagem = new HashMap<>();
            mensagem.put(CAMPO_TEXTO_MENSAGEM, conteudoMensagem);

            RespostaMensagemDTO respostaMensagemDTO = new RespostaMensagemDTO(TipoMensagem.RESPONSE, destinatario, mensagem);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(respostaMensagemDTO);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            throw new RespostaMensagemException("Erro na geração de mensagem de retorno!");
        }

//        JsonNode rootNode = mapper.createObjectNode();
//
//        JsonNode recipient = mapper.createObjectNode()
//
//        StringBuilder mensagem = new StringBuilder();
//        mensagem.append("{\"recipient\": {\"id\": \"").append(mensagemDTO.getSenderId()).append("\"}");
//        mensagem.append(",\"message\": {\"text\": \"").append(conteudoMensagem).append("\"}");
//        mensagem.append("}");
//        return mensagem.toString();
    }

    private HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

}
