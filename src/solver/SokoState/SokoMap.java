/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 19:55:12
 * @ Modified time: 2024-10-03 20:21:34
 * @ Description:
 * 
 * An abstraction over the map just so its easier to query cells.
 */

package solver.SokoState;

import solver.utils.Location;

public class SokoMap {
    
    // The different possible cells of the map
    enum SokoCell {
        WALL,
        CRATE,
        PLAYER,
        GOAL,
        CRATE_ON_GOAL,
        PLAYER_ON_GOAL,
        NONE,   
    }

    // This cannot be modified after it has been set
    // Again this class just makes it easier to query stuff from the map
    private SokoCell[][] map;

    /**
     * Creates a new map object.
     *  
     * @param   map     The contents of the map.
     */
    public SokoMap(char[][] map) {

        // Init the map
        this.map = new SokoCell[map.length][];

        // Populate the map
        for(int i = 0; i < map.length; i++) {
            
            // Grab row
            char[] row = map[i];

            // Create row
            this.map[i] = new SokoCell[row.length];

            // For each cell
            for(int j = 0; j < row.length; j++) {
                switch(row[j]) {
                    case '#': this.map[i][j] = SokoCell.WALL; break;
                    case '$': this.map[i][j] = SokoCell.CRATE; break;
                    case '@': this.map[i][j] = SokoCell.PLAYER; break;
                    case '.': this.map[i][j] = SokoCell.GOAL; break;
                    case '+': this.map[i][j] = SokoCell.PLAYER_ON_GOAL; break;
                    case '*': this.map[i][j] = SokoCell.CRATE_ON_GOAL; break;
                    default:  this.map[i][j] = SokoCell.NONE; break;
                }
            }
        }
    }

    /**
     * Private to ensure that invalid coordinates are not placed here.
     * Crates in goals are crates too.
     * 
     * @return  The obstacle (wall, crate, or nothing) at a given cell.
     */
    private char getObstacle(int x, int y) {
        switch(this.map[y][x]) {        
            case SokoCell.CRATE:
            case SokoCell.CRATE_ON_GOAL:
                return 'c';
            case SokoCell.WALL:
                return 'w';
            default:
                return ' ';
        }
    }

    /**
     * Returns whether or not a crate or wall is above a given cell.
     * 
     * @param   location    The location to inspect.
     * @return              'c' if a crate exists there, 'w' if a wall, '!' if OOB, and ' ' if none.
     */
    public char getObstacleNorth(int location) {
        
        // Grab coords
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(y - 1 >= 0)
            return this.getObstacle(x, y - 1);

        // Out of bounds
        return '!';
    }

    /**
     * Returns whether or not a crate or wall is below a given cell.
     * 
     * @param   location    The location to inspect.
     * @return              'c' if a crate exists there, 'w' if a wall, '!' if OOB, and ' ' if none.
     */
    public char getObstacleSouth(int location) {
        
        // Grab coords
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(y + 1 < this.map.length)
            return this.getObstacle(x, y + 1);

        // Out of bounds
        return '!';
    }

    /**
     * Returns whether or not a crate or wall is left a given cell.
     * 
     * @param   location    The location to inspect.
     * @return              'c' if a crate exists there, 'w' if a wall, '!' if OOB, and ' ' if none.
     */
    public char getObstacleWest(int location) {
        
        // Grab coords
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(x - 1 >= 0)
            return this.getObstacle(x - 1, y);

        // Out of bounds
        return '!';
    }

    /**
     * Returns whether or not a crate or wall is right a given cell.
     * 
     * @param   location    The location to inspect.
     * @return              'c' if a crate exists there, 'w' if a wall, '!' if OOB, and ' ' if none.
     */
    public char getObstacleEast(int location) {
        
        // Grab coords
        short[] coords = Location.decode(location);
        short x = coords[0];
        short y = coords[1];

        // Check if in bounds
        if(x + 1 < this.map.length)
            return this.getObstacle(x + 1, y);

        // Out of bounds
        return '!';
    }
}
