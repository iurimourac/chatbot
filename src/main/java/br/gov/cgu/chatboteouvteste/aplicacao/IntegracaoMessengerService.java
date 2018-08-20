package br.gov.cgu.chatboteouvteste.aplicacao;

import com.github.messenger4j.Messenger;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.send.MessagePayload;
import com.github.messenger4j.send.MessagingType;
import com.github.messenger4j.send.message.TemplateMessage;
import com.github.messenger4j.send.message.template.ButtonTemplate;
import com.github.messenger4j.send.message.template.ListTemplate;
import com.github.messenger4j.send.message.template.Template;
import com.github.messenger4j.send.message.template.button.Button;
import com.github.messenger4j.send.message.template.button.PostbackButton;
import com.github.messenger4j.send.message.template.common.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.gov.cgu.chatboteouvteste.Constantes.TIPO_PAYLOAD_BOTAO_POSTBACK;

@Service
public class IntegracaoMessengerService {

    private static Messenger messenger;

    @Autowired
    public IntegracaoMessengerService(Messenger messenger) {
        IntegracaoMessengerService.messenger = messenger;
    }

    public static List<Button> criarBotoesPostback(List<String> opcoes) {
        if (opcoes.size() > 3) {
            throw new IllegalArgumentException("Somente 3 opções podem ser informadas para criação de botões.");
        }
        List<Button> botoes = new ArrayList<>();
        botoes.addAll(opcoes.stream()
                .map(x -> PostbackButton.create(x, TIPO_PAYLOAD_BOTAO_POSTBACK))
                .collect(Collectors.toList()));
        return botoes;
    }

    /**
     * Enviar mensagem com opção de botões.
     * @param recipientId
     * @param titulo
     * @param botoes
     * @throws MessengerApiException
     * @throws MessengerIOException
     *
     * Exemplo de criação de botões:
     *    final List<Button> buttons = Arrays.asList(
     *            UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/rift/"), of(WebviewHeightRatio.COMPACT), of(false), empty(), empty()),
     *            PostbackButton.create("Trigger Postback", "DEVELOPER_DEFINED_PAYLOAD"),
     *            CallButton.create("Call Phone Number", "+16505551234")
     *    );
     */
    public static void enviarMensagemDeBotoes(String recipientId, String titulo, List<Button> botoes) throws MessengerApiException, MessengerIOException {
        final ButtonTemplate buttonTemplate = ButtonTemplate.create(titulo, botoes);
        enviarMensagem(recipientId, buttonTemplate);
    }

    /**
     * Enviar mensagem com opção de lista.
     * @param recipientId
     * @param elementos
     * @throws MessengerApiException
     * @throws MessengerIOException
     *
     * Exemplo de criação de elementos:
     *    List<Button> riftButtons = new ArrayList<>();
     *    riftButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/rift/")));
     *
     *    List<Button> touchButtons = new ArrayList<>();
     *    touchButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/touch/")));
     *
     *    final List<Element> elements = new ArrayList<>();
     *
     *    elements.add(Element.create("rift", of("Next-generation virtual reality"), of(new URL("https://www.oculus.com/en-us/rift/")), empty(), of(riftButtons)));
     *    elements.add(Element.create("touch", of("Your Hands, Now in VR"), of(new URL("https://www.oculus.com/en-us/touch/")), empty(), of(touchButtons)));
     */
    public static void enviarMensagemDeLista(String recipientId, List<Element> elementos) throws MessengerApiException, MessengerIOException {
        final ListTemplate listTemplate = ListTemplate.create(elementos);
        enviarMensagem(recipientId, listTemplate);
    }

    private static void enviarMensagem(String recipientId, Template template) throws MessengerApiException, MessengerIOException {
        final TemplateMessage templateMessage = TemplateMessage.create(template);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, templateMessage);
        messenger.send(messagePayload);
    }
}
