package br.gov.cgu.chatboteouvteste.negocio;

import com.github.messenger4j.webhook.Event;
import org.springframework.beans.factory.annotation.Autowired;

public final class EventoUsuarioFactory {

    @Autowired
    private EventoUsuario eventoUsuario;

    private EventoUsuarioFactory() {}

    public static EventoUsuario getOrCreate(Event event) {
        EventoUsuarioFactory factory = new EventoUsuarioFactory();
        EventoUsuario eventoUsuario = factory.eventoUsuario;
        if (eventoUsuario == null) {
            eventoUsuario = new EventoUsuario();
        }
        eventoUsuario.setSenderId(event.senderId());
        eventoUsuario.setRecipientId(event.recipientId());
        eventoUsuario.setTimestamp(event.timestamp());
        return eventoUsuario;
    }
}
