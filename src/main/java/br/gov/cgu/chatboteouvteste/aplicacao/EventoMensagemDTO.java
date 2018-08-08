package br.gov.cgu.chatboteouvteste.aplicacao;

import br.gov.cgu.chatboteouvteste.Constantes;

import java.util.Map;

public class EventoMensagemDTO {

    private Map<String, Object> sender;
    private Long timestamp;
    private Map<String, Object> message;
    private Map<String, Object> postback;


    public EventoMensagemDTO(Map<String, Map<String, Object>> evento) {
        this.sender = evento.get(Constantes.CAMPO_SENDER_MENSAGEM);
        this.timestamp = (Long)(Object) evento.get(Constantes.CAMPO_TIMESTAMP_MENSAGEM);
        this.message = evento.get(Constantes.CAMPO_MESSAGE_MENSAGEM);
        this.postback = evento.get(Constantes.CAMPO_POSTBACK_MENSAGEM);
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
