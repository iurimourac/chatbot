package br.gov.cgu.chatboteouvteste.aplicacao;

import br.gov.cgu.chatboteouvteste.negocio.EtapaTipoManifestacao;
import br.gov.cgu.chatboteouvteste.negocio.TipoInteracao;
import com.github.messenger4j.send.message.template.button.PostbackButton;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static br.gov.cgu.chatboteouvteste.aplicacao.IntegracaoMessengerService.criarBotoesPostback;
import static br.gov.cgu.chatboteouvteste.aplicacao.IntegracaoMessengerService.criarElementoComBotaoPostback;
import static br.gov.cgu.chatboteouvteste.negocio.TipoManifestacao.*;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class EtapaTipoManifestacaoBuilder {

    private static final Integer ID_ETAPA_INICIAL = 0;
    private static final Integer ID_ETAPA_FINAL = 99;

    private EtapaTipoManifestacaoBuilder() {}

    public static EtapaTipoManifestacao getEtapaInicial() {
        return new EtapaTipoManifestacao(ID_ETAPA_INICIAL, "Olá! Eu sou o “Chico Bot”, o robô Ouvidor! " +
                "Por aqui, posso te ajudar a registrar uma manifestação para as Ouvidorias do Governo Federal. " +
                "Gostaria de registrar qual tipo?",
                of(Arrays.asList(
                        criarElementoComBotaoPostback(DENUNCIA.getDescricao(), empty(), of("linkDenuncia.png"),
                                getBotaoRegistrar(DENUNCIA.getDescricao())),
                        criarElementoComBotaoPostback(RECLAMACAO.getDescricao(), empty(), of("linkReclamacao.png"),
                                getBotaoRegistrar(RECLAMACAO.getDescricao())),
                        criarElementoComBotaoPostback(SOLICITACAO.getDescricao(), empty(), of("linkSolicitacao.png"),
                                getBotaoRegistrar(SOLICITACAO.getDescricao())),
                        criarElementoComBotaoPostback(SUGESTAO.getDescricao(), empty(), of("linkSugestao.png"),
                                getBotaoRegistrar(SUGESTAO.getDescricao())),
                        criarElementoComBotaoPostback(ELOGIO.getDescricao(), empty(), of("linkElogio.png"),
                                getBotaoRegistrar(ELOGIO.getDescricao())),
                        criarElementoComBotaoPostback(SIMPLIFIQUE.getDescricao(), empty(), of("linkSimplifique.png"),
                                getBotaoRegistrar(SIMPLIFIQUE.getDescricao()))
                )),
                TipoInteracao.PERGUNTA_COM_SELECAO_DE_ELEMENTO_GENERICO);
    }

    public static EtapaTipoManifestacao getEtapaFinal() {
        return new EtapaTipoManifestacao(ID_ETAPA_FINAL, "Sempre que você precisar falar com alguma Ouvidoria, lembre-se de mim. " +
                "Caso você queira fazer mais alguma manifestação, basta clicar em começar.",
                of(criarBotoesPostback(singletonList("Começar"))),
                TipoInteracao.PERGUNTA_COM_SELECAO_DE_BOTAO);
    }

    private static Optional<List<PostbackButton>> getBotaoRegistrar(String tipo) {
        return of(criarBotoesPostback(singletonList("Registrar " + tipo)));
    }
}
