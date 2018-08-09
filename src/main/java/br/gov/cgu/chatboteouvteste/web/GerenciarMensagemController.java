package br.gov.cgu.chatboteouvteste.web;

import br.gov.cgu.chatboteouvteste.Constantes;
import br.gov.cgu.chatboteouvteste.aplicacao.GerenciadorDeMensagem;
import br.gov.cgu.chatboteouvteste.negocio.MensagemInvalidaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@RestController
public class GerenciarMensagemController {

    @Value("${page.access.token}")
    private String accessToken;

    private final Logger logger = LoggerFactory.getLogger(GerenciarMensagemController.class);

    private final GerenciadorDeMensagem gerenciadorDeMensagem;

    @Autowired
    public GerenciarMensagemController(GerenciadorDeMensagem gerenciadorDeMensagem) {
        this.gerenciadorDeMensagem = gerenciadorDeMensagem;
    }

    @RequestMapping(value = "/webhook", method = RequestMethod.GET)
    public ResponseEntity<String> verificar(@RequestParam(value = "hub.mode") String mode,
                                            @RequestParam(value = "hub.verify_token") String token,
                                            @RequestParam(value = "hub.challenge") String challenge) {
        if (isNotEmpty(mode) && isNotEmpty(token)) {
            if (mode.equals(Constantes.MODO_SUBSCRIBE) && token.equals(accessToken)) {
                logger.info("Autenticação com sucesso!!!");
                return new ResponseEntity<>(challenge, HttpStatus.OK);
            } else {
                logger.error("Falha na autenticação!!!");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        logger.error("Parâmetros informados incorretamente!!!");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> tratarMensagem(@RequestBody String mensagem) {
        try {
            logger.info("Mensagem recebida com sucesso!!! Conteúdo: {}.", mensagem);
            gerenciadorDeMensagem.processarMensagem(mensagem);

            return new ResponseEntity<>("SUCESSO", HttpStatus.OK);
        } catch (MensagemInvalidaException e) {
            logger.error("Mensagem inválida!!! {}", mensagem);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}