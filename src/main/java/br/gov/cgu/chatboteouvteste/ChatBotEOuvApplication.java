package br.gov.cgu.chatboteouvteste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
public class ChatBotEOuvApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatBotEOuvApplication.class, args);
    }

    static DataSource dataSource(String usuario, String password, String server) {
        //TODO Definir datasource
        return null;
    }
}
