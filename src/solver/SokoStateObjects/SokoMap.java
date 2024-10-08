/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 19:55:12
 * @ Modified time: 2024-10-08 14:55:13
 * @ Description:
 * 
 * An abstraction over the map just so its easier to query cells.
 */

package solver.SokoStateObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import solver.utils.Location;

public class SokoMap {

    // States of the cells while checking for passability
    enum UnpassableCheckWallState {
        UNPASSABLE_WALL_WEST,
        UNPASSABLE_WALL_EAST,
        UNPASSABLE_WALL_NORTH,
        UNPASSABLE_WALL_SOUTH,
        UNPASSABLE_WALL_PENDING,
        UNPASSABLE_WALL_NONE,
    }

    // This cannot be modified after it has been set
    // Again this class just makes it easier to query stuff from the map
    // false means there's a wall on that cell, while true indicates otherwise
    private boolean[][] mapOpenCells;

    // Stores whether or not locations are stuckable
    // Stuckable locations are locations where boxes cannot be moved out of (even if the boxes aren't stuck)
    // Stuckable locations also account for goals, and if goals are nearby then they do not count as stuckable
    private boolean[][] mapPassableCells;

    // A cost map that assigns a penalty value to each cell based on its distance to the nearest crate
    private short[][] mapCellCosts;

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
        this.mapOpenCells = new boolean[map.length][];
        this.mapPassableCells = new boolean[map.length][];
        this.mapCellCosts = new short[map.length][];
        this.goals = new ArrayList<>();
        
