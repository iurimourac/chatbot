package br.gov.cgu.chatboteouvteste.web;

import br.gov.cgu.chatboteouvteste.aplicacao.GerenciadorDeInteracaoUsuario;
import com.github.messenger4j.Messenger;
import com.github.messenger4j.exception.MessengerVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.github.messenger4j.Messenger.*;
import static java.util.Optional.of;

@RestController
@RequestMapping("/webhook")
public class GerenciarInteracaoUsuarioWebhookController {

    private final Logger logger = LoggerFactory.getLogger(GerenciarInteracaoUsuarioWebhookController.class);

    private final Messenger messenger;
    private final GerenciadorDeInteracaoUsuario gerenciadorDeInteracaoUsuario;

    @Autowired
    public GerenciarInteracaoUsuarioWebhookController(Messenger messenger, GerenciadorDeInteracaoUsuario gerenciadorDeInteracaoUsuario) {
        this.messenger = messenger;
        this.gerenciadorDeInteracaoUsuario = gerenciadorDeInteracaoUsuario;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> autenticar(@RequestParam(MODE_REQUEST_PARAM_NAME) final String mode,
                                             @RequestParam(VERIFY_TOKEN_REQUEST_PARAM_NAME) final String verifyToken,
                                             @RequestParam(CHALLENGE_REQUEST_PARAM_NAME) final String challenge) {
        logger.debug("***WEBHOOK*** Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode, verifyToken, challenge);
        try {
            this.messenger.verifyWebhook(mode, verifyToken);
            logger.info("Autenticação com sucesso!!!");
            return ResponseEntity.ok(challenge);
        } catch (MessengerVerificationException e) {
            logger.warn("Falha na verificação do Webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final String payload, @RequestHeader(SIGNATURE_HEADER_NAME) final String signature) {
        logger.debug("***WEBHOOK*** Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
            this.messenger.onReceiveEvents(payload, of(signature), gerenciadorDeInteracaoUsuario::processarEvento);
            logger.debug("Callback payload processado com sucesso");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessengerVerificationException e) {
            logger.warn("Falha no processamento do callback payload: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @RequestMapping(value = "limpar-interacoes", method = RequestMethod.GET)
    public ResponseEntity<Void> limparInteracoes() {
        logger.debug("***WEBHOOK*** Interacoes de usuario removidas");
        gerenciadorDeInteracaoUsuario.limparInteracoes();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
