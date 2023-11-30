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
    private PImage[] gremlinImgs = new PImage[6]; // Multiple images for animation
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

    private int currentWaveIndex = 0; // no waves have started yet
    private float timeSinceLastWaveStart = 0;

    List<Monster> monstersInMap = new ArrayList<>();

    private boolean isWaveActive = false; // no wave is active
    boolean canStart = false; // you can start the wave

    PImage path0Img;
    PImage path1Img;
    PImage path2Img;
    PImage path3Img;

    // Define directions (up, down, left, right)
    static final int[] dx = {-1, 1, 0, 0};
    static final int[] dy = {0, 0, -1, 1};

    long lastSpawnTime = 0;
    boolean isSpawningAllowed = false;
    float spawningStartTime = 0;
    float timeElapsedSinceLastWave;

    // variable to track the Tower button state
    boolean isTowerButtonActive = false;
    private int buttonSpacing;
    private float buttonStartX;
    private float buttonStartY;
    private float buttonOriginStartX;
    private float buttonOriginStartY;
    private int buttonSize;

    ArrayList<PVector> towerPositions = new ArrayList<PVector>();


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
        towerImg = loadImage(resourcePath + "tower0.png");
        wizardHouseImg = loadImage(resourcePath + "wizard_house.png");

        // Load path tile images
        path0Img = loadImage(resourcePath + "path0.png");
        path1Img = loadImage(resourcePath + "path1.png");
        path2Img = loadImage(resourcePath + "path2.png");
        path3Img = loadImage(resourcePath + "path3.png");

        // Load gremlin animation frames
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                gremlinImgs[i] = loadImage(resourcePath + "gremlin.png");
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
                        spawnLocations.add(new PVector(col * CELLSIZE, row * CELLSIZE + TOPBAR));
                        spawnLocationsIndex.add(new PVector(col, row));
                    }
                }
                row++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up the game board based on the mapLayout array
        background(0);

        // drawMap function to draw the map
        drawMap();

        // print map layout
        println("-----Map Layout-----");
        for (int row = 0; row < BOARD_WIDTH; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                char tileChar = mapLayout[row][col];
                System.out.print(tileChar + " ");
            }
            System.out.println();
        }


        /*********************************
         * STARTING TOWER SETUP
         *********************************/

        fill(132, 115, 74); // Set the background color
        rect(WIDTH - SIDEBAR, 0, SIDEBAR, HEIGHT); // Draw the sidebar background

        float buttonWidth = 30;
        float buttonHeight = 30;
        buttonSize = 30;
        buttonSpacing = 15; // Spacing between buttons

        // button data
        String[][] buttonData = {
                {"FF", "2x speed"},
                {"P", "Pause"},
                {"B", "Build \nTower"},
                {"U1", "Upgrade \nrange"},
                {"U2", "Upgrade \nspeed"},
                {"U3", "Upgrade \npower"},
                {"M", "Mana pool\ncost 100"}
        };

        buttonStartX = WIDTH - SIDEBAR + 10;
        buttonOriginStartX = WIDTH - SIDEBAR + 10;
        println("buttonStartX:", buttonStartX);
        buttonStartY = 20;
        buttonOriginStartY = 20;

        for (String[] data : buttonData) {
            fill(132, 115, 74);
            strokeWeight(2);
            rect(buttonStartX, buttonStartY, buttonWidth, buttonHeight);

            fill(0);
            textSize(16);
            textAlign(CENTER, CENTER);
            text(data[0], buttonStartX + buttonWidth / 2, buttonStartY + buttonHeight / 2);

            fill(0);
            textSize(12);
            textAlign(LEFT, CENTER);
            text(data[1], buttonStartX + buttonWidth + 10, buttonStartY + buttonHeight / 2); // Adjust the position as needed

            // Move to the next button position
            buttonStartY += buttonHeight + buttonSpacing;
        }

    }

    public boolean findPath(char[][] mapLayout, PVector spawnLocation, PVector wizardHouse, Monster monster) {
        Queue<PVector> queue = new LinkedList<>();
        boolean[][] visited = new boolean[mapLayout.length][mapLayout[0].length];
        PVector[][] parent = new PVector[mapLayout.length][mapLayout[0].length];

        int ix = (int) spawnLocation.x / CELLSIZE;
        int iy = (int) (spawnLocation.y - TOPBAR) / CELLSIZE;
        queue.add(new PVector(ix, iy));
        visited[iy][ix] = true;

        while (!queue.isEmpty()) {
            PVector current = queue.poll();
            // Check if the current position is the wizard's house
            if (current.equals(wizardHouse)) {
                // backtrack to construct the route
                Stack<PVector> route = new Stack<>();
                PVector trace = current;

                while (trace != null) {
                    route.push(trace);
                    trace = parent[(int) trace.y][(int) trace.x];
                }
                Collections.reverse(route);

                // set route
                monster.setRoute(route);

                return true;
            }

            // Explore neighboring positions
            for (int i = 0; i < 4; i++) {
                int newX = (int) current.x + dx[i];
                int newY = (int) current.y + dy[i];

                // Check if the new position is within bounds and is an X tile
                if (newX >= 0 && newX < mapLayout[0].length && newY >= 0 && newY < mapLayout.length &&
                        (mapLayout[newY][newX] == 'X' || mapLayout[newY][newX] == 'W') && !visited[newY][newX]) {
                    queue.add(new PVector(newX, newY));
                    visited[newY][newX] = true;
                    parent[newY][newX] = current; // add current path to parent
                }
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

                } else if (tileChar == 'T'){
                    tileImage = towerImg;
                } else {
                    // Default to grass
                    tileImage = grassImg;
                }
                tileImage = (tileImage != null) ? tileImage : path0Img;
                image(tileImage, col * CELLSIZE, row * CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
            }
        }
        // Rerender the wizard's house
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
    @Override
    public void keyPressed() {
        if (key == 't' || key == 'T') {
            isTowerButtonActive = !isTowerButtonActive;
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased() {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        println(mouseX, mouseY);
        if (isTowerButtonClicked(mouseX, mouseY)) {
            println("#####################Clicked#####################");
            // Toggle the Tower button state when clicked
            isTowerButtonActive = !isTowerButtonActive;
        }


        /*******************
         * Tower Drawings
         *******************/
        // Check if the Tower button is active
        if (isTowerButtonActive) {
            println("Tower Placement Active");
            // Listen for mouse click
            println("inside tower placement");
            float mouseXPos = mouseX;
            float mouseYPos = mouseY - TOPBAR;

            // Calculate the grid cell coordinates where the user clicked
            int gridX = (int) (mouseXPos / CELLSIZE);
            int gridY = (int) (mouseYPos / CELLSIZE);

            // Check if the clicked cell is an empty grass tile
            if (gridX >= 0 && gridX < BOARD_WIDTH && gridY >= 0 && gridY < BOARD_WIDTH &&
                    mapLayout[gridY][gridX] == ' ') {
                println("inside grass");

                // Place a tower at the clicked position
                placeTower(gridX, gridY);

                // Deactivate the Tower button
                isTowerButtonActive = false;
            }
        }
    }

    void placeTower(int gridX, int gridY) {
        println("placeTower In");
        // Calculate the tower's position in pixels
        float towerX = gridX * CELLSIZE;
        float towerY = gridY * CELLSIZE + TOPBAR;

        // Load the tower image
        PImage towerImage = loadImage("src/main/resources/WizardTD/tower0.png");
        image(towerImage, towerX, towerY, CELLSIZE, CELLSIZE);

        // Update the mapLayout to mark the cell as occupied by a tower (e.g., 'T')
        mapLayout[gridY][gridX] = 'T';
    }

    boolean isTowerButtonClicked(float x, float y) {
        float towerButtonX = buttonOriginStartX;
        float towerButtonY = buttonOriginStartY + 2 * buttonSize + 2 * buttonSpacing;
        println("Tower Coords:", towerButtonX, towerButtonY);
        return x >= towerButtonX && x <= towerButtonX + buttonSize &&
                y >= towerButtonY && y <= towerButtonY + buttonSize;
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
        // Display the time in the top left corner of the GUI
        fill(132, 115, 74); // Set the fill color to menu color
        rect(0, 0, 640, 40); // Draw a rectangle for the timer region

        // Display the time in the top left corner of the GUI
        fill(255);
        textSize(16);
        textAlign(LEFT, TOP);

        if (currentWaveIndex < waves.length) {
//            println("canStart: ", canStart);
//            println("timeUntilNextWave:", timeUntilNextWave);
            int timeUntilNextWaveInt = floor(timeUntilNextWave);
            if (!isWaveActive) {
                // Display time until wave start
                text("Wave " + (currentWaveIndex + 1) + " starts in: " + timeUntilNextWaveInt + "s", 10, 10);
            } else {
                if (canStart) {
//                    println("######################STARTING NEW WAVE######################");
                    startNextWave(currentWaveIndex);
                }
                // Display wave duration
                text("Wave " + (currentWaveIndex + 1) + " in progress: " + nf(timeUntilNextWave, 0, 1) + "s", 10, 10);
            }
        } else {
            // All waves are completed
            text("All waves completed!", 10, 10);
        }

        if (currentWaveIndex < waves.length) {
            Wave currentWave = waves[currentWaveIndex];

            int totalMonstersToSpawn = currentWave.monsters[0].quantity;
//            println("total monsters to spawn", totalMonstersToSpawn);
            int spawnInterval = (int) (currentWave.duration * 1000) / totalMonstersToSpawn;
//            println("spawnInterval: ", spawnInterval);

            // Calculate the time elapsed since the last monster spawn
            long currentTime = millis();
            long elapsedTime = currentTime - lastSpawnTime;
//            println("isSpawningAllowed:", isSpawningAllowed);
//            println("monstersSpawned:", monstersSpawned, "totalMosntersToSpawn:", totalMonstersToSpawn);

            float elapsedTimeSinceSpawningStart;
            if (isSpawningAllowed && monstersSpawned < totalMonstersToSpawn) {

                // Check if it's time to spawn a monster
                elapsedTimeSinceSpawningStart = currentTime - spawningStartTime;
//                println("elapsedTime", elapsedTime, "spawnInterval", spawnInterval);
//                println("currentTime", currentTime);
//                println("spawningStartTIme:", spawningStartTime);
//                println("elapsedTimeSinceSpawningStart:", elapsedTimeSinceSpawningStart);
                if (elapsedTimeSinceSpawningStart >= currentWave.duration * 1000) {
//                    println("switching isSpawningAllowed false");
                    // Spawning duration has passed, turn off spawning
                    isSpawningAllowed = false;
                    currentWaveIndex++;
                    timeSinceLastWaveStart = millis() / 1000.0f;
                }

                if (elapsedTime >= spawnInterval) {
                    lastSpawnTime = currentTime;
                    // Spawn a monster here
                    // Update lastSpawnTime to the current time
                    spawnMonster(currentWave.monsters[0]);
                    monstersSpawned++;
                }
            } else if (monstersSpawned >= totalMonstersToSpawn) {
                elapsedTimeSinceSpawningStart = currentTime - spawningStartTime;
                if (elapsedTimeSinceSpawningStart >= currentWave.duration * 1000) {
//                    println("turning isSpawn off");
                    // Spawning duration has passed, turn off spawning
                    isSpawningAllowed = false;
                    currentWaveIndex++;
                    timeSinceLastWaveStart = millis() / 1000.0f;
                    isWaveActive = false;
                }
            }
        }
        // Clear moving traces by overdrawing map
        drawMap();

        // Moving monsters
        for (Monster monster : monstersInMap) {
            moveMonster(monster.getRoute(), monster);
        }

        image(wizardHouseImg, WizardX, WizardY, 48, 48);


    }



    private float calculateTimeUntilNextWave() {
        if (currentWaveIndex >= 0 && currentWaveIndex < waves.length) {
            float currentTime = millis() / 1000.0f; // Convert millis to seconds
//            println("currentTime:", currentTime, "timeSinceLastWaveStart:", timeSinceLastWaveStart);
            timeElapsedSinceLastWave = currentTime - timeSinceLastWaveStart; // Time since a single wave start
            float preWavePause = waves[currentWaveIndex].preWavePause; // Pre wave pause time

//            println("------------");
//            println("Wave: ", currentWaveIndex);
//            println("PreWavePause: ", preWavePause);
//            println("timeElapsedSinceLastWave", timeElapsedSinceLastWave);
//            println("Duration: ", waves[currentWaveIndex].duration);

            if (timeElapsedSinceLastWave < preWavePause && !isSpawningAllowed) {
                // Wave hasn't started yet, return time until wave start
//                println("Wave hasn't started yet");
                isWaveActive = false;
            } else if (timeElapsedSinceLastWave < preWavePause + waves[currentWaveIndex].duration) {
                // Wave is active, return the wave duration
//                println("Wave is active");
                isWaveActive = true;
                isSpawningAllowed = true;
            }
//            println("isWaveActive: ", isWaveActive);

            if (!isWaveActive) {
                canStart = true;
                return max(preWavePause - timeElapsedSinceLastWave, 0);
            } else {
//                println("wave duration:", waves[currentWaveIndex].duration);
//                println("timeElapsedSinceLastWave:", timeElapsedSinceLastWave);
//                println("preWavePause:", preWavePause);

                return waves[currentWaveIndex].duration - (timeElapsedSinceLastWave);
            }
        }

        return 0;
    }

    void startNextWave(int waveIndex) {
        canStart = false;
        isSpawningAllowed = true;
        spawningStartTime = (float) (millis());
//        println("spawningStartTime:", spawningStartTime);
//        println("CANSTART CHANGED TO FALSE!!!!!!!");

        if (waveIndex < waves.length) {
            // Set the time since last wave start to the current time
            timeSinceLastWaveStart = millis() / 1000.0f;

            // Implement logic to start the wave here
            Wave currentWave = waves[waveIndex];

//            println("Starting wave " + (waveIndex + 1) + " with " + currentWave.monsters[0].quantity + " monsters");
//            println("Wave duration: " + currentWave.duration);
//            println("Pre-wave pause: " + currentWave.preWavePause);
//            println("Monsters: " + currentWave.monsters.length);
//            println("Monster type: " + currentWave.monsters[0].type);
        }
    }

    void spawnMonster(Monster monster) {
//        println("SPAWNED MONSTER");
        // Choose a random spawn location from the spawnLocations list
        int spawnIndex = (int) random(spawnLocations.size());
        PVector spawnLocation = spawnLocations.get(spawnIndex);
        Monster newMonster = new Monster(monster.type, monster.hp, monster.speed, monster.armour, monster.manaGainedOnKill, monster.quantity, monster.x, monster.y);
        findPath(mapLayout, spawnLocations.get(spawnIndex), new PVector(WizardXIndex, WizardYIndex), newMonster);

        // Set the monster's spawn location (X and Y coordinates)
        newMonster.x = spawnLocation.x;
        newMonster.y = spawnLocation.y;

        // Determine the image file path based on the monster's type
        String imageFilePath = "src/main/resources/WizardTD/" + newMonster.type + ".png";

        // Load the monster's image from the specified file path
        PImage monsterImage = loadImage(imageFilePath);

        // Draw the monster at its spawn location
        image(monsterImage, newMonster.x, newMonster.y);

        // Add the monster to your game's data structure (monsters list)
        monstersInMap.add(newMonster);
    }

    public void moveMonster(List<PVector> route, Monster monster) {
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
