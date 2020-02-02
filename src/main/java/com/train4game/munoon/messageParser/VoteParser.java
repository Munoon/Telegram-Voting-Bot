package com.train4game.munoon.messageParser;

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

@Slf4j
@Component
@AllArgsConstructor
public class VoteParser implements MessageParser {
    private TelegramUserRepository telegramUserRepository;
    private PlayersRepository playersRepository;

    @Override
    @SneakyThrows
    public void parse(AbsSender sender, Message message) {
        int userId = message.getFrom().getId();
        Player player = playersRepository.getPlayer(message.getText());
        player.addVote();

        Key key = telegramUserRepository.getByUserId(userId);

        if (key.isUsed()) {
            key.getVotedFor().removeVote();
            log.info("User in chat {} change vote to {}", message.getChatId(), player.getName());
        } else {
            log.info("User in chat {} voted for {}", message.getChatId(), player.getName());
        }

        key.setUsed(true);
        key.setUsedBy(userId);
        key.setVotedFor(player);

        telegramUserRepository.removeKey(userId);

        SendMessage sendMessage = new SendMessage(message.getChatId(), "Successful vote!")
                .setReplyMarkup(new ReplyKeyboardRemove());
        sender.execute(sendMessage);
    }
}
