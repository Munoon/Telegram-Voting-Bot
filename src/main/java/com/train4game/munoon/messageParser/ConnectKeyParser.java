package com.train4game.munoon.messageParser;

import com.train4game.munoon.models.Key;
import com.train4game.munoon.models.Player;
import com.train4game.munoon.repository.KeysRepository;
import com.train4game.munoon.repository.PlayersRepository;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ConnectKeyParser implements MessageParser {
    private TelegramUserRepository telegramUserRepository;
    private KeysRepository keysRepository;
    private PlayersRepository playersRepository;

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
        sendMessage.setReplyMarkup(getPlayersButtons());
        sender.execute(sendMessage);
    }

    private ReplyKeyboardMarkup getPlayersButtons() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new ArrayList<>());
        List<List<Player>> playersArray = playersRepository.getPlayers();

        for (List<Player> players : playersArray) {
            KeyboardRow row = new KeyboardRow();
            players.forEach(player -> row.add(new KeyboardButton(player.getName())));
            markup.getKeyboard().add(row);
        }

        return markup;
    }
}
