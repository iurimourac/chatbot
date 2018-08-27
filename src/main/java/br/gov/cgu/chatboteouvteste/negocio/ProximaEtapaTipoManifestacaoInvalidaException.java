package br.gov.cgu.chatboteouvteste.negocio;

public class ProximaEtapaTipoManifestacaoInvalidaException extends RuntimeException {

    public ProximaEtapaTipoManifestacaoInvalidaException() {
        super("Próxima etapa do tipo de manifestação inválida.");
    }
}
