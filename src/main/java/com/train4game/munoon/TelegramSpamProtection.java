package com.train4game.munoon;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TelegramSpamProtection {
    private LoadingCache<Integer, Integer> attemptCache;

    public TelegramSpamProtection() {
        attemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS).build(new CacheLoader<>() {
                    @Override
                    public Integer load(Integer userId) throws Exception {
                        return 0;
                    }
                });
    }

    public void messageReceived(int userId) {
        int messages;
        try {
            messages = attemptCache.get(userId);
        } catch (ExecutionException e) {
            messages = 0;
        }
        attemptCache.put(userId, ++messages);
    }

    public boolean shouldProcess(int userId) {
        try {
            return attemptCache.get(userId) < 5;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
