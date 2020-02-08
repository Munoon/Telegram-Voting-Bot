package com.train4game.munoon;

import com.train4game.munoon.commands.ResultsCommand;
import com.train4game.munoon.commands.StartCommand;
import com.train4game.munoon.messageParser.ConnectKeyParser;
import com.train4game.munoon.messageParser.MessageParser;
import com.train4game.munoon.messageParser.VoteParser;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingCommandBot {
    private TelegramSpamProtection spamProtection = new TelegramSpamProtection();
    private TelegramUserRepository userRepository;
    private TelegramMessages telegramMessages;
    private BeanFactory beanFactory;
    private final String botToken;

    public TelegramBot(Environment environment, TelegramUserRepository userRepository, TelegramMessages telegramMessages, BeanFactory beanFactory) {
        super(ApiContext.getInstance(DefaultBotOptions.class), false);
        this.botToken = environment.getRequiredProperty("telegram.token");
        this.userRepository = userRepository;
        this.beanFactory = beanFactory;
        this.telegramMessages = telegramMessages.createWrapper("bot");

        register(beanFactory.getBean(StartCommand.class));
        register(beanFactory.getBean(ResultsCommand.class));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        Message message = update.getMessage();
        int userId = message.getFrom().getId();
        boolean containsConnectedKey = userRepository.containsConnectedKey(userId);
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
                log.info("Attempt to use bot in group chat {}", chat.getId());
                sendMessage.setText(telegramMessages.getProperty("error.groupChat"));
                execute(sendMessage);
                continue;
            }

            Integer userId = update.getMessage().getFrom().getId();
            spamProtection.messageReceived(userId);
            if (!spamProtection.shouldProcess(userId)) {
                log.info("Enabled spam protection on chat {}", chat.getId());
                sendMessage.setText(telegramMessages.getProperty("error.spam"));
                execute(sendMessage);
                continue;
            }

            onUpdateReceived(update);
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
