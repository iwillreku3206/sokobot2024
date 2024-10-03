/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 19:55:12
 * @ Modified time: 2024-10-03 21:28:09
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
     * @return  The presence of wall on a given cell.
     */
    private boolean hasWall(int x, int y) {
        return this.map[y][x]; 
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
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(y - 1 >= 0)
            return this.hasWall(x, y - 1);

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
        
        // Grab coords
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(y + 1 < this.map.length)
            return this.hasWall(x, y + 1);

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
        
        // Grab coords
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(x - 1 >= 0)
            return this.hasWall(x - 1, y);

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
        
        // Grab coords
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(x + 1 < this.map.length)
            return this.hasWall(x + 1, y);

        // Out of bounds
        return true;
    }
}
