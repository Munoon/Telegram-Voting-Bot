package com.train4game.munoon.messageParser;

import com.train4game.munoon.TelegramMessages;
import com.train4game.munoon.models.Key;
import com.train4game.munoon.models.Player;
import com.train4game.munoon.repository.PlayersRepository;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.bots.AbsSender;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@AllArgsConstructor
public class VoteParser implements MessageParser {
    private TelegramUserRepository telegramUserRepository;
    private PlayersRepository playersRepository;
    private TelegramMessages messages;

    @PostConstruct
    public void setupMessages() {
        messages = messages.createWrapper("messages.vote");
    }

    @Override
    @SneakyThrows
    public void parse(AbsSender sender, Message message) {
        int userId = message.getFrom().getId();
        Player player = playersRepository.getPlayer(message.getText());
        player.addVote();

        Key key = telegramUserRepository.getByUserId(userId);

        SendMessage sendMessage = new SendMessage()
                .setChatId(message.getChatId())
                .setReplyMarkup(new ReplyKeyboardRemove());

        if (key.isUsed()) {
            key.getVotedFor().removeVote();
            sendMessage.setText(messages.getProperty("changeVote", player.getName()));
            log.info("User in chat {} change vote to {} using key '{}'", message.getChatId(), player.getName(), key.getKey());
        } else {
            sendMessage.setText(messages.getProperty("success", player.getName()));
            log.info("User in chat {} voted for {} using key '{}'", message.getChatId(), player.getName(), key.getKey());
        }

        key.setUsed(true);
        key.setUsedBy(userId);
        key.setVotedFor(player);

        telegramUserRepository.removeKey(userId);

        sender.execute(sendMessage);
    }
}
