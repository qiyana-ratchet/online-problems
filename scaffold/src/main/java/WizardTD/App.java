package WizardTD;

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

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE * BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH * CELLSIZE + TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();

    // Define image variables for tiles, towers, and enemies
    private PImage grassImg, shrubImg, pathImg, towerImg, wizardHouseImg;
    private PImage[] gremlinImgs = new PImage[6]; // Gremlin has multiple images for animation
    private PImage fireballImg;

    float WizardX = 0;
    float WizardY = 0;
    int WizardXIndex = 0;
    int WizardYIndex = 0;

    char[][] mapLayout;

    // Configuration variables
    private String layoutFileName;
    private Wave[] waves;
    private int initialTowerRange;
    private float initialTowerFiringSpeed;
    private int initialTowerDamage;
    private int initialMana;
    private int initialManaCap;
    private int initialManaGainedPerSecond;
    private int towerCost;
    private int manaPoolSpellInitialCost;
    private int manaPoolSpellCostIncreasePerUse;
    private float manaPoolSpellCapMultiplier;
    private float manaPoolSpellManaGainedMultiplier;
    ArrayList<PVector> spawnLocations = new ArrayList<>(); // monster spawn location
    ArrayList<PVector> spawnLocationsIndex = new ArrayList<>(); // monster spawn location

    // Add this field to your App class
    private int currentWaveIndex = 0; // Initialize to 0 to indicate no waves have started yet
    private float timeSinceLastWaveStart = 0;

    List<Monster> monsters = new ArrayList<>();

    // Add this field to your App class
    private boolean isWaveActive = false; // Initialize to false to indicate no wave is active
    boolean canStart = false; // Variable for indicating that you can start the wave

    PImage path0Img;
    PImage path1Img;
    PImage path2Img;
    PImage path3Img;

    // Define constants for directions (up, down, left, right)
    static final int[] dx = {-1, 1, 0, 0};
    static final int[] dy = {0, 0, -1, 1};

    long lastSpawnTime = 0;
    boolean isSpawningAllowed = false;
     long spawningStartTime = 0;

    /**
     * Starting the App
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */


    @Override
    public void setup() {
        frameRate(FPS);
        readConfig();

        // Set the resource path
        String resourcePath = "src/main/resources/WizardTD/";

        // Load images during setup
        grassImg = loadImage(resourcePath + "grass.png");
        shrubImg = loadImage(resourcePath + "shrub.png");
        towerImg = loadImage(resourcePath + "tower0.png"); // You can change this based on the tower type
        wizardHouseImg = loadImage(resourcePath + "wizard_house.png");

        // Load path tile images
        path0Img = loadImage(resourcePath + "path0.png");
        path1Img = loadImage(resourcePath + "path1.png");
        path2Img = loadImage(resourcePath + "path2.png");
        path3Img = loadImage(resourcePath + "path3.png");

        // Load gremlin animation frames
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                gremlinImgs[i] = loadImage(resourcePath + "gremlin.png"); // Use "gremlin.png" for the first image
            } else {
                gremlinImgs[i] = loadImage(resourcePath + "gremlin" + i + ".png");
            }
        }

        fireballImg = loadImage(resourcePath + "fireball.png");

        // Read the map layout into a 2D array
        mapLayout = new char[BOARD_WIDTH][BOARD_WIDTH];

        try {
            BufferedReader reader = new BufferedReader(new FileReader("level1.txt"));
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null && row < BOARD_WIDTH) {
                for (int col = 0; col < line.length() && col < BOARD_WIDTH; col++) {
                    mapLayout[row][col] = line.charAt(col);

                    // Check if this location is a spawn location (border of 'X')
                    if (line.charAt(col) == 'X' &&
                            (row == 0 || row == BOARD_WIDTH - 1 || col == 0 || col == BOARD_WIDTH - 1)) {
                        // Store the spawn location as a PVector
//                        println("COORDS: ",col, row);
                        spawnLocations.add(new PVector(col * CELLSIZE, row * CELLSIZE + TOPBAR));
                        spawnLocationsIndex.add(new PVector(col , row));
                    }
                }
                row++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up the game board based on the mapLayout array
        background(0); // Set background color

        // Call the drawMap function to draw the map
        drawMap();

        // Iterate through the 2D array and print each character
        println("-----Map Layout-----");
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                char tileChar = mapLayout[row][col];
                System.out.print(tileChar + " "); // Print the character followed by a space
            }
            System.out.println(); // Move to the next line after each row
        }

        // FINDPATH
