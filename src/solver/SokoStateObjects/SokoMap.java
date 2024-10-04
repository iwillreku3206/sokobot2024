/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 19:55:12
 * @ Modified time: 2024-10-04 23:44:17
 * @ Description:
 * 
 * An abstraction over the map just so its easier to query cells.
 */

package solver.SokoStateObjects;

import java.util.ArrayList;
import java.util.List;

import solver.utils.Location;

public class SokoMap {

    // This cannot be modified after it has been set
    // Again this class just makes it easier to query stuff from the map
    // false means there's a wall on that cell, while true indicates otherwise
    private boolean[][] map;

    // The goal locations
    private List<Integer> goals;
    
    // A vector sum of the goal locations
    private int goalCentroid = 0;

    /**
     * Creates a new map object.
     *  
     * @param   map     The contents of the map.
     */
    public SokoMap(char[][] map) {

        // Init the map and the goals
        this.map = new boolean[map.length][];
        this.goals = new ArrayList<>();

        // Populate the map
        for(int y = 0; y < map.length; y++) {
            
            // Grab row
            char[] row = map[y];

            // Create row
            this.map[y] = new boolean[row.length];

            // For each cell
            for(int x = 0; x < row.length; x++) {
                
                // Default everything to true (empty)
                this.map[y][x] = true;

                // Insert stuff based on map
                switch(row[x]) {

                    // A wall exists
                    case '#': 
                        this.map[y][x] = false; 
                        break;

                    // A goal was found
                    case '.': 
                    case '+': 
                    case '*': 
                        this.goalCentroid += Location.encode(x, y);
                        this.goals.add(Location.encode(x, y));
                        break;
                }
            }
        }
    }

    /**
     * Private to ensure that invalid coordinates are not placed here.
     * Returns the presence of a wall on a cell.
     * 
     * @param   location    The location to inspect.
     * @return              The presence of wall on a given cell.
     */
    private boolean hasWall(int location) {
        short x = Location.decodeX(location);
        short y = Location.decodeY(location);

        // OOB
        if(y < 0 || y >= this.map.length)
            return true;

        // OOB
        if(x < 0 || x >= this.map[y].length)
            return true;

        return !this.map[y][x];
    }

    /**
     * Returns whether or not a wall is above a given cell.
     * True means there's a wall or it's OOB.
     * 
     * @param   location    The location to inspect.
     * @return              true if wall exists, false otherwise.
     */
    public boolean hasWallNorth(int location) {
        
        // Grab coords
        int north = location + Location.NORTH;

        // Check if in bounds
        if(Location.decodeY(north) >= 0)
            return this.hasWall(north);

        // Out of bounds
        return true;
    }

    /**
     * Returns whether or not a wall is below a given cell.
     * True means there's a wall or it's OOB.
     * 
     * @param   location    The location to inspect.
     * @return              true if wall exists, false otherwise.
     */
    public boolean hasWallSouth(int location) {

        // Get the south cell
        int south = location + Location.SOUTH;

        // Check if in bounds
        if(Location.decodeY(south) < this.map.length)
            return this.hasWall(south);

        // Out of bounds
        return true;
    }

    /**
     * Returns whether or not a wall is left a given cell.
     * True means there's a wall or it's OOB.
     * 
     * @param   location    The location to inspect.
     * @return              true if wall exists, false otherwise.
     */
    public boolean hasWallWest(int location) {
        
        // Grab west cell
        int west = location + Location.WEST; 

        // Check if in bounds
        if(Location.decodeX(west) >= 0)
            return this.hasWall(west);

        // Out of bounds
        return true;
    }

    /**
     * Returns whether or not a wall is right a given cell.
     * True means there's a wall or it's OOB.
     * 
     * @param   location    The location to inspect.
     * @return              true if wall exists, false otherwise.
     */
    public boolean hasWallEast(int location) {
        
        // Grab east cell
        int east = location + Location.EAST;
        int y = Location.decodeY(location); 

        // Invalid y
        if(y < 0 || y > this.map.length)
            return true;

        // Check if in bounds
        if(Location.decodeX(east) < this.map[y].length)
            return this.hasWall(east);

        // Out of bounds
        return true;
    }

    /**
     * Returns the center of the goals.
     * 
     * @return  The approximate center of the goals.
     */
    public int getGoalCentroid() {
        return this.goalCentroid;
    }

    /**
     * Returns the location of the goals of the map.
     * 
     * @return  The goals of the map.
     */
    public int[] getGoals() {
        return this.goals
            .stream()
            .mapToInt(i -> i)
            .toArray();
    }
}
