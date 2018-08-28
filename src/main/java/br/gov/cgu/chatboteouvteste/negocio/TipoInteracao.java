package br.gov.cgu.chatboteouvteste.negocio;

import br.gov.cgu.chatboteouvteste.aplicacao.IntegracaoMessengerService;
import com.github.messenger4j.exception.MessengerApiException;
import com.github.messenger4j.exception.MessengerIOException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public enum TipoInteracao {

    AFIRMACAO {
        @Override
        public void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
                throws MessengerApiException, MessengerIOException {
            processarMensagemDeTexto(recipientId, texto, parametros);
        }
    },

    PERGUNTA_COM_RESPOSTA_DESCRITIVA {
        @Override
        public void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
                throws MessengerApiException, MessengerIOException {
            processarMensagemDeTexto(recipientId, texto, parametros);
        }
    },

    PERGUNTA_COM_MULTIPLAS_RESPOSTAS {
        @Override
        public void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
                throws MessengerApiException, MessengerIOException {
            processarMensagemDeTexto(recipientId, texto, parametros);
        }
    },

    PERGUNTA_COM_SELECAO_DE_BOTAO_PARA_MULTIPLAS_RESPOSTAS {
        @Override
        public void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
                throws MessengerApiException, MessengerIOException {
            processarMensagemDeBotao(recipientId, texto, opcoes, parametros);
        }
    },

    PERGUNTA_COM_SELECAO_DE_BOTAO {
        @Override
        public void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
                throws MessengerApiException, MessengerIOException {
            //TODO Parse dos parametros no texto
            processarMensagemDeBotao(recipientId, texto, opcoes, parametros);
        }
    },

    PERGUNTA_COM_SELECAO_DE_LISTA {
        @Override
        public void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
                throws MessengerApiException, MessengerIOException {
            validarParametros(recipientId, texto, parametros);
            IntegracaoMessengerService.enviarMensagemDeLista(recipientId, ofNullable(texto),
                    opcoes.orElseThrow(() -> new IllegalArgumentException("A lista de elementos não foi informada.")));
        }
    },

    PERGUNTA_COM_SELECAO_DE_ELEMENTO_GENERICO {
        @Override
        public void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
                throws MessengerApiException, MessengerIOException {
            validarParametros(recipientId, texto, parametros);
            IntegracaoMessengerService.enviarMensagemDeElementoGenerico(recipientId, ofNullable(texto),
                    opcoes.orElseThrow(() -> new IllegalArgumentException("A lista de elementos não foi informada.")));
        }
    };



    public static final String TIPO_PAYLOAD_SELECAO_TIPO_MANIFESTACAO = "SELECAO_TIPO_MANIFESTACAO";
    public static final String TIPO_PAYLOAD_RECOMECAR = "SELECAO_RECOMECAR";

    public abstract void processar(String recipientId, String texto, Optional<List> opcoes, Optional<String>... parametros)
            throws MessengerApiException, MessengerIOException;

    void processarMensagemDeTexto(String recipientId, String texto, Optional<String>[] parametros) throws MessengerApiException, MessengerIOException {
            validarParametros(recipientId, texto, parametros);
            IntegracaoMessengerService.enviarMensagemDeTexto(recipientId, texto);
    }

    void processarMensagemDeBotao(String recipientId, String texto, Optional<List> opcoes, Optional<String>[] parametros)
            throws MessengerApiException, MessengerIOException {
        validarParametros(recipientId, texto, parametros);
        IntegracaoMessengerService.validarListaDeBotoes(opcoes != null && opcoes.isPresent() ? opcoes.get() : null);
        IntegracaoMessengerService.enviarMensagemDeBotoes(recipientId, texto, opcoes.get());
    }

    void validarParametros(String recipientId, String texto, Optional<String>[] parametros) {
        if (StringUtils.isBlank(recipientId)) {
            throw new IllegalArgumentException("O destinatário <recipientId> não foi informado.");
        }
        if (StringUtils.isBlank(texto)) {
            throw new IllegalArgumentException("O texto da mensagem não foi informado.");
        }
    }
}