//        println(spawnLocations.get(0).x, spawnLocations.get(0).y);
//        findPath(mapLayout, spawnLocations.get(0), new PVector(WizardXIndex, WizardYIndex));
    }

    Stack<PVector> route;
    public boolean findPath(char[][] mapLayout, PVector spawnLocation, PVector wizardHouse) {
        Queue<PVector> queue = new LinkedList<>();
        boolean[][] visited = new boolean[mapLayout.length][mapLayout[0].length];
        PVector[][] parent = new PVector[mapLayout.length][mapLayout[0].length];

//        queue.add(spawnLocation);
//        visited[(int) spawnLocation.y][(int) spawnLocation.x] = true;
//        col * CELLSIZE
//        row * CELLSIZE + TOPBAR
        int ix = (int) spawnLocation.x / CELLSIZE;
        int iy = (int) (spawnLocation.y - TOPBAR) / CELLSIZE;
        queue.add(new PVector(ix, iy));
        visited[iy][ix] =true;

        while (!queue.isEmpty()) {
            PVector current = queue.poll();
//            println("current: ", current, "wizardHouse", wizardHouse);
            // Check if the current position is the wizard's house
            if (current.equals(wizardHouse)) {
                // Path found, backtrack to construct the route
                route = new Stack<>();
                PVector trace = current;

                while (trace != null) {
                    route.push(trace);
                    trace = parent[(int) trace.y][(int) trace.x];
//                    println("route: ", route);
                }
                Collections.reverse(route);

                // Print the route
//                System.out.println("Route from spawn to wizard's house:");
//                while (!route.isEmpty()) {
//                    PVector step = route.pop();
//                    System.out.println("X: " + step.x + ", Y: " + step.y);
//                }
//                println("route: ", route);

                return true;
            }

            // Explore neighboring positions
            for (int i = 0; i < 4; i++) {
                int newX = (int) current.x + dx[i];
                int newY = (int) current.y + dy[i];

                // Check if the new position is within bounds and is an X tile
                if (newX >= 0 && newX < mapLayout[0].length && newY >= 0 && newY < mapLayout.length &&
                        ( mapLayout[newY][newX] == 'X' || mapLayout[newY][newX] == 'W' ) && !visited[newY][newX]) {
                    queue.add(new PVector(newX, newY));
                    visited[newY][newX] = true;
                    parent[newY][newX] = current; // add current path to parent
                }
//                else{
//                    println("Conditions do not match");
//                }
            }
        }
        println("No path were found. ");
        return false; // No path found
    }

    private void drawMap() {
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                char tileChar = mapLayout[row][col];
                PImage tileImage = null;

                // Determine the tile type based on the neighboring tiles
                int tileType;
                if (tileChar == 'S') {
                    // Default to shrub
                    tileImage = shrubImg;
                } else if (tileChar == 'X') {
                    // Check neighboring tiles
                    boolean leftPath = col > 0 && mapLayout[row][col - 1] == 'X';
                    boolean rightPath = col < BOARD_WIDTH - 1 && mapLayout[row][col + 1] == 'X';
                    boolean abovePath = row > 0 && mapLayout[row - 1][col] == 'X';
                    boolean belowPath = row < BOARD_WIDTH - 1 && mapLayout[row + 1][col] == 'X';

                    // Determine the tile type based on neighbors
                    if (leftPath && rightPath && abovePath && belowPath) {
                        tileImage = path3Img;
                    } else if (leftPath && rightPath && belowPath) {
                        tileImage = path2Img;
                    } else if (leftPath && belowPath && abovePath) {
                        tileImage = rotateImageByDegrees(path2Img, 90);
                    } else if (leftPath && rightPath && abovePath) {
                        tileImage = rotateImageByDegrees(path2Img, 180);
                    } else if (belowPath && rightPath && abovePath) {
                        tileImage = rotateImageByDegrees(path2Img, 270);
                    } else if (leftPath && rightPath) {
                        tileImage = path0Img;
                    } else if (abovePath && belowPath) {
                        tileImage = rotateImageByDegrees(path0Img, 90);
                    } else if (leftPath && belowPath) {
                        tileImage = path1Img;
                    } else if (leftPath && abovePath) {
                        tileImage = rotateImageByDegrees(path1Img, 90);
                    } else if (rightPath && abovePath) {
                        tileImage = rotateImageByDegrees(path1Img, 180);
                    } else if (rightPath && belowPath) {
                        tileImage = rotateImageByDegrees(path1Img, 270);
                    } else if (rightPath || leftPath) {
                        tileImage = path0Img;
                    } else if (abovePath || belowPath) {
                        tileImage = rotateImageByDegrees(path0Img, 90);
                    } else {
                        tileImage = towerImg;
                    }
                } else if (tileChar == 'W') {
                    // Calculate the position to center the wizard's house within its tile
                    WizardXIndex = col;
                    WizardYIndex = row;
                    float wizardHouseX = col * CELLSIZE + (CELLSIZE - 48) / 2;
                    float wizardHouseY = row * CELLSIZE + TOPBAR + (CELLSIZE - 48) / 2;
                    WizardX = wizardHouseX;
                    WizardY = wizardHouseY;

                } else {
                    // Default to grass
                    tileImage = grassImg;
                }
                tileImage = (tileImage != null) ? tileImage : path0Img;
                image(tileImage, col * CELLSIZE, row * CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
            }
        }
        // Render the wizard's house
        // image(wizardHouseImg, wizardHouseX, wizardHouseY, 48, 48);
        image(wizardHouseImg, WizardX, WizardY, 48, 48);
    }


    // Load JSON configuration file
    public void readConfig() {
        try {
            // Load the JSON configuration file
            JSONObject config = loadJSONObject(configPath);

            // Read configuration data and save it to class fields
            layoutFileName = config.getString("layout");
            initialTowerRange = config.getInt("initial_tower_range");
            initialTowerFiringSpeed = config.getFloat("initial_tower_firing_speed");
            initialTowerDamage = config.getInt("initial_tower_damage");
            initialMana = config.getInt("initial_mana");
            initialManaCap = config.getInt("initial_mana_cap");
            initialManaGainedPerSecond = config.getInt("initial_mana_gained_per_second");
            towerCost = config.getInt("tower_cost");
            manaPoolSpellInitialCost = config.getInt("mana_pool_spell_initial_cost");
            manaPoolSpellCostIncreasePerUse = config.getInt("mana_pool_spell_cost_increase_per_use");
            manaPoolSpellCapMultiplier = config.getFloat("mana_pool_spell_cap_multiplier");
            manaPoolSpellManaGainedMultiplier = config.getFloat("mana_pool_spell_mana_gained_multiplier");

            // Read waves data
            JSONArray wavesArray = config.getJSONArray("waves");
            waves = new Wave[wavesArray.size()];
            for (int i = 0; i < wavesArray.size(); i++) {
                JSONObject waveObject = wavesArray.getJSONObject(i);
                waves[i] = new Wave(
                        waveObject.getInt("duration"),
                        waveObject.getFloat("pre_wave_pause"),
                        waveObject.getJSONArray("monsters")
                );
            }
            //check if values are correct
//            println("layoutFileName: " + layoutFileName);
//            println("initialTowerRange: " + initialTowerRange);
//            println("initialTowerFiringSpeed: " + initialTowerFiringSpeed);
//            println("initialTowerDamage: " + initialTowerDamage);
//            println("initialMana: " + initialMana);
//            println("initialManaCap: " + initialManaCap);
//            println("initialManaGainedPerSecond: " + initialManaGainedPerSecond);
//            println("towerCost: " + towerCost);
//            println("manaPoolSpellInitialCost: " + manaPoolSpellInitialCost);
//            println("manaPoolSpellCostIncreasePerUse: " + manaPoolSpellCostIncreasePerUse);
//            println("manaPoolSpellCapMultiplier: " + manaPoolSpellCapMultiplier);
//            println("manaPoolSpellManaGainedMultiplier: " + manaPoolSpellManaGainedMultiplier);
//            println("waves: " + waves);
//            println("waves.length: " + waves.length);
//            println("waves[0].duration: " + waves[0].duration);
//            println("waves[0].preWavePause: " + waves[0].preWavePause);
//            println("waves[0].monsters: " + waves[0].monsters);
//            println("waves[0].monsters.length: " + waves[0].monsters.length);
//            println("waves[0].monsters[0].type: " + waves[0].monsters[0].type);
//            println("waves[0].monsters[0].hp: " + waves[0].monsters[0].hp);
//            println("waves[0].monsters[0].speed: " + waves[0].monsters[0].speed);
//            println("waves[0].monsters[0].armour: " + waves[0].monsters[0].armour);
//            println("waves[0].monsters[0].manaGainedOnKill: " + waves[0].monsters[0].manaGainedOnKill);
//            println("waves[0].monsters[0].quantity: " + waves[0].monsters[0].quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed() {

    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * Draw all elements in the game by current frame.
     */

    int monstersSpawned = 0;
    @Override
    public void draw() {
        float timeUntilNextWave = calculateTimeUntilNextWave();
        // Calculate the time until the next wave begins
//        println("-------Log-------");
//        println("Time until next wave: " + timeUntilNextWave + " seconds");

        // Display the time in the top left corner of the GUI
        fill(0); // Set the fill color to black
        rect(0, 0, 250, 38); // Draw a black rectangle for the timer region

        // Display the time in the top left corner of the GUI
        fill(255);
        textSize(16);
        textAlign(LEFT, TOP);

        if (currentWaveIndex < waves.length) {
            println("canStart: ", canStart);
            if (timeUntilNextWave > 0) {
                int timeUntilNextWaveInt = floor(timeUntilNextWave);
                if (!isWaveActive) {
                    // Display time until wave start
                    text("Wave " + (currentWaveIndex + 1) + " starts in: " + timeUntilNextWaveInt + "s", 10, 10);
                } else {
                    if (canStart) {
                        println("######################YES WE STARTING NEW WAVE######################");
                        startNextWave(currentWaveIndex);
                    }
                    // Display wave duration
                    text("Wave " + (currentWaveIndex + 1) + " in progress: " + nf(timeUntilNextWave, 0, 1) + "s", 10, 10);
                }
            }
        } else {
            // All waves are completed
            text("All waves completed!", 10, 10);
        }

        Wave currentWave = waves[currentWaveIndex];
//        int totalMonstersToSpawn = currentWave.monsters[0].quantity;
        int totalMonstersToSpawn = 2;
        println("totalMonstersSpawn", totalMonstersToSpawn);
        println(currentWave.monsters);
        int spawnInterval = (int) (currentWave.duration * 1000) / totalMonstersToSpawn;

        // Calculate the time elapsed since the last monster spawn
        long currentTime = millis();
        long elapsedTime = currentTime - lastSpawnTime;
        println(isSpawningAllowed);
        if (isSpawningAllowed && monstersSpawned < totalMonstersToSpawn){
            println("GOGOGO1");
            // Check if it's time to spawn a monster
            println("elapsedTime", elapsedTime, "spawnInterval", spawnInterval);
            float elapsedTimeSinceSpawningStart = currentTime - spawningStartTime;
            if (elapsedTimeSinceSpawningStart >= currentWave.duration*1000) {
                println("turning isSpawn off");
                // Spawning duration has passed, turn off spawning
                isSpawningAllowed = false;
            }

            if (elapsedTime >= spawnInterval) {
                println("GOGOGO2");
                // Spawn a monster here
                // Update lastSpawnTime to the current time
                spawnMonster(currentWave.monsters[0]);
                monstersSpawned++;
                lastSpawnTime = currentTime;
            }
        }

        // Clear moving traces by overdrawing map
        drawMap();

        // Moving monsters
//        for (Monster monster : monsters) {
//            moveMonster(route, monster);
//        }
    }

    // Modify startNextWave() to accept a wave index parameter
    private float calculateTimeUntilNextWave() {
        if (currentWaveIndex >= 0 && currentWaveIndex < waves.length) {
            float currentTime = millis() / 1000.0f; // Convert millis to seconds
            float timeElapsedSinceLastWave = currentTime - timeSinceLastWaveStart; // Time since a single wave start
            float preWavePause = waves[currentWaveIndex].preWavePause; // Pre wave pause time

            println("------------");
            println("Wave: ", currentWaveIndex);
            println("PreWavePause: ", preWavePause);
            println("timeElapsedSinceLastWave", timeElapsedSinceLastWave);
            println("Duration: ", waves[currentWaveIndex].duration);

            if (timeElapsedSinceLastWave < preWavePause) {
                // Wave hasn't started yet, return time until wave start
                println("Wave hasn't started yet");
                isWaveActive = false;
            } else if (timeElapsedSinceLastWave < preWavePause + waves[currentWaveIndex].duration) {
                // Wave is active, return the wave duration
                println("Wave is active");
                isWaveActive = true;
            }
            println("isWaveActive: ", isWaveActive);

            if (!isWaveActive) {
                canStart = true;
                return max(preWavePause - timeElapsedSinceLastWave, 0);
            } else {
                return waves[currentWaveIndex].duration - (timeElapsedSinceLastWave - preWavePause);
            }
        }

        return 0;
    }

    void startNextWave(int waveIndex) {
        canStart = false;
        isSpawningAllowed = true;
        spawningStartTime = (long) (millis() / 1000.0);
        println("CANSTART CHANGED TO FALSE!!!!!!!");
        // Increment the current wave index
        currentWaveIndex++;
        println(currentWaveIndex);

        if (waveIndex < waves.length) {

            // Set the time since last wave start to the current time
            timeSinceLastWaveStart = millis() / 1000.0f;

            // Implement logic to start the wave here
            Wave currentWave = waves[waveIndex];

            println("Starting wave " + (waveIndex + 1) + " with " + currentWave.monsters[0].quantity + " monsters");
            println("Wave duration: " + currentWave.duration);
            println("Pre-wave pause: " + currentWave.preWavePause);
            println("Monsters: " + currentWave.monsters.length);
            println("Monster 1 type: " + currentWave.monsters[0].type);

//            // Spawn monsters based on the current wave's configuration
//            for (Monster monster : currentWave.monsters) {
//                for (int i = 0; i < monster.quantity; i++) {
//                    spawnMonster(monster); // Call a function to spawn monsters
//                }
//            }

        }
    }

    void spawnMonster(Monster monster) {
//        println("SPAWNING@@@@");
        // Choose a random spawn location from the spawnLocations list
        int spawnIndex = (int) random(spawnLocations.size());
        PVector spawnLocation = spawnLocations.get(spawnIndex);
        PVector spawnLocationIndex = spawnLocationsIndex.get(spawnIndex);

        findPath(mapLayout, spawnLocations.get(spawnIndex), new PVector(WizardXIndex, WizardYIndex));


        // Set the monster's spawn location (X and Y coordinates)
        monster.x = spawnLocation.x;
        monster.y = spawnLocation.y;

        // Determine the image file path based on the monster's type
        String imageFilePath = "src/main/resources/WizardTD/" + monster.type + ".png";

        // Load the monster's image from the specified file path
        PImage monsterImage = loadImage(imageFilePath);

        // Draw the monster at its spawn location
        image(monsterImage, monster.x, monster.y);
        println("Monster Drawn");


        // Add the monster to your game's data structure (monsters list)
        monsters.add(monster);
    }

    public void moveMonster(List<PVector> route, Monster monster) {
        println(route);
        // Check if there are waypoints left in the route
        if (!route.isEmpty()) {
            // Get the next waypoint
            PVector nextWaypoint = route.get(0);

            // Calculate the actual position of the waypoint based on cell size
            float nextWaypointX = nextWaypoint.x * CELLSIZE + (CELLSIZE - 48) / 2;
            float nextWaypointY = nextWaypoint.y * CELLSIZE + TOPBAR + (CELLSIZE - 48) / 2;

            // Calculate the direction vector from the current position to the next waypoint
            float dx = nextWaypointX - monster.x;
            float dy = nextWaypointY - monster.y;

            // Calculate the distance to the next waypoint
            float distance = dist(monster.x, monster.y, nextWaypointX, nextWaypointY);

            // Normalize the direction vector
            float directionX = dx / distance;
            float directionY = dy / distance;

            // Calculate the movement increment based on the monster's speed
            float moveX = directionX * monster.speed;
            float moveY = directionY * monster.speed;

            // Update the monster's position
            monster.x += moveX;
            monster.y += moveY;

            // Check if the monster has reached the next waypoint
            if (distance < monster.speed) {
                // Remove the reached waypoint from the route
                route.remove(0);
            }

            // Load and display the monster's image at the updated position
            String imageFilePath = "src/main/resources/WizardTD/" + monster.type + ".png";
            PImage monsterImage = loadImage(imageFilePath);
            image(monsterImage, monster.x, monster.y);
        }
    }




    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     *
     * @param pimg  The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, ARGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
