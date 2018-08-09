package br.gov.cgu.chatboteouvteste.aplicacao;

public class RespostaMensagemException extends RuntimeException {

    public RespostaMensagemException(String erro) {
        super(erro);
    }

}