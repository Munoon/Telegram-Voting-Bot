package com.train4game.munoon.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private int id;

    private String name;

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
