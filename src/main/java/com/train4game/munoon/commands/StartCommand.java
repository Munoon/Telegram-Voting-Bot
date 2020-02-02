package com.train4game.munoon.commands;

import com.train4game.munoon.TelegramMessages;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Component
public class StartCommand extends BotCommand {
    private TelegramMessages messages;

    public StartCommand(TelegramMessages telegramMessages) {
        super("start", "Start command");
        messages = telegramMessages.createWrapper("commands.start");
    }

    @Override
    @SneakyThrows
    public void execute(AbsSender sender, User user, Chat chat, String[] arguments) {
        log.info("Send start message to chat {}", chat.getId());
        SendMessage message = new SendMessage(chat.getId(), messages.getProperty("message"));
        sender.execute(message);
    }
}
