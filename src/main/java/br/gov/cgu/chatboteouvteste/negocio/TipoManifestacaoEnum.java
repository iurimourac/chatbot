package br.gov.cgu.chatboteouvteste.negocio;

import java.util.Arrays;

public enum TipoManifestacaoEnum {
    DENUNCIA("Denúncia"),
    RECLAMACAO("Reclamação"),
    SOLICITACAO("Solicitação"),
    SUGESTAO("Sugestão"),
    ELOGIO("Elogio"),
    SIMPLIFIQUE("Simplifique");

    private final String descricao;

    TipoManifestacaoEnum(String descricao) {
        this.descricao = descricao;
    }

    public static TipoManifestacaoEnum get(String descricao) {
        return Arrays.stream(values()).filter(v -> v.descricao.equals(descricao)).findFirst().orElse(null);
    }
}
