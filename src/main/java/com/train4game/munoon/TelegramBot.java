package com.train4game.munoon;

import com.train4game.munoon.commands.StartCommand;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingCommandBot {
    private final String botToken;

    public TelegramBot(Environment environment, StartCommand startCommand) {
        super(ApiContext.getInstance(DefaultBotOptions.class), false);
        this.botToken = environment.getRequiredProperty("telegram.token");

        register(startCommand);
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        System.out.println(update.getMessage());
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
