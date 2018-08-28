package br.gov.cgu.chatboteouvteste.aplicacao;

import com.github.messenger4j.Messenger;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.send.MessagePayload;
import com.github.messenger4j.send.MessageResponse;
import com.github.messenger4j.send.MessagingType;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.message.TemplateMessage;
import com.github.messenger4j.send.message.TextMessage;
import com.github.messenger4j.send.message.template.ButtonTemplate;
import com.github.messenger4j.send.message.template.GenericTemplate;
import com.github.messenger4j.send.message.template.ListTemplate;
import com.github.messenger4j.send.message.template.Template;
import com.github.messenger4j.send.message.template.button.Button;
import com.github.messenger4j.send.message.template.button.PostbackButton;
import com.github.messenger4j.send.message.template.button.UrlButton;
import com.github.messenger4j.send.message.template.common.Element;
import com.github.messenger4j.send.recipient.IdRecipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.cgu.chatboteouvteste.Constantes.TIPO_PAYLOAD_BOTAO_POSTBACK;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
public class IntegracaoMessengerService {

    private static final String TIPO_PAYLOAD_BOTAO_POSTBACK = "DEVELOPER_DEFINED_PAYLOAD";
    private static final String TIPO_PAYLOAD_SELECAO_TIPO_MANIFESTACAO = "SELECAO_TIPO_MANIFESTACAO";
    private static final String TIPO_PAYLOAD_MENSAGEM_TEXTO = "DEVELOPER_DEFINED_METADATA";

    private static String urlImagensAplicacao;

    @Value("${aplicacao.url.imagens}")
    public void setUrlImagensAplicacao(String urlImagensAplicacao) {
        IntegracaoMessengerService.urlImagensAplicacao = urlImagensAplicacao;
    }

    private static Messenger messenger;

    @Autowired
    public IntegracaoMessengerService(Messenger messenger) {
        IntegracaoMessengerService.messenger = messenger;
    }

    /**
     * Cria botões de postback (máximo de 3 opções).
     * @param opcoes - list<String>: texto dos botões
     * @return lista de botões - List<PostbackButton>
     */
    public static List<PostbackButton> criarBotoesPostback(List<String> opcoes) {
        validarListaDeBotoes(opcoes);
        return opcoes.stream().map(x -> PostbackButton.create(x, TIPO_PAYLOAD_BOTAO_POSTBACK)).collect(Collectors.toList());
    }

    /**
     * Cria lista de botões com opões de URL.
     * @param opcoes - Map<String, String>: chave = título;  valor = URL
     * @return lista de botões - List<UrlButton>
     *
     * Exemplo de criação do parâmetro de mapa de opções:
     *    Map<String, String> opcoes = new HashMap<>();
     *    opcoes.put("Elogio", "https://eouv.cgu.gov.br/registrar/elogio");
     *    opcoes.put("Denúncia", "https://eouv.cgu.gov.br/registrar/denuncia");
     */
    public static List<UrlButton> criarBotoesURL(Map<String, String> opcoes) {
        return opcoes.entrySet().stream().map(x -> {
            try {
                return UrlButton.create(x.getKey(), new URL(x.getValue()));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("URL do botão inválida!");
            }
        }).collect(Collectors.toList());
    }

    /**
     * Cria elemento com botão(ões) postback.
     * @param titulo
     * @param subtitulo (opcional)
     * @param nomeArquivoImagem (opcional)
     * @param botoes - máximo de 3 botões (opcional)
     * @return elemento - Element
     */
    public static Element criarElementoComBotaoPostback(String titulo, Optional<String> subtitulo,
                                                        Optional<String> nomeArquivoImagem, Optional<List<PostbackButton>> botoes) {
        try {
            Optional<List<Button>> listaBotoes = empty();
            if (botoes.isPresent()) {
                validarListaDeBotoes(botoes.get());
                listaBotoes = of(new ArrayList<>(botoes.get()));
            }
            return Element.create(titulo, subtitulo, nomeArquivoImagem.isPresent() ? of(new URL(urlImagensAplicacao + nomeArquivoImagem.get())) : empty(),
                    empty(), listaBotoes);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL do elemento inválida!");
        }
    }

    /**
     * Enviar mensagem de texto sem espera de retorno.
     * @param recipientId
     * @param texto
     * @return MessageResponse
     * @throws MessengerApiException
     * @throws MessengerIOException
     */
    public static MessageResponse enviarMensagemDeTexto(String recipientId, String texto) throws MessengerApiException, MessengerIOException {
        final IdRecipient recipient = IdRecipient.create(recipientId);
        final NotificationType notificationType = NotificationType.REGULAR;

        final TextMessage textMessage = TextMessage.create(texto, empty(), of(TIPO_PAYLOAD_MENSAGEM_TEXTO));
        final MessagePayload messagePayload = MessagePayload.create(recipient, MessagingType.RESPONSE, textMessage, of(notificationType), empty());
        return messenger.send(messagePayload);
    }

