// Monster.java
package WizardTD;

import processing.core.PImage;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import static processing.core.PApplet.dist;
import static processing.core.PApplet.println;

public class Monster {
    String type;
    int hp;
    float speed;
    float armour;
    int manaGainedOnKill;
    int quantity;
    float x;
    float y;


    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE + TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    // Add a field to store the monster's route
    private List<PVector> route = new ArrayList<>();

    // Getter and setter for the route field
    public List<PVector> getRoute() {
//        println("Got route: ", route);
        return route;
    }

    public void setRoute(List<PVector> route) {
        this.route = route;
    }

    Monster(String type, int hp, float speed, float armour, int manaGainedOnKill, int quantity,  float x, float y) {
        this.type = type;
        this.hp = hp;
        this.speed = speed;
        this.armour = armour;
        this.manaGainedOnKill = manaGainedOnKill;
        this.quantity = quantity;
        this.x = x; // X coordinate of the monster's position
        this.y = y; // Y coordinate of the monster's position
    }

//    public void moveMonster(char[][] map, float targetX, float targetY, Monster monster) {
//        // Determine the image file path based on the monster's type
//        String imageFilePath = "src/main/resources/WizardTD/" + monster.type + ".png";
//
//        // Load the monster's image from the specified file path
//        PImage monsterImage = loadImage(imageFilePath);
//
//        image(monsterImage, monster.x, monster.y);
//
//        while (true) {
//            try {
//                // Sleep for 2 seconds
//                Thread.sleep(300); // 2000 milliseconds = 2 seconds
//            } catch (InterruptedException e) {
//                // Handle any potential interruption exception
//            }
//            println("gogogo");
//            // Calculate the current cell position of the monster based on its coordinates
//            float x = monster.x;
//            float y = monster.y;
//            int currentCellX = (int) (x / CELLSIZE);
//            int currentCellY = (int) ((y - TOPBAR) / CELLSIZE);
//
//            // Check if the monster has reached the target (wizard's house)
//            if (currentCellX * CELLSIZE == targetX && (currentCellY * CELLSIZE + TOPBAR) == targetY) {
//                // Monster has reached the target, perform any necessary actions (e.g., damage wizard)
//                // Remove the monster from the game or update its status accordingly
//                return;
//            }
//
//            // Check if the current cell contains an 'X', indicating a path
//            if (map[currentCellY][currentCellX] == 'X') {
//                // Calculate the vector from the current position to the next 'X' position
//                float dx = (currentCellX + 1) * CELLSIZE - x;
//                float dy = (currentCellY * CELLSIZE + TOPBAR) - y;
//
//                // Calculate the distance to the next 'X' position
//                float distance = dist(x, y, (currentCellX + 1) * CELLSIZE, currentCellY * CELLSIZE + TOPBAR);
//
//                // Normalize the direction vector
//                float directionX = dx / distance;
//                float directionY = dy / distance;
//
//                // Calculate the movement increment
//                float moveX = directionX * monster.speed;
//                float moveY = directionY * monster.speed;
//
//                // Update the monster's position
//                x += moveX;
//                y += moveY;
//            }
//
//
//
//            image(monsterImage, monster.x, monster.y);
//        }
//    }
}