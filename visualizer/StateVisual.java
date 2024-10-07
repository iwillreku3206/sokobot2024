/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-07 12:51:19
 * @ Modified time: 2024-10-07 15:16:59
 * @ Description:
 * 
 * Helps us visualize a state found by the bot.
 */

package visualizer;

import javax.swing.JPanel;
import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.util.stream.Stream;
import java.util.Arrays;
import java.io.File;

import solver.SokoStateObjects.SokoMap;
import solver.SokoStateObjects.SokoState;
import solver.utils.Location;

public class StateVisual extends JPanel {

    // The images to use
    private static BufferedImage WALL;
    private static BufferedImage CRATE;
    private static BufferedImage GOAL;
    private static BufferedImage CRATEGOAL;
    private static BufferedImage PLAYER;

    private static int UPPER_LEFT_X = 2;
    private static int UPPER_LEFT_Y = 6;
    private static int TILE_SIZE = 32;

    // The state and map references
    private SokoState state;
    private SokoMap map;

    /**
     * Creates a new state visual and inits it.
     */
    public StateVisual() {
        super();
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.loadImages();
    }

    /**
     * Loads the assets to use for displaying.
     */
    private void loadImages() {

        // Try to load the pics
        try {
            WALL = ImageIO.read(new File("visualizer/graphics/wall.png"));
            GOAL = ImageIO.read(new File("visualizer/graphics/goal.png"));
            CRATE = ImageIO.read(new File("visualizer/graphics/crate.png"));
            CRATEGOAL = ImageIO.read(new File("visualizer/graphics/crategoal.png"));
            PLAYER = ImageIO.read(new File("visualizer/graphics/bot.png"));
        
        // Something went wrong
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Visualizes a state through a gui.
     * 
     * @param   state   The state of the crates and player.
     * @param   map     The map of walls and goals.
     */
    public void showState(SokoState state, SokoMap map) {
        this.state = state;
        this.map = map;
    }

    /**
     * Retrieves the x coordinate in the screen space.
     * Receives x coord in map space.
     * 
     * @param   xIndex  The x on the map.
     * @return          The x on the component.
     */
    private int getX(int xIndex) {
        return (xIndex + UPPER_LEFT_X) * TILE_SIZE;
    }

    /**
     * Retrieves the y coordinate in the screen space.
     * Receives y coord in map space.
     * 
     * @param   yIndex  The y on the map.
     * @return          The y on the component.
     */
    private int getY(int yIndex) {
        return (yIndex + UPPER_LEFT_Y) * TILE_SIZE;
    }

    /**
     * Paints the component.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Clear the component again
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(new Color(10, 15, 25));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Skip if null
        if(this.state == null || this.map == null)
            return;

        // Grab the map and state info
        boolean[][] walls = this.map.getWalls();
        int[] goals = this.map.getGoalLocations();
        int[] crates = this.state.getCrateLocations();
        int player = this.state.getPlayer();

        // Render the walls
        for (int y = 0; y < walls.length; y++) {
            for (int x = 0; x < walls[y].length; x++) {
                
                // Wall exists
                if (!walls[y][x])
                    g.drawImage(WALL, 
                        this.getX(x), this.getY(y), 
                        TILE_SIZE, TILE_SIZE, this);
            }
        }

        // Draw the goals
        for(int goal : goals) {

            // Draw the goal
            g.drawImage(
                GOAL, 
                this.getX(Location.decodeX(goal)),
                this.getY(Location.decodeY(goal)),
                TILE_SIZE, TILE_SIZE, this);
        }

        // Draw the crates
        for(int crate : crates) {

            // The image to use
            BufferedImage crateImage;

            // Choose image
            if(Arrays.stream(goals).anyMatch(goal -> goal == crate))
                crateImage = CRATEGOAL;
            else
                crateImage = CRATE;
            
            // Draw the crate
            g.drawImage(
                crateImage, 
                this.getX(Location.decodeX(crate)),
                this.getY(Location.decodeY(crate)),
                TILE_SIZE, TILE_SIZE, this);
        }

        // Draw the player
        g.drawImage(
            PLAYER, 
            this.getX(Location.decodeX(player)),
            this.getY(Location.decodeY(player)),
            TILE_SIZE, TILE_SIZE, this);

        g.setColor(new Color(150, 160, 180));
        g.fillRect(0, 0, this.getWidth(), TILE_SIZE * 4);
        g.setColor(Color.RED);
        // g.setFont(Font.createFont(, ));
        g.drawString("HMMM", this.getWidth() - 375, this.getHeight() - 12);
        g.setColor(Color.BLACK);
        // g.setFont(this.statusFont);
        g.drawString("MOVES: ", 8, this.getHeight() - 12);
        g.drawString("PROGRESS: ", 176, this.getHeight() - 12);
      }
}
