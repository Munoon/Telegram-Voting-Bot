package com.train4game.munoon.commands;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class StartCommand extends BotCommand {
    public StartCommand() {
        super("start", "Start command");
    }

    @Override
    @SneakyThrows
    public void execute(AbsSender sender, User user, Chat chat, String[] arguments) {
        SendMessage message = new SendMessage(chat.getId(), "Start message");
        sender.execute(message);
    }
}
