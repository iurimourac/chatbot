package br.gov.cgu.chatboteouvteste.aplicacao;

import br.gov.cgu.chatboteouvteste.negocio.*;
import com.github.messenger4j.Messenger;
import com.github.messenger4j.common.WebviewHeightRatio;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import com.github.messenger4j.send.MessagePayload;
import com.github.messenger4j.send.MessagingType;
import com.github.messenger4j.send.NotificationType;
import com.github.messenger4j.send.SenderActionPayload;
import com.github.messenger4j.send.message.RichMediaMessage;
import com.github.messenger4j.send.message.TemplateMessage;
import com.github.messenger4j.send.message.TextMessage;
import com.github.messenger4j.send.message.quickreply.LocationQuickReply;
import com.github.messenger4j.send.message.quickreply.QuickReply;
import com.github.messenger4j.send.message.quickreply.TextQuickReply;
import com.github.messenger4j.send.message.richmedia.UrlRichMediaAsset;
import com.github.messenger4j.send.message.template.ButtonTemplate;
import com.github.messenger4j.send.message.template.GenericTemplate;
import com.github.messenger4j.send.message.template.ListTemplate;
import com.github.messenger4j.send.message.template.ReceiptTemplate;
import com.github.messenger4j.send.message.template.button.*;
import com.github.messenger4j.send.message.template.common.Element;
import com.github.messenger4j.send.message.template.receipt.Address;
import com.github.messenger4j.send.message.template.receipt.Adjustment;
import com.github.messenger4j.send.message.template.receipt.Item;
import com.github.messenger4j.send.message.template.receipt.Summary;
import com.github.messenger4j.send.recipient.IdRecipient;
import com.github.messenger4j.send.senderaction.SenderAction;
import com.github.messenger4j.userprofile.UserProfile;
import com.github.messenger4j.webhook.Event;
import com.github.messenger4j.webhook.event.*;
import com.github.messenger4j.webhook.event.attachment.Attachment;
import com.github.messenger4j.webhook.event.attachment.LocationAttachment;
import com.github.messenger4j.webhook.event.attachment.RichMediaAttachment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

import static com.github.messenger4j.send.message.richmedia.RichMediaAsset.Type.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Service
public class GerenciadorDeInteracaoUsuario {

    private final Logger logger = LoggerFactory.getLogger(GerenciadorDeInteracaoUsuario.class);
    private static final String RESOURCE_URL = "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public";

    private InteracaoUsuario interacaoUsuario;
    private StringBuilder descricaoManifestacao;

    private final Messenger messenger;
    private InteracoesUsuarios interacoesUsuarios;

    @Autowired
    public GerenciadorDeInteracaoUsuario(Messenger messenger, InteracoesUsuarios interacoesUsuarios) {
        this.messenger = messenger;
        this.interacoesUsuarios = interacoesUsuarios;
    }

    public void limparInteracoes() {
        logger.debug("Antes limpeza interações: {}", interacoesUsuarios.toString());
        interacoesUsuarios = new InteracoesUsuarios();
        logger.debug("Depois limpeza interações: {}", interacoesUsuarios.toString());
    }

    public void processarEvento(Event event) {
        logger.debug("Antes sincronização: {}", interacoesUsuarios);
        montarInteracaoUsuario(event);
        boolean isNovaInteracao = interacoesUsuarios.isNovaInteracao(interacaoUsuario);
        interacoesUsuarios.adicionar(interacaoUsuario);
        logger.debug("Depois sincronização: {}", interacoesUsuarios);

        try {
            if (isNovaInteracao) {
                enviarApresentacaoInicial();
            } else {
                if (event.isTextMessageEvent()) {
                    if (interacaoUsuario.isMultiplasRespostas()) {
                        descricaoManifestacao.append(event.asTextMessageEvent().text()).append(" ");
                    } else {
                        processarProximaEtapa();
                    }
                    handleTextMessageEvent(event.asTextMessageEvent());
                } else if (event.isQuickReplyMessageEvent()) {
                    handleQuickReplyMessageEvent(event.asQuickReplyMessageEvent());
                } else if (event.isPostbackEvent()) {
                    tratarEventoDeRetornoDoUsuario(event);
                    processarProximaEtapa();
                    handlePostbackEvent(event.asPostbackEvent());
                }
            }
            logger.debug("Depois tratamento de evento: {}", interacoesUsuarios);
        } catch (MessengerApiException | MessengerIOException e) {
            handleSendException(e);
        }
    }

