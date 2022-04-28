package ru.demmax93.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public float health;

    public PlayerComponent() {
        health = 100;
    }
}
