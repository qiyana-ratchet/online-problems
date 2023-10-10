package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
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

    // Feel free to add any additional methods or attributes you want. Please put classes in different files.

    // Define image variables for tiles, towers, and enemies
    private PImage grassImg, shrubImg, pathImg, towerImg, wizardHouseImg;
    private PImage[] gremlinImgs = new PImage[6]; // Gremlin has multiple images for animation
    private PImage fireballImg;

    // Configuration variables
    private String layoutFileName;
    private JSONArray waves;
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
        PImage path0Img = loadImage(resourcePath + "path0.png");
        PImage path1Img = loadImage(resourcePath + "path1.png");
        PImage path2Img = loadImage(resourcePath + "path2.png");
        PImage path3Img = loadImage(resourcePath + "path3.png");

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
        char[][] mapLayout = new char[BOARD_WIDTH][BOARD_WIDTH];

        try {
            BufferedReader reader = new BufferedReader(new FileReader("level1.txt"));
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null && row < BOARD_WIDTH) {
                for (int col = 0; col < line.length() && col < BOARD_WIDTH; col++) {
                    mapLayout[row][col] = line.charAt(col);
                }

                row++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up the game board based on the mapLayout array
        background(0); // Set background color

        float wx = 0;
        float wy = 0;
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
                    float wizardHouseX = col * CELLSIZE + (CELLSIZE - 48) / 2;
                    float wizardHouseY = row * CELLSIZE + TOPBAR + (CELLSIZE - 48) / 2;
                    wx = wizardHouseX;
                    wy = wizardHouseY;

                    // Render the wizard's house
                    // image(wizardHouseImg, wizardHouseX, wizardHouseY, 48, 48);
                } else {
                    // Default to grass
                    tileImage = grassImg;
                }

                tileImage = (tileImage != null) ? tileImage : path0Img;
                image(tileImage, col * CELLSIZE, row * CELLSIZE + TOPBAR, CELLSIZE, CELLSIZE);
            }
        }
        image(wizardHouseImg, wx, wy, 48, 48);
    }


    // Load JSON configuration file
    public void readConfig() {
        try {
            // Load the JSON configuration file
            JSONObject config = loadJSONObject(configPath);

            // Read configuration data and save it to class fields
            layoutFileName = config.getString("layout");
            waves = config.getJSONArray("waves");
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

    /*@Override
    public void mouseDragged(MouseEvent e) {

    }*/

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {


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
