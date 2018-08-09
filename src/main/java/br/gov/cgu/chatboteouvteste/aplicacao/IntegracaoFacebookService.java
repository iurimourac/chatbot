package br.gov.cgu.chatboteouvteste.aplicacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
        StringBuilder mensagem = new StringBuilder();
        mensagem.append("{\"recipient\": {\"id\": ").append(mensagemDTO.getSenderId()).append("}");
        mensagem.append(",\"message\": {\"text\": ").append(conteudoMensagem).append("}");
        mensagem.append("}");
        return mensagem.toString();
    }

    private HttpHeaders getHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

}
