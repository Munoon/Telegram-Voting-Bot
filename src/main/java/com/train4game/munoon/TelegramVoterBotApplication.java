package com.train4game.munoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@SpringBootApplication
public class TelegramVoterBotApplication {
    public static void main(String[] args) throws TelegramApiRequestException {
        ApiContextInitializer.init();
        ConfigurableApplicationContext application = SpringApplication.run(TelegramVoterBotApplication.class, args);

        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(application.getBean(TelegramBot.class));
    }
}
