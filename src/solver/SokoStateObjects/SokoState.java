/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:47:30
 * @ Modified time: 2024-10-04 22:06:09
 * @ Description:
 * 
 * A class that represents the state of the game at any given time.
 * Stores information about the states of the creates too.
 * Note that this class only stores information that changes across states.
 */

package solver.SokoStateObjects;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import solver.SokoObjects.SokoCrate;
import solver.utils.Heuristic;
import solver.utils.Location;

public class SokoState {

    // Tweak this parameter, this seems like a good value for now
    // Adjusts how much the heuristic influences priority evaluation
    // 0.0 means not at all and 1.0 means it contributes a lot
    public static final float HEURISTIC_WEIGHT_SOLUTION_LENGTH = 1.0f;
    public static final float HEURISTIC_WEIGHT_GOOD_COUNT = 0.5f;

    public static final int HEURISTIC_BIAS_SOLUTION_LENGTH = 0;
    public static final int HEURISTIC_BIAS_GOOD_COUNT = 10;

    // What state the state is in
    public enum StateStatus {
        WON,        // All crates are on goals
        LOST,       // All crates are stuck / some crates are permanently stuck
        PENDING,    // Keep trying!
    }
    
    // A reference to actual crate objects
    // We use these for convenience of computations
    // These are indexed by their locations
    private Map<Integer, SokoCrate> crates;

    // The location of the player
    private int player;

    // The history of the state (what moves were taken to get there)
    private String history;
    private int historyLength;

    // The serial of the state
    // Should only be computed once
    private BigInteger serial;

    /**
     * Creates a new state object using only serialized data.
     * Note that this class only stores data that changes between states.
     * All other data are stored by the SokoGame class.
     * 
     * @param   player          An integer representing the location of the player.
     * @param   crates          Integers representing the location of the crates.
     * @param   map             The map that contextualizes the information of the player and crates.
     * @param   history         The history of the state (what moves got us there).
     * @param   historyLength   The length of the history of the state.
     */
    public SokoState(int player, int[] crates, SokoMap map, String history, int historyLength) {

        // Init the arrays
        this.crates = new HashMap<>();
        
        // Set the locations
        this.player = player;

        // Init the history
        this.history = history;
        this.historyLength = historyLength;

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

        // Set the good crates
        for(int goal : map.getGoals())
            if(this.crates.containsKey(goal))
                this.crates.get(goal).setGood(true);
    }

    /**
     * Returns the number of good crates we got.
     * 
     * @return  The number of crates on goals.
     */
    private int getGoodCrateCount() {

        // Count good crates
        int goodCrateCount = 0;
        for(SokoCrate crate : this.crates.values())
            if(crate.isGood())
                goodCrateCount++;
            
        return goodCrateCount;
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
        boolean allCratesAreStuck = true;
        boolean allCratesAreGood = true;

        // Check if all the goals have crates
        for(int goal : map.getGoals())
            if(!this.crates.containsKey(goal))
                allCratesAreGood = false;

        // We won!
        // It is important to check for this condition first
        // because crates can be stuck in a winning state
        if(allCratesAreGood)
            return StateStatus.WON;

        // Check if at least one crate is permanently stuck
        for(SokoCrate crate : this.crates.values()) 
            if(crate.isStuckPermanently() && !crate.isGood())
                return StateStatus.LOST;

        // Check if all crates are at least temporarily stuck
        for(SokoCrate crate : this.crates.values()) 
            if(!crate.isStuckPermanently() && !crate.isStuckTemporarily())
                allCratesAreStuck = false;

        // No more moves for this state
        if(allCratesAreStuck)
            return StateStatus.LOST;

        // Still more to do
        return StateStatus.PENDING;
    }

    /**
     * Returns a crate we can use to test stuff.
     * 
     * @param   location    The location of the crate.
     * @return              Crate object or null if not found.
     */
    public SokoCrate getCrate(int location) {
        if(!this.hasCrate(location))
            return null;
        return this.crates.get(location);
    }

    /**
     * Returns the crate locations.
     * 
     * @return  An array containing the crate locations.
     */
    public int[] getCrateLocations() {
        return this.crates.keySet()
            .stream()
            .mapToInt(i -> i)
            .toArray();
    }

    /**
     * Return the location of the player.
     * 
     * @return  The player location.
     */
    public int getPlayer() {
        return this.player;
    }

    /**
     * Return the history of the state.
     * 
     * @return  A string representing the moves taken by the player to get to that state.
     */
    public String getHistory() {
        return this.history;
    }

    /**
     * Return the length of the history.
     * 
     * @return  The length of the history string.
     */
    public int getHistoryLength() {
        return this.historyLength;
    }

    /**
     * This allows us to check whether or not we hit the same state twice.
     * Hitting the same state twice indicates a loop, which we try to avoid.
     * 
     * @return  A hash or serial that uniquely represents the state.
     */
    public BigInteger getSerial() {

        // If it's already defined
        if(this.serial != null)
            return this.serial;

        // Get crate locations in order
        int[] crates = this.crates.keySet()
            .stream()
            .mapToInt(i -> i)
            .sorted()
            .toArray();
       
        // The serial
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Serialize the state
        // Unfortunately needs a try catch
        try {
            dos.writeInt(this.player);
            for(int crate : crates)
                dos.writeInt(crate);
        
        // Exception handler
        } catch (IOException e) {
            System.out.println("Something went wrong during state serialization.");
            e.printStackTrace();
        }

        // Convert to a byte array
        this.serial = new BigInteger(baos.toByteArray());

        // Return
        return this.serial;
    }

    /**
     * Returns an estimate of the priority score of the state.
     * Note that we use integers so things are computed much faster.
     * 
     * @return  The estimate of the priority for the state.
     */
    public int getPriority() {

        // Get the variables
        int h = this.historyLength;
        int g = this.getGoodCrateCount();

        // Ain't no way,, this shit worked
        return (int) (

            // The solution length heuristic 
            Heuristic.weight(
                Heuristic.bias(
                    h,  HEURISTIC_BIAS_SOLUTION_LENGTH), 
                        HEURISTIC_WEIGHT_SOLUTION_LENGTH) *

            // The good crate count heuristic
            Heuristic.weight(
                Heuristic.invert(
                    Heuristic.bias(
                        g,  HEURISTIC_BIAS_GOOD_COUNT)),
                            HEURISTIC_WEIGHT_GOOD_COUNT)
        );
    }
}
