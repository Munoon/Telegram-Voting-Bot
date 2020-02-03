package com.train4game.munoon.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private final int id;

    private final String name;

    private int votes;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addVote() {
        ++votes;
    }

    public void removeVote() {
        --votes;
    }
}
