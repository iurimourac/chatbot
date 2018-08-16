package br.gov.cgu.chatboteouvteste;

import br.gov.cgu.chatboteouvteste.negocio.EventoUsuario;
import br.gov.cgu.chatboteouvteste.negocio.InteracoesUsuarios;
import com.github.messenger4j.Messenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public Messenger messenger(@Value("${messenger4j.pageAccessToken}") String pageAccessToken,
                               @Value("${messenger4j.appSecret}") final String appSecret,
                               @Value("${messenger4j.verifyToken}") final String verifyToken) {
        logger.info("Parâmetros Messenger:");
        logger.info("messenger4j.pageAccessToken: {}", pageAccessToken);
        logger.info("messenger4j.appSecret {}", appSecret);
        logger.info("messenger4j.verifyToken: {}", verifyToken);
        return Messenger.create(pageAccessToken, appSecret, verifyToken);
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public EventoUsuario eventoUsuario() {
        return new EventoUsuario();
    }

//    @Bean
//    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
//    public InteracoesUsuarios interacoesUsuarios() {
//        return new InteracoesUsuarios();
//    }

}
