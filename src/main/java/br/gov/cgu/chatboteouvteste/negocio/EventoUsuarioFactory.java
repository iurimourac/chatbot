package br.gov.cgu.chatboteouvteste.negocio;

import com.github.messenger4j.webhook.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public final class EventoUsuarioFactory {

    private final Logger logger = LoggerFactory.getLogger(EventoUsuarioFactory.class);

    @Autowired
    private EventoUsuario eventoUsuario;

    private EventoUsuarioFactory() {}

    public static EventoUsuario getOrCreate(Event event) {
        EventoUsuarioFactory factory = new EventoUsuarioFactory();
factory.logger.debug("Factory inicio - {}", factory.eventoUsuario.toString());
        if (factory.eventoUsuario == null) {
            factory.eventoUsuario = new EventoUsuario();
        }
        factory.eventoUsuario.setSenderId(event.senderId());
        factory.eventoUsuario.setRecipientId(event.recipientId());
        factory.eventoUsuario.setTimestamp(event.timestamp());
factory.logger.debug("Factory fim - {}", factory.eventoUsuario.toString());
        return factory.eventoUsuario;
    }
}
