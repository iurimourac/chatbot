package br.gov.cgu.chatboteouvteste.negocio;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EventoUsuario {

    private String senderId;
    private String recipientId;
    private Instant timestamp;
    private TipoManifestacaoEnum tipoManifestacao;

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

    public TipoManifestacaoEnum getTipoManifestacao() {
        return tipoManifestacao;
    }

    public void setTipoManifestacao(TipoManifestacaoEnum tipoManifestacao) {
        this.tipoManifestacao = tipoManifestacao;
    }

    public boolean isNovoEventoUsuario() {
        return tipoManifestacao == null;
    }
}
