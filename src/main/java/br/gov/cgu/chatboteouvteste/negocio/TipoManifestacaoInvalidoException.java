package br.gov.cgu.chatboteouvteste.negocio;

public class TipoManifestacaoInvalidoException extends RuntimeException {

    public TipoManifestacaoInvalidoException() {
        super("Tipo de manifestação inválido!");
    }

    public TipoManifestacaoInvalidoException(String erro) {
        super(erro);
    }
}