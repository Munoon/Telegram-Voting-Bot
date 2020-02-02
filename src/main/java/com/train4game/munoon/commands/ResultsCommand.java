package com.train4game.munoon.commands;

import com.train4game.munoon.TelegramMessages;
import com.train4game.munoon.models.Player;
import com.train4game.munoon.repository.PlayersRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Component
public class ResultsCommand extends BotCommand {
    private final Long adminChat;
    private TelegramMessages messages;
    private PlayersRepository playersRepository;

    public ResultsCommand(Environment environment, TelegramMessages messages, PlayersRepository playersRepository) {
        super("results", "Command to get results");
        this.messages = messages.createWrapper("commands.results");
        this.playersRepository = playersRepository;

        String adminChatStr = environment.getProperty("telegram.admin");
        this.adminChat = adminChatStr == null ? null : Long.parseLong(adminChatStr);
    }

    @Override
    @SneakyThrows
    public void execute(AbsSender sender, User user, Chat chat, String[] arguments) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chat.getId());

        if (adminChat == null || !adminChat.equals(chat.getId())) {
            log.info("Attempt to use results command not by admin in chat {}", chat.getId());
            sendMessage.setText(messages.getProperty("notAdmin"));
            sender.execute(sendMessage);
            return;
        }

        List<Player> players = playersRepository.getSimplePlayers();
        players.sort(Comparator.comparingInt(Player::getVotes).reversed());

        StringJoiner joiner = new StringJoiner("\n");
        for (Player player : players) {
            joiner.add(messages.getProperty("player", player.getName(), player.getVotes()));
        }

        log.info("Send players result to chat {}", chat.getId());
        sendMessage.setText(joiner.toString());
        sender.execute(sendMessage);
    }
}
