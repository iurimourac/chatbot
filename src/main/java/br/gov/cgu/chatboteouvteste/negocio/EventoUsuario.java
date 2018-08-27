package br.gov.cgu.chatboteouvteste.negocio;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EventoUsuario {

    private String senderId;
    private String recipientId;
    private Instant timestamp;
    private TipoManifestacao tipoManifestacao;

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

    public boolean isNovoEventoUsuario() {
        return tipoManifestacao == null;
    }

    @Override
    public String toString() {
        return "EventoUsuario{" +
                "obj=" + this.hashCode() + ", " +
                "senderId='" + senderId + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", timestamp=" + timestamp +
                ", tipoManifestacao=" + tipoManifestacao +
                '}';
    }

}