    private void montarInteracaoUsuario(Event event) {
        interacaoUsuario = interacoesUsuarios.get(event.senderId());
        if (interacaoUsuario == null) {
            interacaoUsuario = new InteracaoUsuario();
            interacaoUsuario.setSenderId(event.senderId());
            interacaoUsuario.setRecipientId(event.recipientId());
            interacaoUsuario.setTimestamp(event.timestamp());
            logger.debug("Nova interação de usuário.");
        }
        logger.debug("montarInteracaoUsuario: {}", interacaoUsuario);
    }

    private void enviarApresentacaoInicial() {
        try {
            logger.debug("Apresentação inicial...");
            EtapaTipoManifestacao etapaInicial = EtapaTipoManifestacaoBuilder.getEtapaInicial();
            etapaInicial.getTipoInteracao().processar(interacaoUsuario.getSenderId(), etapaInicial.getDescricao(), etapaInicial.getOpcoes());
            interacaoUsuario.setUltimaEtapaInteracaoProcessada(etapaInicial);
        } catch (MessengerApiException | MessengerIOException e) {
            logger.error("Ocorreu um erro ao enviar a mensagem inicial da interação.", e);
        }
    }

    private void tratarEventoDeRetornoDoUsuario(Event event) {
        logger.debug("Tratando evento: event: {}, title: {}, payload: {}", event, event.asPostbackEvent().title(), event.asPostbackEvent().payload());
        PostbackEvent postbackEvent = event.asPostbackEvent();
        if (event == null || StringUtils.isBlank(postbackEvent.title()) || !postbackEvent.payload().isPresent()) {
            throw new EventoPostbackInvalidoException();
        }

        boolean isNovaSelecaoDeTipoDeManifestacao = postbackEvent.payload().get().equals(TipoInteracao.TIPO_PAYLOAD_SELECAO_TIPO_MANIFESTACAO);
        if (isNovaSelecaoDeTipoDeManifestacao) {
            TipoManifestacao tipoManifestacao = TipoManifestacao.get(postbackEvent.title());
            if (tipoManifestacao == null) {
                throw new EventoPostbackInvalidoException();
            }

            if (interacaoUsuario.isNovoEventoUsuario()) {
                logger.debug("Nova interação de usuário");
                interacaoUsuario.setTipoManifestacao(tipoManifestacao);
            } else {
                if (isNovaSelecaoDeTipoDeManifestacao) {
                    logger.debug("Seleção de novo tipo de manifestação. Usuário já tinha selecionado outra antes.");
                    interacaoUsuario.setTipoManifestacao(tipoManifestacao);
                    interacaoUsuario.setUltimaEtapaInteracaoProcessada(EtapaTipoManifestacaoBuilder.getEtapaInicial());
                }
            }
        }
    }

    private void processarProximaEtapa(Optional<String>... parametros) throws MessengerApiException, MessengerIOException {
        logger.debug("Processando próxima etapa...");
        validarDadosDeInteracaoUsuario();

        EtapaTipoManifestacao etapa = interacaoUsuario.getTipoManifestacao().getProximaEtapa(interacaoUsuario.getUltimaEtapaInteracaoProcessada().getId());

        if (etapa.isRegistrarManifestacao()) {
            //TODO Implementar chamada ao webservice
        }

        etapa.processar(interacaoUsuario.getSenderId(), Optional.empty());
        interacaoUsuario.setUltimaEtapaInteracaoProcessada(etapa);

        if (interacaoUsuario.isTodasEtapaProcessadas()) {
            logger.debug("Todas as etapas foram processadas. Processando a etapa final... {}", interacoesUsuarios);
            etapa = EtapaTipoManifestacaoBuilder.getEtapaFinal();
            etapa.processar(interacaoUsuario.getSenderId());
            interacoesUsuarios.remover(interacaoUsuario);
            logger.debug("Interacao de usuario removida. {}", interacoesUsuarios);
        }
    }

    private void validarDadosDeInteracaoUsuario() {
        if (interacaoUsuario.getTipoManifestacao() == null) {
            throw new TipoManifestacaoInvalidoException("Tipo de manifestação não definido na interação!");
        }

        if (interacaoUsuario.getUltimaEtapaInteracaoProcessada() == null) {
            throw new TipoManifestacaoInvalidoException("Etapa do tipo de manifestação não definida na interação!");
        }
    }

