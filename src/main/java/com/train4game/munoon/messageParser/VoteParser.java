package com.train4game.munoon.messageParser;

import com.train4game.munoon.models.Key;
import com.train4game.munoon.models.Player;
import com.train4game.munoon.repository.PlayersRepository;
import com.train4game.munoon.repository.TelegramUserRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
@AllArgsConstructor
public class VoteParser implements MessageParser {
    private TelegramUserRepository telegramUserRepository;
    private PlayersRepository playersRepository;

    @Override
    @SneakyThrows
    public void parse(AbsSender sender, Message message) {
        Long chatId = message.getChatId();
        Player player = playersRepository.getPlayer(message.getText());
        player.addVote();

        Key key = telegramUserRepository.getByUserId(chatId);

        if (key.isUsed()) {
            key.getVotedFor().removeVote();
        }

        key.setUsed(true);
        key.setUsedBy(chatId);
        key.setVotedFor(player);

        telegramUserRepository.removeKey(chatId);

        SendMessage sendMessage = new SendMessage(message.getChatId(), "Successful vote!")
                .setReplyMarkup(new ReplyKeyboardRemove());
        sender.execute(sendMessage);
    }
}
