package com.train4game.munoon.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.train4game.munoon.models.Player;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class PlayersRepository {
    private List<List<Player>> storage;

    public PlayersRepository() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("players.json");
        InputStream inputStream = resource.getInputStream();

        List<List<String>> data = objectMapper.readValue(inputStream, new TypeReference<>() {});

        AtomicInteger idCounter = new AtomicInteger();
        storage = data.stream()
                .map(list -> list
                        .stream()
                        .map(playerName -> new Player(idCounter.incrementAndGet(), playerName))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public List<List<Player>> getPlayers() {
        return new ArrayList<>(storage);
    }

    public Player getPlayer(String name) {
        for (List<Player> players : storage) {
            for (Player player : players) {
                if (player.getName().equals(name)) {
                    return player;
                }
            }
        }
        return null;
    }

    public List<Player> getSimplePlayers() {
        List<Player> result = new ArrayList<>();
        storage.forEach(result::addAll);
        return result;
    }

    @PreDestroy
    public void writeResults() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
        List<Player> players = getSimplePlayers();
        players.sort(Comparator.comparingInt(Player::getVotes).reversed());

        for (Player player : players) {
            String message = player.getName() + " - " + player.getVotes() + " votes";
            System.out.println(message);
            writer.println(message);
        }

        writer.close();
    }
}
