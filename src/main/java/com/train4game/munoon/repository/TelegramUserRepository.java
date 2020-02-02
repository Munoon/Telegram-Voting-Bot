package com.train4game.munoon.repository;

import com.train4game.munoon.models.Key;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TelegramUserRepository {
    private Map<Long, Key> storage; // <UserId, Key>

    public TelegramUserRepository() {
        storage = new HashMap<>();
    }

    public void addKey(long userId, Key key) {
        storage.put(userId, key);
    }

    public Key getByUserId(long userId) {
        return storage.get(userId);
    }

    public void removeKey(long userId) {
        storage.remove(userId);
    }

    public boolean containsConnectedKey(long userId) {
        return storage.containsKey(userId);
    }
}
