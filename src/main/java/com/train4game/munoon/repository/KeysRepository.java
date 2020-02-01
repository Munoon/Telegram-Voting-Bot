package com.train4game.munoon.repository;

import com.train4game.munoon.models.Key;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Repository
public class KeysRepository {
    private List<Key> storage = new ArrayList<>();

    public KeysRepository() throws IOException {
        ClassPathResource resource = new ClassPathResource("keys.txt");
        InputStream inputStream = resource.getInputStream();
        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNext()) {
            Key key = new Key(scanner.nextLine());
            storage.add(key);
        }
    }

    public Key getKey(String key) {
        return storage.stream()
                .filter(item -> item.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }
}
