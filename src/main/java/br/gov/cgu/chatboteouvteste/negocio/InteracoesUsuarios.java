package br.gov.cgu.chatboteouvteste.negocio;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InteracoesUsuarios {

    private Map<String, InteracaoUsuario> interacoes = new ConcurrentHashMap<>();

    public void adicionar(InteracaoUsuario interacaoUsuario) {
        if (interacaoUsuario == null || StringUtils.isEmpty(interacaoUsuario.getSenderId())) {
            throw new IllegalArgumentException("InteracaoUsuario não informada!");
        }
        interacoes.putIfAbsent(interacaoUsuario.getSenderId(), interacaoUsuario);
    }

    public InteracaoUsuario get(String senderId) {
        InteracaoUsuario interacaoUsuario = new InteracaoUsuario();
        interacaoUsuario.setSenderId(senderId);
        return get(interacaoUsuario);
    }

    public InteracaoUsuario get(InteracaoUsuario interacaoUsuario) {
        return interacoes.get(interacaoUsuario.getSenderId());
    }

}
