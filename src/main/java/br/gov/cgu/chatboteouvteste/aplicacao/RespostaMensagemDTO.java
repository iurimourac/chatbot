package br.gov.cgu.chatboteouvteste.aplicacao;

import java.util.Map;

public class RespostaMensagemDTO {

    private TipoMensagem tipoMensagem;
    private Map<String, Object> recipient;
    private Map<String, Object> message;

    public RespostaMensagemDTO(TipoMensagem tipoMensagem, Map<String, Object> recipient, Map<String, Object> message) {
        this.tipoMensagem = tipoMensagem;
        this.recipient = recipient;
        this.message = message;
    }

    public TipoMensagem getTipoMensagem() {
        return tipoMensagem;
    }

    public Map<String, Object> getRecipient() {
        return recipient;
    }

    public Map<String, Object> getMessage() {
        return message;
    }
}
