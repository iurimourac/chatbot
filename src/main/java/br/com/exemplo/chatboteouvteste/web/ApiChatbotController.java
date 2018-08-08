package br.com.exemplo.chatboteouvteste.web;

import br.com.exemplo.chatboteouvteste.Constantes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@RestController
public class ApiChatbotController {

    @Value("${webhook.token}")
    private String tokenWebhook;

    private final Logger logger = LoggerFactory.getLogger(ApiChatbotController.class);

    @RequestMapping(value = "/webhook", method = RequestMethod.GET)
    public ResponseEntity<String> verificar(@RequestParam(value = "hub.mode") String mode,
                                            @RequestParam(value = "hub.verify_token") String token,
                                            @RequestParam(value = "hub.challenge") String challenge) {
        if (isNotEmpty(mode) && isNotEmpty(token)) {
            if (mode.equals(Constantes.MODO_SUBSCRIBE) && token.equals(tokenWebhook)) {
                logger.info("[INFO] Autenticação com sucesso!!!");
                return new ResponseEntity<>(challenge, HttpStatus.OK);
            } else {
                logger.error("[ERROR] Falha na autenticação!!!");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        logger.error("[ERROR] Parâmetros informados incorretamente!!!");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> tratarMensagem(@RequestBody String mensagem) {
        logger.info("[INFO] Mensagem recebida com sucesso!!!");
        return new ResponseEntity<>("SUCESSO", HttpStatus.OK);
    }

}