        // Init the maps
        this.initMaps(map);
    }

    /**
     * A step in the preprocessing of the map for unpassable cells.
     * Checks whether or not a cell has a wall left or right of it (or both or neither).
     *  
     * @param   location    The location to inspect.
     * @return              The result of the check.
     */
    private UnpassableCheckWallState checkVerticalUnpassable(int location) {

        // Western walls 
        if(!this.hasWall(location, Location.EAST) && 
            this.hasWall(location, Location.WEST))
            return UnpassableCheckWallState.UNPASSABLE_WALL_WEST;

        // Eastern walls
        if(!this.hasWall(location, Location.WEST) && 
            this.hasWall(location, Location.EAST))
            return UnpassableCheckWallState.UNPASSABLE_WALL_EAST;

        // No walls on either side, so line is passable
        if(!this.hasWall(location, Location.WEST) && 
            !this.hasWall(location, Location.EAST))
            return UnpassableCheckWallState.UNPASSABLE_WALL_NONE; 

        // Walls on both sides, whole line might still be unpassable
        return UnpassableCheckWallState.UNPASSABLE_WALL_PENDING; 
    }

    /**
     * Checks for unpassability through a vertical strip, but with the side already decided.
     * Calls the same method with a different signature.
     * 
     * @param   location        The location to inspect.
     * @param   currentState    The current determined state of the vertical strip of cells.
     * @return                  Whether the state changed or not.
     */
    private UnpassableCheckWallState checkVerticalUnpassable(int location, UnpassableCheckWallState currentState) {
        
        // If it's pending, just call the plain function as is
        if(currentState == UnpassableCheckWallState.UNPASSABLE_WALL_PENDING)
            return this.checkVerticalUnpassable(location);

        // If not equal, then the walls are passable
        // Walls switched sides
        if(this.checkVerticalUnpassable(location) != currentState)
            return UnpassableCheckWallState.UNPASSABLE_WALL_NONE;

        // Otherwise, return current state
        return currentState;
    }

    /**
     * A step in the preprocessing of the map for unpassable cells.
     * Checks whether or not a cell has a wall above or below it (or both or neither).
     *  
     * @param   location    The location to inspect.
     * @return              The result of the check.
     */
    private UnpassableCheckWallState checkHorizontalUnpassable(int location) {
        
        // Northern walls 
        if(!this.hasWall(location, Location.SOUTH) && 
            this.hasWall(location, Location.NORTH))
            return UnpassableCheckWallState.UNPASSABLE_WALL_NORTH;

        // Southern walls
        if(!this.hasWall(location, Location.NORTH) && 
            this.hasWall(location, Location.SOUTH))
            return UnpassableCheckWallState.UNPASSABLE_WALL_SOUTH;

        // No walls on either side
        if(!this.hasWall(location, Location.NORTH) && 
            !this.hasWall(location, Location.SOUTH))
            return UnpassableCheckWallState.UNPASSABLE_WALL_NONE;

        // Walls on both sides, whole line might still be unpassable
        return UnpassableCheckWallState.UNPASSABLE_WALL_PENDING;
    }

    /**
     * Checks for unpassability through a horizontal strip, but with the side already decided.
     * Calls the same method with a different signature.
     * 
     * @param   location        The location to inspect.
     * @param   currentState    Where walls have been seen so far for that side.
     * @return                  Whether or not the wall was on the same side or not.
     */
    private UnpassableCheckWallState checkHorizontalUnpassable(int location, UnpassableCheckWallState currentState) {
        
        // If it's pending, just call the plain function as is
        if(currentState == UnpassableCheckWallState.UNPASSABLE_WALL_PENDING)
            return this.checkHorizontalUnpassable(location);

        // If not equal, then the walls are passable
        // Walls switched sides
        if(this.checkHorizontalUnpassable(location) != currentState)
            return UnpassableCheckWallState.UNPASSABLE_WALL_NONE;

        // Return current state
        return currentState;
    }

    /**
     * Generates unpassable corners.
     * Updates the unpassable map and creates a set of the unpassable corners.
     * 
     * @return  A set containing the locations of the unpassable corners.
     */
    private Set<Integer> generateUnpassableCorners() {

        // Set of unpassable corners
        Set<Integer> unpassableCorners = new TreeSet<>();

        // Compute the unpassable corners
        for(int y = 0; y < this.mapPassableCells.length; y++) {
            for(int x = 0; x < this.mapPassableCells[y].length; x++) {

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
                        this.mapPassableCells[y][x] = false;
                        unpassableCorners.add(location);
                    }
                }
            }
        }

        // Set on unpassable corners
        return unpassableCorners;
    }

    /**
     * Generates a vertical line of unpassable cells between two unpassable corners.
     * If such a line does not exist, it does nothing.
     * 
     * @param   x   The shared x between the two corners.
     * @param   y1  The first y.
     * @param   y2  The second y.
     */
    private void generateUnpassableVertical(int x, int y1, int y2) {

        // All cells on the strip are unpassable
        // If this becomes false, we just early return from the function
        boolean allCellsUnpassable = true;
        UnpassableCheckWallState side = UnpassableCheckWallState.UNPASSABLE_WALL_PENDING;

        // Go through the path between the corners
        for(int yIter = Math.min(y1, y2) + 1; yIter < Math.max(y1, y2); yIter++) {
            
            // Current inspect
            int currentLocation = Location.encode(x, yIter);

            // If it has a wall, then we're checking the wrong pair of corners
            if(this.hasWall(currentLocation)) {
                allCellsUnpassable = false;
                break;
            }

            // If it has a goal, then this region should be passable
            if(this.hasGoal(currentLocation)) {
                allCellsUnpassable = false;
                break;
            }

            // Determine walls
            side = this.checkVerticalUnpassable(currentLocation, side);

            // Not unpassable
            if(side == UnpassableCheckWallState.UNPASSABLE_WALL_NONE) {
                allCellsUnpassable = false;
                break;
            }        
        }

        // Mark the cells between the two corners as unpassable
        if(allCellsUnpassable)
            for(int yIter = Math.min(y1, y2) + 1; yIter < Math.max(y1, y2); yIter++)
                this.mapPassableCells[yIter][x] = false;
    }

    /**
     * Generates a horizontal line of unpassable cells between two unpassable corners.
     * If such a line does not exist, it does nothing.
     * 
     * @param   y   The shared y between the two corners.
     * @param   x1  The first x.
     * @param   x2  The second x.
     */
    private void generateUnpassableHorizontal(int y, int x1, int x2) {

        // All cells on the strip are unpassable
        // If this becomes false, we just early return from the function
        boolean allCellsUnpassable = true;
        UnpassableCheckWallState side = UnpassableCheckWallState.UNPASSABLE_WALL_PENDING;
        
        // Go through the path between the corners
        for(int xIter = Math.min(x1, x2) + 1; xIter < Math.max(x1, x2); xIter++) {

            // Grab the current inspect location
            int currentLocation = Location.encode(xIter, y);

            // If it has a wall, then we're checking the wrong pair of corners
            if(this.hasWall(currentLocation)) {
                allCellsUnpassable = false;
                break;
            }

            // If it has a goal, then this region should be passable
            if(this.hasGoal(currentLocation)) {
                allCellsUnpassable = false;
                break;
            }

            side = this.checkHorizontalUnpassable(currentLocation, side);

            // Not unpassable
            if(side == UnpassableCheckWallState.UNPASSABLE_WALL_NONE) {
                allCellsUnpassable = false;
                break;
            }
        }

        // Mark the cells between the two corners as unpassable
        if(allCellsUnpassable)
            for(int xIter = Math.min(x1, x2) + 1; xIter < Math.max(x1, x2); xIter++)
                this.mapPassableCells[y][xIter] = false;
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
            this.mapOpenCells[y] = new boolean[row.length];
            this.mapPassableCells[y] = new boolean[row.length];
            this.mapCellCosts[y] = new short[row.length];

            // For each cell
            for(int x = 0; x < row.length; x++) {
                
                // Default everything to true (empty)
                this.mapOpenCells[y][x] = true;
                this.mapPassableCells[y][x] = true;
                this.mapCellCosts[y][x] = (short) ((1 << 15) - 1);

                // Insert stuff based on map
                switch(row[x]) {

                    // A wall exists
                    case '#': 
                        this.mapOpenCells[y][x] = false; 
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
        Set<Integer> unpassableCorners = this.generateUnpassableCorners();

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
                if(x1 == x2)
                    this.generateUnpassableVertical(x1, y1, y2);

                // Case 2: Horizontally aligned
                else if(y1 == y2)
                    this.generateUnpassableHorizontal(y1, x1, x2);
            }
        }

        // Visited cells
        Set<Integer> visitedMap = new TreeSet<>();
        PriorityQueue<Long> queue = new PriorityQueue<>();

        // Init the cell costs
        for(int goal : this.goals) {

            // Grab coords
            int x = Location.decodeX(goal);
            int y = Location.decodeY(goal);

            // Queue value
            long queueValue = goal;

            // Set the cost to 0
            this.mapCellCosts[y][x] = 0;

            // Add to visited
            queue.add(queueValue);
        }

        // While queue is non-empty
        while(!queue.isEmpty()) {

            // Grab head
            long head = queue.poll();

            // New queue value
            int location = (int) (head & -1);
            int cost = (int) (head >> 32);
            
            // Location
            short x = Location.decodeX(location);
            short y = Location.decodeY(location);

            // Add to visited
            visitedMap.add(location);

            // If wall, just skip
            if(!this.mapOpenCells[y][x])
                continue;

            // Update map cost
            if(this.mapCellCosts[y][x] > cost)
                this.mapCellCosts[y][x] = (short) cost;
            
            // For each neighbor
            for(int direction : Location.DIRECTIONS) {

                // New location
                int newLocation = location + direction;

                // If visited, skip
                if(visitedMap.contains(newLocation))
                    continue;

                // New coords
                int newX = Location.decodeX(newLocation);
                int newY = Location.decodeY(newLocation);

                // Out of bounds y
                if(newY < 0 || newY >= this.mapCellCosts.length)
                    continue;

                // If out of bounds x
                if(newX < 0 || newX >= this.mapCellCosts[newY].length)
                    continue;

                // Compute cost
                long queueValue = cost + 1;

                // Add location info
                queueValue <<= 32;
                queueValue += newLocation;

                // Queue it
                queue.add(queueValue);
            }
        }
    }

    /**
     * Returns the cost of a cell.
     * Cells with higher costs are bad places for crates to be in.
     * 
     * @param   location    The location to inspect.
     * @return              Whether or not getting a crate there means game over.
     */
    public short getCellCost(int location) {
        short x = Location.decodeX(location);
        short y = Location.decodeY(location);
        
        return this.mapCellCosts[y][x];
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

        // OOB
        if(y < 0 || y >= this.mapOpenCells.length)
            return true;

        // OOB
        if(x < 0 || x >= this.mapOpenCells[y].length)
            return true;
        
        return this.mapPassableCells[y][x];
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
        if(y < 0 || y >= this.mapOpenCells.length)
            return true;

        // OOB
        if(x < 0 || x >= this.mapOpenCells[y].length)
            return true;

        return !this.mapOpenCells[y][x];
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
        return this.mapOpenCells;
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