    private void handleTextMessageEvent(TextMessageEvent event) {
        logger.debug("Received TextMessageEvent: {}", event);

        final String messageId = event.messageId();
        final String messageText = event.text();
        final String senderId = event.senderId();
        final Instant timestamp = event.timestamp();

        logger.info("Received message '{}' with text '{}' from user '{}' at '{}'", messageId, messageText, senderId, timestamp);

        try {
            switch (messageText.toLowerCase()) {
                case "user":
                    sendUserDetails(senderId);
                    break;

                case "image":
                    sendImageMessage(senderId);
                    break;

                case "gif":
                    sendGifMessage(senderId);
                    break;

                case "audio":
                    sendAudioMessage(senderId);
                    break;

                case "video":
                    sendVideoMessage(senderId);
                    break;

                case "file":
                    sendFileMessage(senderId);
                    break;

                case "button":
                    sendButtonMessage(senderId);
                    break;

                case "generic":
                    sendGenericMessage(senderId);
                    break;

                case "list":
                    sendListMessageMessage(senderId);
                    break;

                case "receipt":
                    sendReceiptMessage(senderId);
                    break;

                case "quick reply":
                    sendQuickReply(senderId);
                    break;

                case "read receipt":
                    sendReadReceipt(senderId);
                    break;

                case "typing on":
                    sendTypingOn(senderId);
                    break;

                case "typing off":
                    sendTypingOff(senderId);
                    break;

                case "account linking":
                    sendAccountLinking(senderId);
                    break;

                default:
                    logger.debug("Default text message {}", messageText);
//                    sendTextMessage(senderId, messageText);
            }
        } catch (MessengerApiException | MessengerIOException | MalformedURLException e) {
            handleSendException(e);
        }
    }

    private void sendUserDetails(String recipientId) throws MessengerApiException, MessengerIOException {
        final UserProfile userProfile = this.messenger.queryUserProfile(recipientId);
        sendTextMessage(recipientId, String.format("Your name is %s and you are %s", userProfile.firstName(), userProfile.gender()));
        logger.info("User Profile Picture: {}", userProfile.profilePicture());
    }

