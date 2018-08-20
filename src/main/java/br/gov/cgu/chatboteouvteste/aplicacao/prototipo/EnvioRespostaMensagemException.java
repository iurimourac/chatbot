package br.gov.cgu.chatboteouvteste.aplicacao.prototipo;

public class EnvioRespostaMensagemException extends RuntimeException {

    public EnvioRespostaMensagemException(String erro) {
        super(erro);
    }

}