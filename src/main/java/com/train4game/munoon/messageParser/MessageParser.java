package com.train4game.munoon.messageParser;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface MessageParser {
    void parse(AbsSender sender, Message message);
}
