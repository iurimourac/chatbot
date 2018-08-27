package br.gov.cgu.chatboteouvteste.negocio;

import java.time.Instant;

public class InteracaoUsuario {

    private String senderId;
    private String recipientId;
    private Instant timestamp;
    private TipoManifestacao tipoManifestacao;
    private EtapaTipoManifestacao ultimaEtapaInteracaoProcessada;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public TipoManifestacao getTipoManifestacao() {
        return tipoManifestacao;
    }

    public void setTipoManifestacao(TipoManifestacao tipoManifestacao) {
        this.tipoManifestacao = tipoManifestacao;
    }

    public EtapaTipoManifestacao getUltimaEtapaInteracaoProcessada() {
        return ultimaEtapaInteracaoProcessada;
    }

    public void setUltimaEtapaInteracaoProcessada(EtapaTipoManifestacao ultimaEtapaInteracaoProcessada) {
        this.ultimaEtapaInteracaoProcessada = ultimaEtapaInteracaoProcessada;
    }

    public boolean isNovoEventoUsuario() {
        return tipoManifestacao == null;
    }

    public boolean isTodasEtapaProcessadas() {
        return tipoManifestacao != null && ultimaEtapaInteracaoProcessada != null
                && tipoManifestacao.isTodasEtapasProcessadas(ultimaEtapaInteracaoProcessada.getId());
    }

    @Override
    public String toString() {
        return "InteracaoUsuario{" +
                "senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", timestamp=" + timestamp +
                ", tipoManifestacao=" + tipoManifestacao +
                ", ultimaEtapaInteracaoProcessada=" + ultimaEtapaInteracaoProcessada +
                '}';
    }
}
