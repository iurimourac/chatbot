package br.gov.cgu.chatboteouvteste;

import com.github.messenger4j.Messenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

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

}
