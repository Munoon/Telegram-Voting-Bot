package com.train4game.munoon;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TelegramSpamProtection {
    private LoadingCache<Long, Integer> attemptCache;

    public TelegramSpamProtection() {
        attemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS).build(new CacheLoader<>() {
                    @Override
                    public Integer load(Long aLong) throws Exception {
                        return 0;
                    }
                });
    }

    public void messageReceived(long chatId) {
        int messages;
        try {
            messages = attemptCache.get(chatId);
        } catch (ExecutionException e) {
            messages = 0;
        }
        attemptCache.put(chatId, ++messages);
    }

    public boolean shouldProcess(long chatId) {
        try {
            return attemptCache.get(chatId) < 5;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