    private void sendImageMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        final UrlRichMediaAsset richMediaAsset = UrlRichMediaAsset.create(IMAGE, new URL(RESOURCE_URL + "/assets/rift.png"));
        sendRichMediaMessage(recipientId, richMediaAsset);
    }

    private void sendRichMediaMessage(String recipientId, UrlRichMediaAsset richMediaAsset) throws MessengerApiException, MessengerIOException {
        final RichMediaMessage richMediaMessage = RichMediaMessage.create(richMediaAsset);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, richMediaMessage);
        this.messenger.send(messagePayload);
    }

    private void sendGifMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        final UrlRichMediaAsset richMediaAsset = UrlRichMediaAsset.create(IMAGE, new URL("https://media.giphy.com/media/11sBLVxNs7v6WA/giphy.gif"));
        sendRichMediaMessage(recipientId, richMediaAsset);
    }

    private void sendAudioMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        final UrlRichMediaAsset richMediaAsset = UrlRichMediaAsset.create(AUDIO, new URL(RESOURCE_URL + "/assets/sample.mp3"));
        sendRichMediaMessage(recipientId, richMediaAsset);
    }

    private void sendVideoMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        final UrlRichMediaAsset richMediaAsset = UrlRichMediaAsset.create(VIDEO, new URL(RESOURCE_URL + "/assets/allofus480.mov"));
        sendRichMediaMessage(recipientId, richMediaAsset);
    }

    private void sendFileMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        final UrlRichMediaAsset richMediaAsset = UrlRichMediaAsset.create(FILE, new URL(RESOURCE_URL + "/assets/test.txt"));
        sendRichMediaMessage(recipientId, richMediaAsset);
    }

    private void sendButtonMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        final List<Button> buttons = Arrays.asList(
                UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/rift/"), of(WebviewHeightRatio.COMPACT), of(false), empty(), empty()),
                PostbackButton.create("Trigger Postback", "DEVELOPER_DEFINED_PAYLOAD"),
                CallButton.create("Call Phone Number", "+16505551234")
        );

        final ButtonTemplate buttonTemplate = ButtonTemplate.create("Tap a button", buttons);
        final TemplateMessage templateMessage = TemplateMessage.create(buttonTemplate);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, templateMessage);
        this.messenger.send(messagePayload);
    }

    private void sendGenericMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        List<Button> riftButtons = new ArrayList<>();
        riftButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/rift/")));
        riftButtons.add(PostbackButton.create("Call Postback", "Payload for first bubble"));

        List<Button> touchButtons = new ArrayList<>();
        touchButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/touch/")));
        touchButtons.add(PostbackButton.create("Call Postback", "Payload for second bubble"));

        final List<Element> elements = new ArrayList<>();

        elements.add(
                Element.create("rift", of("Next-generation virtual reality"), of(new URL("https://www.oculus.com/en-us/rift/")), empty(), of(riftButtons)));
        elements.add(Element.create("touch", of("Your Hands, Now in VR"), of(new URL("https://www.oculus.com/en-us/touch/")), empty(), of(touchButtons)));

        final GenericTemplate genericTemplate = GenericTemplate.create(elements);
        final TemplateMessage templateMessage = TemplateMessage.create(genericTemplate);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, templateMessage);
        this.messenger.send(messagePayload);
    }

    private void sendListMessageMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        List<Button> riftButtons = new ArrayList<>();
        riftButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/rift/")));

        List<Button> touchButtons = new ArrayList<>();
        touchButtons.add(UrlButton.create("Open Web URL", new URL("https://www.oculus.com/en-us/touch/")));

        final List<Element> elements = new ArrayList<>();

        elements.add(
                Element.create("rift", of("Next-generation virtual reality"), of(new URL("https://www.oculus.com/en-us/rift/")), empty(), of(riftButtons)));
        elements.add(Element.create("touch", of("Your Hands, Now in VR"), of(new URL("https://www.oculus.com/en-us/touch/")), empty(), of(touchButtons)));

        final ListTemplate listTemplate = ListTemplate.create(elements);
        final TemplateMessage templateMessage = TemplateMessage.create(listTemplate);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, templateMessage);
        this.messenger.send(messagePayload);
    }

    private void sendReceiptMessage(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        final String uniqueReceiptId = "order-" + Math.floor(Math.random() * 1000);

        final List<Item> items = new ArrayList<>();

        items.add(Item.create("Oculus Rift", 599.00f, of("Includes: headset, sensor, remote"), of(1), of("USD"),
                of(new URL(RESOURCE_URL + "/assets/riftsq.png"))));
        items.add(Item.create("Samsung Gear VR", 99.99f, of("Frost White"), of(1), of("USD"), of(new URL(RESOURCE_URL + "/assets/gearvrsq.png"))));

        final ReceiptTemplate receiptTemplate = ReceiptTemplate
                .create("Peter Chang", uniqueReceiptId, "Visa 1234", "USD", Summary.create(626.66f, of(698.99f), of(57.67f), of(20.00f)),
                        of(Address.create("1 Hacker Way", "Menlo Park", "94025", "CA", "US")), of(items),
                        of(Arrays.asList(Adjustment.create("New Customer Discount", -50f), Adjustment.create("$100 Off Coupon", -100f))),
                        of("The Boring Company"), of(new URL("https://www.boringcompany.com/")), of(true), of(Instant.ofEpochMilli(1428444852L)));

        final TemplateMessage templateMessage = TemplateMessage.create(receiptTemplate);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, templateMessage);
        this.messenger.send(messagePayload);
    }

    private void sendQuickReply(String recipientId) throws MessengerApiException, MessengerIOException {
        List<QuickReply> quickReplies = new ArrayList<>();

        quickReplies.add(TextQuickReply.create("Action", "DEVELOPER_DEFINED_PAYLOAD_FOR_PICKING_ACTION"));
        quickReplies.add(TextQuickReply.create("Comedy", "DEVELOPER_DEFINED_PAYLOAD_FOR_PICKING_COMEDY"));
        quickReplies.add(TextQuickReply.create("Drama", "DEVELOPER_DEFINED_PAYLOAD_FOR_PICKING_DRAMA"));
        quickReplies.add(LocationQuickReply.create());

        TextMessage message = TextMessage.create("What's your favorite movie genre?", of(quickReplies), empty());
        messenger.send(MessagePayload.create(recipientId, MessagingType.RESPONSE, message));
    }

    private void sendReadReceipt(String recipientId) throws MessengerApiException, MessengerIOException {
        this.messenger.send(SenderActionPayload.create(recipientId, SenderAction.MARK_SEEN));
    }

    private void sendTypingOn(String recipientId) throws MessengerApiException, MessengerIOException {
        this.messenger.send(SenderActionPayload.create(recipientId, SenderAction.TYPING_ON));
    }

    private void sendTypingOff(String recipientId) throws MessengerApiException, MessengerIOException {
        this.messenger.send(SenderActionPayload.create(recipientId, SenderAction.TYPING_OFF));
    }

    private void sendAccountLinking(String recipientId) throws MessengerApiException, MessengerIOException, MalformedURLException {
        // Mandatory https
//        final LogInButton buttonIn = LogInButton.create(new URL("https://<YOUR_REST_CALLBACK_URL>"));
        // [CGU]
        final LogInButton buttonIn = LogInButton.create(new URL("https://chatboteouvteste.heroku.com/callback"));
        final LogOutButton buttonOut = LogOutButton.create();

        final List<Button> buttons = Arrays.asList(buttonIn, buttonOut);
        final ButtonTemplate buttonTemplate = ButtonTemplate.create("Log in to see an account linking callback", buttons);

        final TemplateMessage templateMessage = TemplateMessage.create(buttonTemplate);
        final MessagePayload messagePayload = MessagePayload.create(recipientId, MessagingType.RESPONSE, templateMessage);
        this.messenger.send(messagePayload);
    }

    private void handleAttachmentMessageEvent(AttachmentMessageEvent event) {
        logger.debug("Handling QuickReplyMessageEvent");
        final String senderId = event.senderId();
        logger.debug("senderId: {}", senderId);
        for (Attachment attachment : event.attachments()) {
            if (attachment.isRichMediaAttachment()) {
                final RichMediaAttachment richMediaAttachment = attachment.asRichMediaAttachment();
                final RichMediaAttachment.Type type = richMediaAttachment.type();
                final URL url = richMediaAttachment.url();
                logger.debug("Received rich media attachment of type '{}' with url: {}", type, url);
                final String text = String.format("Media %s received (url: %s)", type.name(), url);
                sendTextMessage(senderId, text);
            } else if (attachment.isLocationAttachment()) {
                final LocationAttachment locationAttachment = attachment.asLocationAttachment();
                final double longitude = locationAttachment.longitude();
                final double latitude = locationAttachment.latitude();
                logger.debug("Received location information (long: {}, lat: {})", longitude, latitude);
                final String text = String.format("Location received (long: %s, lat: %s)", String.valueOf(longitude), String.valueOf(latitude));
                sendTextMessage(senderId, text);
            }
        }
    }

    private void handleQuickReplyMessageEvent(QuickReplyMessageEvent event) {
        logger.debug("Handling QuickReplyMessageEvent");
        final String payload = event.payload();
        logger.debug("payload: {}", payload);
        final String senderId = event.senderId();
        logger.debug("senderId: {}", senderId);
        final String messageId = event.messageId();
        logger.debug("messageId: {}", messageId);
        logger.info("Received quick reply for message '{}' with payload '{}'", messageId, payload);
        sendTextMessage(senderId, "Quick reply tapped");
    }

    private void handlePostbackEvent(PostbackEvent event) {
        logger.debug("Handling PostbackEvent");
        final String payload = event.payload().orElse("empty payload");
        logger.debug("payload: {}", payload);
        final String title = event.title();
        logger.debug("title: {}", title);
        final String senderId = event.senderId();
        logger.debug("senderId: {}", senderId);
        final Instant timestamp = event.timestamp();
        logger.debug("timestamp: {}", timestamp);
        logger.info("Received postback for user '{}' and page '{}' with payload '{}' and title '{}' at '{}'", senderId, senderId, payload, title, timestamp);
//        sendTextMessage(senderId, "Postback event tapped");
    }

    private void sendTextMessage(String recipientId, String text) {
        try {
            final IdRecipient recipient = IdRecipient.create(recipientId);
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            final TextMessage textMessage = TextMessage.create(text, empty(), of(metadata));
            final MessagePayload messagePayload = MessagePayload.create(recipient, MessagingType.RESPONSE, textMessage,
                    of(notificationType), empty());
            this.messenger.send(messagePayload);
        } catch (MessengerApiException | MessengerIOException e) {
            handleSendException(e);
        }
    }

    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }

}
