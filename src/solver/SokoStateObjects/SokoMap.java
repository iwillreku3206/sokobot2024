/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 19:55:12
 * @ Modified time: 2024-10-07 19:52:40
 * @ Description:
 * 
 * An abstraction over the map just so its easier to query cells.
 */

package solver.SokoStateObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import solver.utils.Location;

public class SokoMap {

    // This cannot be modified after it has been set
    // Again this class just makes it easier to query stuff from the map
    // false means there's a wall on that cell, while true indicates otherwise
    private boolean[][] map;

    // Stores whether or not locations are stuckable
    // Stuckable locations are locations where boxes cannot be moved out of (even if the boxes aren't stuck)
    // Stuckable locations also account for goals, and if goals are nearby then they do not count as stuckable
    // ! todo todo todoooo
    // ! preprocess map for this or smth
    private boolean[][] passable;

    // The goal locations
    private List<Integer> goals;
    private int[] goalLocations;
    
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
        this.passable = new boolean[map.length][];
        this.goals = new ArrayList<>();
        
        // Init the maps
        this.initMaps(map);
    }

    /**
     * Inits both the map and passable arrays.
     * 
     * @param   map     The reference map.
     */
    private void initMaps(char[][] map) {

        // Populate the map
        for(int y = 0; y < map.length; y++) {
            
            // Grab row
            char[] row = map[y];

            // Create row
            this.map[y] = new boolean[row.length];
            this.passable[y] = new boolean[row.length];

            // For each cell
            for(int x = 0; x < row.length; x++) {
                
                // Default everything to true (empty)
                this.map[y][x] = true;
                this.passable[y][x] = true;

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

        // Set of unpassable corners
        Set<Integer> unpassableCorners = new TreeSet<>();

        // Compute the unpassable corners
        for(int y = 0; y < this.passable.length; y++) {
            for(int x = 0; x < this.passable[y].length; x++) {

                // Grab current cell coords
                int location = Location.encode(x, y);

                // We don't check walls too,,
                if(this.hasWall(location))
                    continue;

                // Mark corners unpassable
                if((this.hasWall(location + Location.NORTH) && this.hasWall(location + Location.EAST)) ||
                    (this.hasWall(location + Location.EAST) && this.hasWall(location + Location.SOUTH)) ||
                    (this.hasWall(location + Location.SOUTH) && this.hasWall(location + Location.WEST)) ||
                    (this.hasWall(location + Location.WEST) && this.hasWall(location + Location.NORTH))) {

                    // Corner has no goal
                    if(!this.hasGoal(location)) {
                        this.passable[y][x] = false;
                        unpassableCorners.add(location);
                    }
                }
            }
        }

        // Compute remaining of unpassable cells
        for(int corner1 : unpassableCorners) {
            for(int corner2 : unpassableCorners) {
                
                // First corner
                short x1 = Location.decodeX(corner1);
                short y1 = Location.decodeY(corner1);

                // Second corner
                short x2 = Location.decodeX(corner2);
                short y2 = Location.decodeY(corner2);

                // We check pairs of non-equal corners
                if(corner1 == corner2)
                    continue;

                // Only corners that line up are to be checked
                if(x1 != x2 && y1 != y2)
                    continue;

                // Only corners that aren't goals count
                if(this.hasGoal(corner1) || this.hasGoal(corner2))
                    continue;

                // Finally, we check if they're connected by walls
                // Case 1: vertically aligned
                if(x1 == x2) {
                    
                    boolean allCellsUnpassable = true;
                    int side = 0;

                    // Go through the path between the corners
                    for(int yIter = Math.min(y1, y2) + 1; yIter < Math.max(y1, y2); yIter++) {
                        
                        // Current inspect
                        int currentLocation = Location.encode(x1, yIter);

                        // If it has a wall, then we're checking the wrong pair of corners
                        if(this.hasWall(currentLocation)) {
                            allCellsUnpassable = false;
                            break;
                        }

                        // If side hasn't been defined
                        if(side == 0) {

                            // Western walls 
                            if(!this.hasWall(currentLocation, Location.EAST) && 
                                this.hasWall(currentLocation, Location.WEST))
                                side = -1;

                            // Eastern walls
                            if(!this.hasWall(currentLocation, Location.WEST) && 
                                this.hasWall(currentLocation, Location.EAST))
                                side = 1;

                            // No walls on either side
                            if(!this.hasWall(currentLocation, Location.WEST) && 
                                !this.hasWall(currentLocation, Location.EAST)) {
                                allCellsUnpassable = false;
                                break;    
                            }
                                
                            // The other scenario is two walls on both sides,
                            // but when that happens we don't do anyt cuz we're not sure what side to check
                                
                        // We've decided on a side
                        } else {
                            
                            // Walls swapped places
                            if(!this.hasWall(currentLocation, Location.EAST) && 
                                this.hasWall(currentLocation, Location.WEST) && 
                                side == 1) {
                                allCellsUnpassable = false;
                                break;
                            }

                            // Walls swapped places
                            if(!this.hasWall(currentLocation, Location.WEST) && 
                                this.hasWall(currentLocation, Location.EAST) &&
                                side == -1) {
                                allCellsUnpassable = false;
                                break;
                            }

                            // No walls on either side
                            if(!this.hasWall(currentLocation, Location.WEST) && 
                                !this.hasWall(currentLocation, Location.EAST)) {
                                allCellsUnpassable = false;
                                break;    
                            }
                        }
                    }

                    // Mark the cells between the two corners as unpassable
                    if(allCellsUnpassable)
                        for(int yIter = Math.min(y1, y2) + 1; yIter < Math.max(y1, y2); yIter++)
                            this.passable[yIter][x1] = false;

                // Case 2: Horizontally aligned
                } else if(y1 == y2) {

                    boolean allCellsUnpassable = true;
                    int side = 0;
                    
                    // Go through the path between the corners
                    for(int xIter = Math.min(x1, x2) + 1; xIter < Math.max(x1, x2); xIter++) {

                        // Grab the current inspect location
                        int currentLocation = Location.encode(xIter, y1);

                        // If it has a wall, then we're checking the wrong pair of corners
                        if(this.hasWall(currentLocation)) {
                            allCellsUnpassable = false;
                            break;
                        }

                        // If side hasn't been defined
                        if(side == 0) {

                            // Northern walls 
                            if(!this.hasWall(currentLocation, Location.SOUTH) && 
                                this.hasWall(currentLocation, Location.NORTH))
                                side = -1;

                            // Southern walls
                            if(!this.hasWall(currentLocation, Location.NORTH) && 
                                this.hasWall(currentLocation, Location.SOUTH))
                                side = 1;

                            // No walls on either side
                            if(!this.hasWall(currentLocation, Location.NORTH) && 
                                !this.hasWall(currentLocation, Location.SOUTH)) {
                                allCellsUnpassable = false;
                                break;    
                            }
                                
                            // The other scenario is two walls on both sides,
                            // but when that happens we don't do anyt cuz we're not sure what side to check
                                
                        // We've decided on a side
                        } else {
                            
                            // Walls swapped places
                            if(!this.hasWall(currentLocation, Location.SOUTH) && 
                                this.hasWall(currentLocation, Location.NORTH) && 
                                side == 1) {
                                allCellsUnpassable = false;
                                break;
                            }

                            // Walls swapped places
                            if(!this.hasWall(currentLocation, Location.NORTH) && 
                                this.hasWall(currentLocation, Location.SOUTH) &&
                                side == -1) {
                                allCellsUnpassable = false;
                                break;
                            }

                            // No walls on either side
                            if(!this.hasWall(currentLocation, Location.NORTH) && 
                                !this.hasWall(currentLocation, Location.SOUTH)) {
                                allCellsUnpassable = false;
                                break;    
                            }
                        }
                    }

                    // Mark the cells between the two corners as unpassable
                    if(allCellsUnpassable)
                        for(int xIter = Math.min(x1, x2) + 1; xIter < Math.max(x1, x2); xIter++)
                            this.passable[y1][xIter] = false;
                }
            }
        }
    }

    /**
     * Returns whether or not a location is passable.
     * Otherwise, crates get stuck there.
     * 
     * @param   location    The location to inspect.
     * @return              Whether or not getting a crate there means game over.
     */
    public boolean isPassable(int location) {
        short x = Location.decodeX(location);
        short y = Location.decodeY(location);
        
        return this.passable[y][x];
    }

    /** 
     * Returns whether or not goal at location exists.
     * 
     * @param   location    The location to inspect.
     * @return              Presence of goal.
    */
    private boolean hasGoal(int location) {
        return this.goals.contains(location);
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
     * Returns the presence of a wall on an adjacent cell.
     * 
     * @param   location    The location to inspect.
     * @param   direction   The direction of the adjacent cell.
     * @return              The presence of wall on the adjacent cell.
     */
    public boolean hasWall(int location, int direction) {
        return this.hasWall(location + direction);
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
     * Returns the walls of the map.
     * 
     * @return  An array containing bools about wall presence.
     */
    public boolean[][] getWalls() {
        return this.map;
    }

    /**
     * Returns the location of the goals of the map.
     * 
     * @return  The goals of the map.
     */
    public int[] getGoalLocations() {

        if(this.goalLocations != null)
            return this.goalLocations;
        
        // Grab locations
        this.goalLocations = this.goals
            .stream()
            .mapToInt(i -> i)
            .toArray();

        return this.goalLocations;
    }
}
