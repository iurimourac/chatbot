package br.gov.cgu.chatboteouvteste.aplicacao.prototipo;

import java.util.Map;

import static br.gov.cgu.chatboteouvteste.Constantes.*;

public class EventoMensagemDTO {

    private Map<String, Object> sender;
    private Long timestamp;
    private Map<String, Object> message;
    private Map<String, Object> postback;

    public EventoMensagemDTO(Map<String, Map<String, Object>> evento) {
        this.sender = evento.get(CAMPO_SENDER_MENSAGEM);
        this.timestamp = (Long)(Object) evento.get(CAMPO_TIMESTAMP_MENSAGEM);
        this.message = evento.get(CAMPO_MESSAGE_MENSAGEM);
        this.postback = evento.get(CAMPO_POSTBACK_MENSAGEM);
    }

    public Map<String, Object> getSender() {
        return sender;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getMessage() {
        return message;
    }

    public Map<String, Object> getPostback() {
        return postback;
    }
}
