package com.train4game.munoon.repository;

import com.train4game.munoon.models.Key;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TelegramUserRepository {
    private Map<Long, Key> storage; // <ChatId, Key>

    public TelegramUserRepository() {
        storage = new HashMap<>();
    }

    public void addKey(long chatId, Key key) {
        storage.put(chatId, key);
    }

    public Key getByUserId(long chatId) {
        return storage.get(chatId);
    }

    public void removeKey(long chatId) {
        storage.remove(chatId);
    }

    public boolean containsConnectedKey(long chatId) {
        return storage.containsKey(chatId);
    }
}
