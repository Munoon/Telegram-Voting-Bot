package com.train4game.munoon;

import com.train4game.munoon.commands.StartCommand;
import com.train4game.munoon.messageParser.ConnectKeyParser;
import com.train4game.munoon.messageParser.MessageParser;
import com.train4game.munoon.messageParser.VoteParser;
import com.train4game.munoon.repository.TelegramUserRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingCommandBot {
    private TelegramUserRepository telegramUserRepository;
    private BeanFactory beanFactory;
    private final String botToken;

    public TelegramBot(Environment environment, TelegramUserRepository telegramUserRepository, BeanFactory beanFactory) {
        super(ApiContext.getInstance(DefaultBotOptions.class), false);
        this.botToken = environment.getRequiredProperty("telegram.token");
        this.telegramUserRepository = telegramUserRepository;
        this.beanFactory = beanFactory;

        register(beanFactory.getBean(StartCommand.class));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message message = update.getMessage();
        boolean containsConnectedKey = telegramUserRepository.containsConnectedKey(message.getChatId());
        MessageParser messageParser;

        if (containsConnectedKey) {
            messageParser = beanFactory.getBean(VoteParser.class);
        } else {
            messageParser = beanFactory.getBean(ConnectKeyParser.class);
        }

        messageParser.parse(this, message);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
