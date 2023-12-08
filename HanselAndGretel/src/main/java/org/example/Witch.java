package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Witch {
    private int x, y; // Witch's current position
    private int dx, dy; // Movement speed
    private int targetX, targetY; // Player's position (target)
    private List<Position> route; // List to store the route from Witch to player

    private BufferedImage witchImage; // Witch's image
    private int witchSpeed = 3;

    public Witch(int initialX, int initialY) {
        x = initialX;
        y = initialY;
        dx = 3;
        dy = 3;

        try {
            witchImage = ImageIO.read(new File("src/main/resources/witch.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        if (witchImage != null) {
            g.drawImage(witchImage, x - 10, y - 28, null);
        }
    }

    public void update() {
        if (route != null && !route.isEmpty()) {
//            for (Position el : route) {
//                System.out.println(el.x + " " + el.y);
//            }
            System.out.println(route.get(0).x + " " + route.get(0).y);

            System.out.println("Following the route...");
            Position nextPosition = route.get(0);
            int dx = nextPosition.x * 32 - x;
            int dy = nextPosition.y * 32 - y;

            System.out.println("nx, x, ny, y: " + nextPosition.x + " " + x + " " + nextPosition.y + " " + y);
            System.out.println("dx, dy: " + dx + " " + dy);

            if (dx == 0 && dy == 0) {
                route.remove(0);
                return;
            }

            // Calculate the distance to the next position
            double distance = Math.sqrt(dx * dx + dy * dy);

            System.out.println("distance " + distance);

            // Normalize the direction vector
            double directionX = dx / distance;
            double directionY = dy / distance;
            System.out.println("dirX, dirY: " + directionX + " " + directionY);

            // Adjust Witch's position based on movement speed
//            if (!Double.isNaN(directionX) && !Double.isNaN(directionY)) {
            x += (int) (directionX * witchSpeed);
            y += (int) (directionY * witchSpeed);
//            }
            System.out.println("Moving witch to x,y: " + x + " " + y);

            // Remove the visited position from the route if the Witch has reached it
            if (Math.abs(x/32 - nextPosition.x) < witchSpeed &&
                    Math.abs(y/32 - nextPosition.y) < witchSpeed) {
                route.remove(0);
            }

        }
    }

    public void trackPlayer(GameMap gameMap, Player player) {
        if (route == null || route.isEmpty()) {
            System.out.println("Tracking player...");

            targetX = player.getX() / 32;
            targetY = player.getY() / 32;

            System.out.println(targetX + " / " + targetY + " / " + x + " / " + y);

            Queue<Position> queue = new LinkedList<>();
            Set<Position> visited = new HashSet<>();
            Map<Position, Position> parentMap = new HashMap<>(); // Map to store parent positions

            // Initialize with Witch's current position
            int witchX = x / 32;
            int witchY = y / 32;
            Position initialPosition = new Position(witchX, witchY);
            queue.add(initialPosition);
            visited.add(initialPosition);
            parentMap.put(initialPosition, null);

            while (!queue.isEmpty()) {
//                System.out.println(queue);
                Position currentPosition = queue.poll();

                // Check if player's position is reached
                if (currentPosition.x == targetX && currentPosition.y == targetY) {
                    // Store the route from Witch to player
                    route = new ArrayList<>();
                    Position backtrackPosition = currentPosition;
                    while (backtrackPosition != null) {
                        route.add(backtrackPosition);
                        backtrackPosition = parentMap.get(backtrackPosition);
                    }
                    Collections.reverse(route);

                    System.out.println("Route found. Route: ");
                    for (Position el : route) {
                        System.out.println(el.x + " " + el.y);
                    }

                    System.out.println("Queue had been: ");
                    for (Position el : queue){
                        System.out.println(el.x + " " + el.y);
                    }

                    break;
                }

                // Explore neighboring positions
                for (Position neighbor : getValidNeighbors(currentPosition, gameMap)) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                        parentMap.put(neighbor, currentPosition);
                    }
                }
            }
        }
    }



    //     Implement a method to get valid neighboring positions for the Witch to move to
    private List<Position> getValidNeighbors(Position position, GameMap gameMap) {
        List<Position> neighbors = new ArrayList<>();

        int tileSize = 32;
        int x = position.x;
        int y = position.y;

        // Define possible movement directions
        int[] dx = {-1, 1, 0, 0}; // Left, Right, Up, Down
        int[] dy = {0, 0, -1, 1};

        // Check each direction for valid neighbors
        for (int i = 0; i < 4; i++) {
//            int newX = x + dx[i] * tileSize; // Adjust for tile size
//            int newY = y + dy[i] * tileSize; // Adjust for tile size
            int newX = x + dx[i]; // Adjust for tile size
            int newY = y + dy[i]; // Adjust for tile size

            // Check if the new position is within the game map boundaries
            if (newX >= 0 && newX < 20 && newY >= 0 && newY < 20) {
//                int tileX = newX / tileSize;
//                int tileY = newY / tileSize;
                int tileX = newX;
                int tileY = newY;

                // Check if the tile at the new position is not a wall (0)
                if (gameMap.getTileAt(tileX, tileY) != 0) {
                    neighbors.add(new Position(newX, newY));
                }
            }
        }

        return neighbors;
    }
}
