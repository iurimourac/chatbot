package br.gov.cgu.chatboteouvteste.aplicacao;

import br.gov.cgu.chatboteouvteste.negocio.MensagemInvalidaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;

import static br.gov.cgu.chatboteouvteste.Constantes.OBJETO_PAGINA;

@Service
public class GerenciadorDeMensagem {

    private IntegracaoFacebookService facebookService;

    @Autowired
    public GerenciadorDeMensagem(IntegracaoFacebookService facebookService) {
        this.facebookService = facebookService;
    }

    public void tratarMensagem(String mensagem) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            RequisicaoMensagemDTO requisicaoMensagemDTO = mapper.readValue(mensagem, RequisicaoMensagemDTO.class);
            validarMensagem(requisicaoMensagemDTO);
            facebookService.enviarMensagem(requisicaoMensagemDTO);
        } catch (IOException e) {
            throw new MensagemInvalidaException();
        }
    }

    private void validarMensagem(RequisicaoMensagemDTO requisicaoMensagemDTO) {
        if (!requisicaoMensagemDTO.getObject().equals(OBJETO_PAGINA)) {
            throw new MensagemInvalidaException("Objeto da mensagem inválido!");
        }

        if (StringUtils.isEmpty(requisicaoMensagemDTO.getSenderId())) {
            throw new MensagemInvalidaException("Id do remetente não informado!");
        }

        if (CollectionUtils.isEmpty(requisicaoMensagemDTO.getEvento().getMessage())
                && CollectionUtils.isEmpty(requisicaoMensagemDTO.getEvento().getPostback())) {
            throw new MensagemInvalidaException("Mensagem/Postback não informado!");
        }
    }
}
