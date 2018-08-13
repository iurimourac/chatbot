package br.gov.cgu.chatboteouvteste.aplicacao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class RespostaMensagemDTO {

    @JsonProperty("messaging_type")
    private TipoMensagemEnum tipoMensagem;

    @JsonProperty("recipient")
    private Map<String, String> destinatario;

    @JsonProperty("message")
    private Map<String, String> mensagem;

    @JsonCreator
    public RespostaMensagemDTO(TipoMensagemEnum tipoMensagem, Map<String, String> destinatario, Map<String, String> mensagem) {
        this.tipoMensagem = tipoMensagem;
        this.destinatario = destinatario;
        this.mensagem = mensagem;
    }

    public TipoMensagemEnum getTipoMensagem() {
        return tipoMensagem;
    }

    public Map<String, String> getDestinatario() {
        return destinatario;
    }

    public Map<String, String> getMensagem() {
        return mensagem;
    }
}
