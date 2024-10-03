/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:47:30
 * @ Modified time: 2024-10-03 22:30:42
 * @ Description:
 * 
 * A class that represents the state of the game at any given time.
 * Stores information about the states of the creates too.
 * Note that this class only stores information that changes across states.
 */

package solver.SokoStateObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import solver.SokoObjects.SokoCrate;
import solver.utils.Location;

public class SokoState {

    enum StateStatus {
        WON,        // All crates are on goals
        LOST,       // All crates are stuck / some crates are permanently stuck
        PENDING,    // Keep trying!
    }
    
    // A reference to actual crate objects
    // We use these for convenience of computations
    // These are indexed by their locations
    private HashMap<Integer, SokoCrate> crates;

    // The location of the player
    private int player;

    /**
     * Creates a new state object using only serialized data.
     * Note that this class only stores data that changes between states.
     * All other data are stored by the SokoGame class.
     * 
     * @param   player  An integer representing the location of the player.
     * @param   crates  Integers representing the location of the crates.
     * @param   map     The map that contextualizes the information of the player and crates.
     */
    public SokoState(int player, int[] crates, SokoMap map) {

        // Init the arrays
        this.crates = new HashMap<>();
        
        // Set the locations
        this.player = player;

        // Create the crates
        for(int i = 0; i < crates.length; i++) {
            int crateLocation = crates[i];

            // Insert a crate into the hashmap
            this.crates.put(
                crateLocation,
                SokoCrate
                    .create(crateLocation)
                    .setN(this.getObstacleNorth(crateLocation, map))
                    .setE(this.getObstacleEast(crateLocation, map))
                    .setW(this.getObstacleWest(crateLocation, map))
                    .setS(this.getObstacleSouth(crateLocation, map))
                    .build());
        }
    }

    /**
     * Checks whether or not a crate exists at  a certain location.
     * 
     * @param   location    The location to inspect.
     * @return              Whether or not crate exists at location.
     */
    private boolean hasCrate(int location) {
        
        // Damn this is O(1)
        if(this.crates.containsKey(location))
            return true;
        
        // No crate there
        return false;
    }

    /**
     * Returns whether or not a crate is above a given cell.
     * True means there's a crate.
     * 
     * @param   location    The location to inspect.
     * @return              true if crate exists, false otherwise.
     */
    private boolean hasCrateNorth(int location) {
        return this.hasCrate(location + Location.NORTH);
    }

    /**
     * Returns whether or not a crate is below a given cell.
     * True means there's a crate.
     * 
     * @param   location    The location to inspect.
     * @return              true if crate exists, false otherwise.
     */
    private boolean hasCrateSouth(int location) {
        return this.hasCrate(location + Location.SOUTH);
    }

    /**
     * Returns whether or not a crate is left a given cell.
     * True means there's a crate.
     * 
     * @param   location    The location to inspect.
     * @return              true if crate exists, false otherwise.
     */
    private boolean hasCrateWest(int location) {
        return this.hasCrate(location + Location.WEST);
    }

    /**
     * Returns whether or not a crate is right a given cell.
     * True means there's a crate.
     * 
     * @param   location    The location to inspect.
     * @return              true if crate exists, false otherwise.
     */
    private boolean hasCrateEast(int location) {
        return this.hasCrate(location + Location.EAST);
    }

    /**
     * Retrieves whether or not there's a wall or a crate above a given cell.
     * 
     * @param   location    The location to inspect.
     * @param   map         The map to use.
     * @return
     */
    public char getObstacleNorth(int location, SokoMap map) {
        if(map.hasWallNorth(location))
            return 'w';
        if(this.hasCrateNorth(location))
            return 'c';
        return ' ';
    }

    /**
     * Retrieves whether or not there's a wall or a crate below a given cell.
     * 
     * @param   location    The location to inspect.
     * @param   map         The map to use.
     * @return
     */
    public char getObstacleSouth(int location, SokoMap map) {
        if(map.hasWallSouth(location))
            return 'w';
        if(this.hasCrateSouth(location))
            return 'c';
        return ' ';
    }

    /**
     * Retrieves whether or not there's a wall or a crate left a given cell.
     * 
     * @param   location    The location to inspect.
     * @param   map         The map to use.
     * @return
     */
    public char getObstacleWest(int location, SokoMap map) {
        if(map.hasWallWest(location))
            return 'w';
        if(this.hasCrateWest(location))
            return 'c';
        return ' ';
    }

    /**
     * Retrieves whether or not there's a wall or a crate right a given cell.
     * 
     * @param   location    The location to inspect.
     * @param   map         The map to use.
     * @return
     */
    public char getObstacleEast(int location, SokoMap map) {
        if(map.hasWallEast(location))
            return 'w';
        if(this.hasCrateEast(location))
            return 'c';
        return ' ';
    }

    /**
     * Determines the status of the state.
     * 
     * @param   map     The map to check the state with.
     * @return          An enum indicating what status the state is in.
     */
    public StateStatus getStatus(SokoMap map) {
        
        // All crates are stuck
        // All crates are at goals
        boolean allCratesAreStuck = true;
        boolean allCratesAreGood = true;

        // Check if at least one crate is permanently stuck
        for(SokoCrate crate : this.crates.values()) 
            if(crate.isStuckPermanently())
                return StateStatus.LOST;

        // Check if all crates are at least temporarily stuck
        for(SokoCrate crate : this.crates.values()) 
            if(!crate.isStuckPermanently() && !crate.isStuckTemporarily())
                allCratesAreStuck = false;

        // Check if all the goals have crates
        for(int goal : map.getGoals())
            if(!this.crates.containsKey(goal))
                allCratesAreGood = false;

        // No more moves for this state
        if(allCratesAreStuck)
            return StateStatus.LOST;

        // We won!
        if(allCratesAreGood)
            return StateStatus.WON;

        // Still more to do
        return StateStatus.PENDING;
    }

    /**
     * This allows us to check whether or not we hit the same state twice.
     * Hitting the same state twice indicates a loop, which we try to avoid.
     * 
     * @return  A hash or serial that uniquely represents the state.
     */
    public int getSerial() {
       
        // ! figure out a way to serialize states
        return 1; 
    }

    /**
     * Returns an estimate of the priority score of the state.
     * Note that we use integers so things are computed much faster.
     * 
     * @return  The estimate of the priority for the state.
     */
    public int getPriority() {
        
        // ! todo implement
        return 1;
    }
}
