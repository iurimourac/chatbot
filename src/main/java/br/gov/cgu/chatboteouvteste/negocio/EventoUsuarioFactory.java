package br.gov.cgu.chatboteouvteste.negocio;

import com.github.messenger4j.webhook.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class EventoUsuarioFactory {

    private static EventoUsuario eventoUsuario;

    @Autowired
    public EventoUsuarioFactory(EventoUsuario eventoUsuario) {
        EventoUsuarioFactory.eventoUsuario = eventoUsuario;
    }

    public static EventoUsuario getOrCreate(Event event) {
        if (eventoUsuario == null) {
            eventoUsuario = new EventoUsuario();
        }
        eventoUsuario.setSenderId(event.senderId());
        eventoUsuario.setRecipientId(event.recipientId());
        eventoUsuario.setTimestamp(event.timestamp());
        return eventoUsuario;
    }
}
