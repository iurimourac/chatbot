package chatboteouvteste.web;

import chatboteouvteste.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@RestController
public class ApiChatbotController {

    @Value("${webhook.token}")
    private String tokenWebhook;

    @RequestMapping(value = "/webhook", method = RequestMethod.GET)
    public ResponseEntity<String> verificar(@RequestParam(value = "hub.mode") String mode,
                                            @RequestParam(value = "hub.verify_token") String token,
                                            @RequestParam(value = "hub.challenge") String challenge) {
        if (isNotEmpty(mode) && isNotEmpty(token)) {
            if (mode.equals(Constantes.MODO_SUBSCRIBE) && token.equals(tokenWebhook)) {
                return new ResponseEntity<>(challenge, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> tratarMensagem(@RequestBody String mensagem) {
System.out.println("POSSSSSSSSSSSSTTTTTTTTTTTTTTTT!!!!!!!!!!!!!!!!!!!!!");
        return new ResponseEntity<>("SUCESSO", HttpStatus.OK);
    }

}
