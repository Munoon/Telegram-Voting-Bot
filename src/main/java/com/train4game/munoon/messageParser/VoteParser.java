package com.train4game.munoon.messageParser;

import com.train4game.munoon.models.Key;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@AllArgsConstructor
public class VoteParser implements MessageParser {
    private TelegramUserRepository telegramUserRepository;

    @Override
    public void parse(AbsSender sender, Message message) {
        Long chatId = message.getChatId();

        Key key = telegramUserRepository.getByUserId(chatId);
        key.setUsed(true);
        key.setUsedBy(chatId);

        telegramUserRepository.removeKey(chatId);
    }
}
