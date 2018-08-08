package br.gov.cgu.chatboteouvteste.negocio;

public class MensagemInvalidaException extends RuntimeException {

    public MensagemInvalidaException() {
        super("Conteúdo da mensagem inválido!");
    }

    public MensagemInvalidaException(String erro) {
        super(erro);
    }
}