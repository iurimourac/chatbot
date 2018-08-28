package br.gov.cgu.chatboteouvteste.aplicacao;

import br.gov.cgu.chatboteouvteste.negocio.EtapaTipoManifestacao;
import br.gov.cgu.chatboteouvteste.negocio.TipoInteracao;
import com.github.messenger4j.send.message.template.common.Element;

import java.util.Arrays;

import static br.gov.cgu.chatboteouvteste.aplicacao.IntegracaoMessengerService.criarBotoesPostback;
import static br.gov.cgu.chatboteouvteste.aplicacao.IntegracaoMessengerService.criarElementoComBotaoPostback;
import static br.gov.cgu.chatboteouvteste.negocio.TipoManifestacao.*;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public class EtapaTipoManifestacaoBuilder {

    private static final Integer ID_ETAPA_INICIAL = 0;
    private static final Integer ID_ETAPA_FINAL = 99;
    private static final String DESCRICAO_REGISTRO_TIPO_MANIFESTACAO = "Clique no botão abaixo para registrar ";

    private EtapaTipoManifestacaoBuilder() {}

    public static EtapaTipoManifestacao getEtapaInicial() {
        return new EtapaTipoManifestacao(ID_ETAPA_INICIAL, "Olá! Eu sou o “Chico Bot”, o robô Ouvidor! " +
                "Por aqui, posso te ajudar a registrar uma manifestação para as Ouvidorias do Governo Federal. " +
                "Gostaria de registrar qual tipo?",
                of(Arrays.asList(
                        criarElementoRegistroManifestacao(DENUNCIA.getDescricao(), "uma " + DENUNCIA.getDescricao(),
                                "linkDenuncia.png"),
                        criarElementoRegistroManifestacao(RECLAMACAO.getDescricao(), "uma " + RECLAMACAO.getDescricao(),
                                "linkReclamacao.png"),
                        criarElementoRegistroManifestacao(SOLICITACAO.getDescricao(), "uma " + SOLICITACAO.getDescricao(),
                                "linkSolicitacao.png"),
                        criarElementoRegistroManifestacao(SUGESTAO.getDescricao(), "uma " + SUGESTAO.getDescricao(),
                                "linkSugestao.png"),
                        criarElementoRegistroManifestacao(ELOGIO.getDescricao(), "um " + ELOGIO.getDescricao(),
                                "linkElogio.png"),
                        criarElementoRegistroManifestacao(SIMPLIFIQUE.getDescricao(), "uma solicitação de simplificação",
                                "linkSimplifique.png")
                )),
                TipoInteracao.PERGUNTA_COM_SELECAO_DE_ELEMENTO_GENERICO);
    }

    private static Element criarElementoRegistroManifestacao(String descricaoTipoManifestacao, String complementoDescricao, String nomeArquivoImagem) {
        return criarElementoComBotaoPostback(descricaoTipoManifestacao, of(DESCRICAO_REGISTRO_TIPO_MANIFESTACAO + complementoDescricao),
                of(nomeArquivoImagem), of(criarBotoesPostback(singletonList(descricaoTipoManifestacao),
                        of(TipoInteracao.TIPO_PAYLOAD_SELECAO_TIPO_MANIFESTACAO))));
    }

    public static EtapaTipoManifestacao getEtapaFinal() {
        return new EtapaTipoManifestacao(ID_ETAPA_FINAL, "Sempre que você precisar falar com alguma Ouvidoria, lembre-se de mim. " +
                "Caso você queira fazer mais alguma manifestação, basta clicar em começar.",
                of(criarBotoesPostback(singletonList("Começar"), of(TipoInteracao.TIPO_PAYLOAD_RECOMECAR))),
                TipoInteracao.RECOMECAR);
    }
}
