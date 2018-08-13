package br.gov.cgu.chatboteouvteste.aplicacao;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) {
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) {

    }
}
