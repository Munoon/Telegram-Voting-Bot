package com.train4game.munoon.messageParser;

import com.train4game.munoon.models.Key;
import com.train4game.munoon.repository.KeysRepository;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@AllArgsConstructor
public class ConnectKeyParser implements MessageParser {
    private TelegramUserRepository telegramUserRepository;
    protected KeysRepository keysRepository;

    @Override
    @SneakyThrows
    public void parse(AbsSender sender, Message message) {
        Long chatId = message.getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        Key key = keysRepository.getKey(message.getText());

        if (key == null) {
            sendMessage.setText("Key not found!");
            sender.execute(sendMessage);
            return;
        }

        if (key.isUsed() && !key.getUsedBy().equals(chatId)) {
            sendMessage.setText("Key already used!");
            sender.execute(sendMessage);
            return;
        }

        telegramUserRepository.addKey(chatId, key);
        sendMessage.setText("Success! Make vote!");
        sender.execute(sendMessage);
    }
}
