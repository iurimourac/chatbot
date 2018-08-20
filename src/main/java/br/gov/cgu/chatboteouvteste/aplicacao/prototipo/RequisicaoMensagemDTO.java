package br.gov.cgu.chatboteouvteste.aplicacao.prototipo;

import br.gov.cgu.chatboteouvteste.negocio.MensagemInvalidaException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.gov.cgu.chatboteouvteste.Constantes.CAMPO_EVENTO_MENSAGEM;
import static br.gov.cgu.chatboteouvteste.Constantes.CAMPO_ID_MENSAGEM;
import static br.gov.cgu.chatboteouvteste.Constantes.CAMPO_TEXTO_MENSAGEM;

public class RequisicaoMensagemDTO {

    private String object;
    private List<Object> entry;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<Object> getEntry() {
        return entry;
    }

    public void setEntry(List<Object> entry) {
        this.entry = entry;
    }

    @SuppressWarnings("unchecked")
    public EventoMensagemDTO getEvento() {
        try {
            Map<String, List> entries = (HashMap<String, List>) getEntry().get(0);
            Map<String, Map<String, Object>> evento = (Map<String, Map<String, Object>>) entries.get(CAMPO_EVENTO_MENSAGEM).get(0);
            return new EventoMensagemDTO(evento);
        } catch (IndexOutOfBoundsException e) {
            throw new MensagemInvalidaException();
        }
    }

    public String getSenderId() {
        return (String) getEvento().getSender().get(CAMPO_ID_MENSAGEM);
    }

    public String getTextoMensagem() {
        return (String) getEvento().getMessage().get(CAMPO_TEXTO_MENSAGEM);
    }
}
