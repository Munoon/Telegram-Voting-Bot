package com.train4game.munoon.messageParser;

import com.train4game.munoon.TelegramMessages;
import com.train4game.munoon.models.Key;
import com.train4game.munoon.models.Player;
import com.train4game.munoon.repository.KeysRepository;
import com.train4game.munoon.repository.PlayersRepository;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ConnectKeyParser implements MessageParser {
    private ReplyKeyboardMarkup playersButtons;
    private TelegramUserRepository telegramUserRepository;
    private KeysRepository keysRepository;
    private PlayersRepository playersRepository;
    private TelegramMessages telegramMessages;

    public ConnectKeyParser(TelegramUserRepository telegramUserRepository, KeysRepository keysRepository, PlayersRepository playersRepository, TelegramMessages telegramMessages) {
        this.telegramUserRepository = telegramUserRepository;
        this.keysRepository = keysRepository;
        this.playersRepository = playersRepository;
        this.telegramMessages = telegramMessages.createWrapper("messages.connectKey");
    }

    @Override
    @SneakyThrows
    public void parse(AbsSender sender, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        Integer userId = message.getFrom().getId();

        Key key = keysRepository.getKey(message.getText());

        if (key == null) {
            log.info("Not found key '{}' in chat {}", message.getText(), message.getChatId());
            sendMessage.setText(telegramMessages.getProperty("notFound"));
            sender.execute(sendMessage);
            return;
        }

        if (key.isUsed() && key.getUsedBy() != userId) {
            log.info("Key '{}' already used by other user, chat {}", key.getKey(), message.getChatId());
            sendMessage.setText(telegramMessages.getProperty("alreadyUsed"));
            sender.execute(sendMessage);
            return;
        }

        log.info("Ask user to make vote in chat {}, key '{}'", message.getChatId(), key.getKey());
        telegramUserRepository.addKey(userId, key);
        sendMessage.setText(telegramMessages.getProperty("makeVote"));
        sendMessage.setReplyMarkup(playersButtons);
        sender.execute(sendMessage);
    }

    @PostConstruct
    private void initializePlayersButtons() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(new ArrayList<>());
        List<List<Player>> playersArray = playersRepository.getPlayers();

        for (List<Player> players : playersArray) {
            KeyboardRow row = new KeyboardRow();
            players.forEach(player -> row.add(new KeyboardButton(player.getName())));
            markup.getKeyboard().add(row);
        }

        playersButtons = markup;
    }
}