    /**
     * Enviar mensagem com opção de botões.
     * @param recipientId
     * @param titulo
     * @param botoes - List<Button>
     * @throws MessengerApiException
     * @throws MessengerIOException
     *
     * Exemplo de criação de lista de botões:
     *    final List<Button> buttons = Arrays.asList(
     *        UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/rift/"), of(WebviewHeightRatio.COMPACT), of(false), empty(), empty()),
     *        PostbackButton.create("Trigger Postback", "DEVELOPER_DEFINED_PAYLOAD"),
     *        CallButton.create("Call Phone Number", "+16505551234")
     *    );
     */
    public static void enviarMensagemDeBotoes(String recipientId, String titulo, List<Button> botoes) throws MessengerApiException, MessengerIOException {
        final ButtonTemplate buttonTemplate = ButtonTemplate.create(titulo, botoes);
        enviarMensagem(recipientId, buttonTemplate);
    }

    /**
     * Enviar mensagem com opção de lista.
     * @param recipientId
     * @param elementos  - List<Element>
     * @throws MessengerApiException
     * @throws MessengerIOException
     *
     * Exemplo de criação de lista de elementos:
     *    List<Button> riftButtons = new ArrayList<>();
     *    riftButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/rift/")));
     *
     *    List<Button> touchButtons = new ArrayList<>();
     *    touchButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/touch/")));
     *
     *    final List<Element> elements = new ArrayList<>();
     *    elements.add(Element.create("rift", of("Next-generation virtual reality"), of(new URL("https://www.oculus.com/en-us/rift/")), empty(), of(riftButtons)));
     *    elements.add(Element.create("touch", of("Your Hands, Now in VR"), of(new URL("https://www.oculus.com/en-us/touch/")), empty(), of(touchButtons)));
     */
    public static void enviarMensagemDeLista(String recipientId, Optional<String> titulo, List<Element> elementos) throws MessengerApiException, MessengerIOException {
        validarListaDeElementos(elementos);
        final ListTemplate listTemplate = ListTemplate.create(elementos);
        if (titulo.isPresent()) {
            enviarMensagemDeTexto(recipientId, titulo.get());
        }
        enviarMensagem(recipientId, listTemplate);
    }

    /**
     * Enviar mensagem com opção de lista.
     * @param recipientId
     * @param elementos  - List<Element>
     * @throws MessengerApiException
     * @throws MessengerIOException
     *
     * Exemplo de criação de lista de elementos:
     *    List<Button> riftButtons = new ArrayList<>();
     *    riftButtons.add(PostbackButton.create("Call Postback", "Payload for first bubble"));
     *
     *    List<Button> touchButtons = new ArrayList<>();
     *    touchButtons.add(PostbackButton.create("Call Postback", "Payload for second bubble"));
     *
     *    final List<Element> elements = new ArrayList<>();
     *    elements.add(Element.create("rift", of("Next-generation virtual reality"), of(new URL("https://www.oculus.com/en-us/rift/")), empty(), of(riftButtons)));
     *    elements.add(Element.create("touch", of("Your Hands, Now in VR"), of(new URL("https://www.oculus.com/en-us/touch/")), empty(), of(touchButtons)));
     */
    public static void enviarMensagemDeElementoGenerico(String recipientId, Optional<String> titulo, List<Element> elementos) throws MessengerApiException, MessengerIOException {
        final GenericTemplate genericTemplate = GenericTemplate.create(elementos);
        if (titulo.isPresent()) {
            enviarMensagemDeTexto(recipientId, titulo.get());
        }
        enviarMensagem(recipientId, genericTemplate);
    }

    private static void enviarMensagem(String recipientId, Template template) throws MessengerApiException, MessengerIOException {
        final TemplateMessage templateMessage = TemplateMessage.create(template);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, templateMessage);
        messenger.send(messagePayload);
    }

    public static void validarListaDeBotoes(List<?> opcoes) {
        if (opcoes == null || opcoes.size() == 0) {
            throw new IllegalArgumentException("Lista de opções não foi informada.");
        }
        if (opcoes.size() > 3) {
            throw new IllegalArgumentException("Somente 3 opções podem ser informadas para criação de botões.");
        }
    }

    private static void validarListaDeElementos(List<Element> elementos) {
        if (elementos == null) {
            throw new IllegalArgumentException("Lista de elementos não foi informada.");
        }
        if (elementos.size() < 2 || elementos.size() > 4) {
            throw new IllegalArgumentException("Lista de elementos deve conter no mínimo 2 e no máximo 4 ocorrências.");
        }
    }

}
