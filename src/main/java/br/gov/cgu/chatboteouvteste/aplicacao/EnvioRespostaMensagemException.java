package br.gov.cgu.chatboteouvteste.aplicacao;

public class EnvioRespostaMensagemException extends RuntimeException {

    public EnvioRespostaMensagemException(String erro) {
        super(erro);
    }

}