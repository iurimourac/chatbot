package br.gov.cgu.chatboteouvteste.negocio;

import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;

import java.util.List;
import java.util.Optional;

public class EtapaTipoManifestacao {

    private Integer id;
    private String descricao;
    private Optional<List> opcoes;
    private TipoInteracao tipoInteracao;
    private boolean registrarManifestacao;

    public EtapaTipoManifestacao(Integer id, String descricao, TipoInteracao tipoInteracao) {
        this.id = id;
        this.descricao = descricao;
        this.tipoInteracao = tipoInteracao;
    }

    public EtapaTipoManifestacao(Integer id, String descricao, Optional<List> opcoes, TipoInteracao tipoInteracao) {
        this(id, descricao, tipoInteracao);
        this.opcoes = opcoes;
    }

    public EtapaTipoManifestacao(Integer id, String descricao, Optional<List> opcoes, TipoInteracao tipoInteracao, boolean registrarManifestacao) {
        this(id, descricao, tipoInteracao);
        this.opcoes = opcoes;
        this.registrarManifestacao = registrarManifestacao;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Optional<List> getOpcoes() {
        return opcoes;
    }

    public TipoInteracao getTipoInteracao() {
        return tipoInteracao;
    }

    public boolean isRegistrarManifestacao() {
        return registrarManifestacao;
    }

    public void processar(String recipienteId, Optional<String>... parametros) throws MessengerApiException, MessengerIOException {
        tipoInteracao.processar(recipienteId, descricao, opcoes, parametros);
    }

    @Override
    public String toString() {
        return "EtapaTipoManifestacao{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                ", tipoInteracao=" + tipoInteracao +
                '}';
    }
}
