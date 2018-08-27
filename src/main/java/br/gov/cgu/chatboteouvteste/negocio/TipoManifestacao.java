package br.gov.cgu.chatboteouvteste.negocio;

import br.gov.cgu.chatboteouvteste.aplicacao.EtapaTipoManifestacaoBuilder;
import br.gov.cgu.chatboteouvteste.aplicacao.IntegracaoMessengerService;

import java.util.*;

import static br.gov.cgu.chatboteouvteste.negocio.TipoInteracao.PERGUNTA_COM_RESPOSTA_DESCRITIVA;
import static br.gov.cgu.chatboteouvteste.negocio.TipoInteracao.PERGUNTA_COM_SELECAO_DE_BOTAO;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public enum TipoManifestacao {

    DENUNCIA("Denúncia", Arrays.asList(
            //TODO Definir etapas
    )),

    RECLAMACAO("Reclamação", Arrays.asList(
            new EtapaTipoManifestacao(1, "Certo. Vejo que você quer fazer uma reclamação. Para qual órgão público você deseja que eu envie sua reclamação?",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(2, "Agora, por favor, me explique direitinho sua reclamação.",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(3, "Estou anotando, pode continuar. Quando tiver terminado, pressione “Encerrar”.",
                    of(IntegracaoMessengerService.criarBotoesPostback(singletonList("Encerrar"))),
                    PERGUNTA_COM_SELECAO_DE_BOTAO),
            new EtapaTipoManifestacao(4, "Obrigada por se manifestar! Registrei sua reclamação com o número de protocolo {:X}. " +
                    "Se quiser acompanhar o andamento, clique no botão a seguir e informe seu e-mail e o número de protocolo fornecido.",
                    montarBotaoAcompanhamentoManifestacaoEouv(),
                    PERGUNTA_COM_SELECAO_DE_BOTAO)
    )),

    SOLICITACAO("Solicitação", Arrays.asList(
            new EtapaTipoManifestacao(1, "Certo. Vejo que você quer fazer uma solicitação. Para qual órgão público você deseja que eu envie sua solicitação?",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(2, "Agora, por favor, me explique direitinho o que você quer solicitar.",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(3, "Estou anotando, pode continuar. Quando tiver terminado, pressione “Encerrar”.",
                    of(IntegracaoMessengerService.criarBotoesPostback(singletonList("Encerrar"))),
                    PERGUNTA_COM_SELECAO_DE_BOTAO),
            new EtapaTipoManifestacao(4, "Obrigada por se manifestar! Registrei sua solicitação com o número de protocolo {:X}. " +
                    "Se quiser acompanhar o andamento, clique no botão a seguir e informe seu e-mail e o número de protocolo fornecido.",
                    montarBotaoAcompanhamentoManifestacaoEouv(),
                    PERGUNTA_COM_SELECAO_DE_BOTAO)
    )),

    SUGESTAO("Sugestão", Arrays.asList(
            new EtapaTipoManifestacao(1, "Certo. Vejo que você quer fazer uma sugestão. Para qual órgão público você deseja que eu envie sua sugestão?",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(2, "Agora, por favor, me explique direitinho sua sugestão.",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(3, "Estou anotando, pode continuar. Quando tiver terminado, pressione “Encerrar”.",
                    of(IntegracaoMessengerService.criarBotoesPostback(singletonList("Encerrar"))),
                    PERGUNTA_COM_SELECAO_DE_BOTAO),
            new EtapaTipoManifestacao(4, "Obrigada por se manifestar! Registrei sua sugestão com o número de protocolo {:X}. " +
                    "Se quiser acompanhar o andamento, clique no botão a seguir e informe seu e-mail e o número de protocolo fornecido.",
                    montarBotaoAcompanhamentoManifestacaoEouv(),
                    PERGUNTA_COM_SELECAO_DE_BOTAO)
    )),

    ELOGIO("Elogio", Arrays.asList(
            new EtapaTipoManifestacao(1, "Certo. Vejo que você quer fazer um elogio. Para qual órgão público você deseja que eu envie seu elogio?",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(2, "Agora, por favor, escreva o elogio que deseja fazer.",
                    PERGUNTA_COM_RESPOSTA_DESCRITIVA),
            new EtapaTipoManifestacao(3, "Estou anotando, pode continuar. Quando tiver terminado, pressione “Encerrar”.",
                    of(IntegracaoMessengerService.criarBotoesPostback(singletonList("Encerrar"))),
                    PERGUNTA_COM_SELECAO_DE_BOTAO),
            new EtapaTipoManifestacao(4, "Obrigada pela seu elogio! Registrei seu elogio com o número de protocolo {:X}. " +
                    "Se quiser acompanhar o andamento, clique no botão a seguir e informe seu e-mail e o número de protocolo fornecido.",
                    montarBotaoAcompanhamentoManifestacaoEouv(),
                    PERGUNTA_COM_SELECAO_DE_BOTAO)
    )),

    SIMPLIFIQUE("Simplifique", Arrays.asList(
            //TODO Definir etapas
    ));


    private final String descricao;
    private List<EtapaTipoManifestacao> etapas;

    TipoManifestacao(String descricao, List<EtapaTipoManifestacao> etapas) {
        this.descricao = descricao;
        this.etapas = etapas;
    }

    public String getDescricao() {
        return descricao;
    }

    public EtapaTipoManifestacao getProximaEtapa(Integer idEtapa) {
        final int totalEtapas = etapas.size() + 1;
        if (idEtapa == null || idEtapa.compareTo(totalEtapas) >= 0) {
            throw new ProximaEtapaTipoManifestacaoInvalidaException();
        }
        final Integer idUltimaEtapa = ++idEtapa;
        return etapas.stream().filter(x -> x.getId().equals(idUltimaEtapa)).findFirst()
                .orElse(EtapaTipoManifestacaoBuilder.getEtapaFinal());
    }

    public boolean isTodasEtapasProcessadas(Integer idEtapa) {
        return idEtapa != null && idEtapa.equals(etapas.size());
    }

    public static TipoManifestacao get(String descricao) {
        return Arrays.stream(values()).filter(v -> v.descricao.equals(descricao)).findFirst().orElse(null);
    }

    private static Optional<List> montarBotaoAcompanhamentoManifestacaoEouv() {
        final String URL_ACOMPANHAR_MANIFESTACAO_EOUV = "https://sistema.ouvidorias.gov.br/publico/Manifestacao/ConsultarManifestacaoLogin.aspx";
        Map<String, String> map = new HashMap<>();
        map.put("Acompanhar", URL_ACOMPANHAR_MANIFESTACAO_EOUV);
        return of(IntegracaoMessengerService.criarBotoesURL(Collections.unmodifiableMap(map)));
    }
}
