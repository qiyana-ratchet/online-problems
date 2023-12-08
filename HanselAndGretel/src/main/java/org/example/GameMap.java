package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private Image mapImage;
    private int[][] mapTiles = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 0},
            {0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0},
            {0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0},
            {0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0},
            {0, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0},
            {0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0},
            {0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0},
            {0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    private List<Position> cookies = new ArrayList<>(); // List to store cookie positions
    private BufferedImage cookieImage; // Cookie image


    public GameMap(String mapImagePath) {
        // Load the map image from the provided path
        mapImage = Toolkit.getDefaultToolkit().getImage(mapImagePath);
        initializeCookies();

        try {
            cookieImage = ImageIO.read(new File("src/main/resources/cookie.png")); // Replace with your cookie image file path
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        // Draw the map image
//        g.drawImage(mapImage, 0, 30, null);
        g.drawImage(mapImage, 0, 0, null);
    }

    public void drawCookies(Graphics g) {
        for (Position cookiePosition : cookies) {
            int x = cookiePosition.x * 32; // Adjust for tile size
            int y = cookiePosition.y * 32; // Adjust for tile size

            if (cookieImage != null) {
                g.drawImage(cookieImage, x+5, y+7, null);
            }
        }
    }
    public int getTileAt(int x, int y) {
        // Retrieve the tile type at the given (x, y) position
        if (x >= 0 && x < mapTiles[0].length && y >= 0 && y < mapTiles.length) {
            return mapTiles[y][x];
        }
        return -1; // Invalid position
    }

    private void initializeCookies() {
        for (int y = 0; y < mapTiles.length; y++) {
            for (int x = 0; x < mapTiles[y].length; x++) {
                if (mapTiles[y][x] == 1) {
                    cookies.add(new Position(x, y));
                }
            }
        }
    }

    public List<Position> getCookies() {
        return cookies;
    }
}

