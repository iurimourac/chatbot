package br.gov.cgu.chatboteouvteste.aplicacao;

public class EventoPostbackInvalidoException extends RuntimeException {

    public EventoPostbackInvalidoException() {
        super("Evento de retorno do usuário inválido.");
    }
}
