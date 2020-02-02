package com.train4game.munoon;

import com.train4game.munoon.commands.StartCommand;
import com.train4game.munoon.messageParser.ConnectKeyParser;
import com.train4game.munoon.messageParser.MessageParser;
import com.train4game.munoon.messageParser.VoteParser;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingCommandBot {
    private TelegramSpamProtection spamProtection = new TelegramSpamProtection();
    private TelegramUserRepository userRepository;
    private BeanFactory beanFactory;
    private final String botToken;

    public TelegramBot(Environment environment, TelegramUserRepository userRepository, BeanFactory beanFactory) {
        super(ApiContext.getInstance(DefaultBotOptions.class), false);
        this.botToken = environment.getRequiredProperty("telegram.token");
        this.userRepository = userRepository;
        this.beanFactory = beanFactory;

        register(beanFactory.getBean(StartCommand.class));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message message = update.getMessage();
        boolean containsConnectedKey = userRepository.containsConnectedKey(message.getChatId());
        MessageParser messageParser;

        if (containsConnectedKey) {
            messageParser = beanFactory.getBean(VoteParser.class);
        } else {
            messageParser = beanFactory.getBean(ConnectKeyParser.class);
        }

        messageParser.parse(this, message);
    }

    @Override
    @SneakyThrows
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            Chat chat = update.getMessage().getChat();
            SendMessage sendMessage = new SendMessage()
                    .setChatId(chat.getId());

            if (chat.isGroupChat()) {
                sendMessage.setText("Bot not working in group chat");
                execute(sendMessage);
                return;
            }

            spamProtection.messageReceived(chat.getId());
            if (!spamProtection.shouldProcess(chat.getId())) {
                sendMessage.setText("Too much messages");
                execute(sendMessage);
                return;
            }

            onUpdateReceived(update);
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
