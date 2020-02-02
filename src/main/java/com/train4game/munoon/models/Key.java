package com.train4game.munoon.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Key {
    private String key;

    private boolean used;

    private int usedBy;

    private Player votedFor;

    public Key(String key) {
        this.key = key;
    }
}
