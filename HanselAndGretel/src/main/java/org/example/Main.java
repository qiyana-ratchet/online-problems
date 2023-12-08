package org.example;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Entry point of the game
        Game game = new Game("Henzel & Gretel Pacman", 640, 640 + 30);
        game.start();
    }
}
