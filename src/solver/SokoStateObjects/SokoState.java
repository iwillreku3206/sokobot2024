/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:47:30
 * @ Modified time: 2024-10-08 15:02:01
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
import java.util.Collection;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Base64;

import solver.SokoObjects.SokoCrate;
import solver.utils.Heuristic;
import solver.utils.Location;

public class SokoState {
    public static final float HEURISTIC_WEIGHT_MOVE_COUNT = 0.5f;
    public static final float HEURISTIC_WEIGHT_CRATE_MOVE_COUNT = 0.35f;
    public static final float HEURISTIC_WEIGHT_TURN_COUNT = 0.25f;

    // Tweak this parameter, this seems like a good value for now
    // Adjusts how much the heuristic influences cost evaluation
    // 0.0 means not at all and 1.0 means it contributes a lot
    public static final float HEURISTIC_WEIGHT_SOLUTION = 1.0f;
    public static final float HEURISTIC_WEIGHT_GOOD_COUNT = 1.0f;
    public static final float HEURISTIC_WEIGHT_DISTANCE = 0.05f;

    public static final int HEURISTIC_BIAS_SOLUTION = 0;
    public static final int HEURISTIC_BIAS_GOOD_COUNT = 10;
    public static final int HEURISTIC_BIAS_DISTANCE = 0;

    // These determine whether or not their effects on the heuristic value are inverted or not
    // By default good crates are inverted because more of them means a smaller cost value
    public static final boolean HEURISTIC_INVERT_SOLUTION = false;
    public static final boolean HEURISTIC_INVERT_GOOD_COUNT = true;
    public static final boolean HEURISTIC_INVERT_DISTANCE = false;

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

    // Crate moves are the number of moves that have moved crates
    // Think of crateCentroid as the vector sum of the locations of the crates.
    private int crateCentroid = 0;
    
    // Move-based heuristics
    private int crateMoveCount = 0;
    private int turnCount = 0;

    // The location of the player
    private int player;

    // The history of the state (what moves were taken to get there)
    private String history;
    private int moveCount;

    // The serial of the state
    // Should only be computed once
    private String stateSerial = null;

    /**
     * Creates a new state object using only serialized data.
     * Note that this class only stores data that changes between states.
     * All other data are stored by the SokoGame class.
     * 
     * @param   player          An integer representing the location of the player.
     * @param   crates          Integers representing the location of the crates.
     * @param   crateMoved      Whether or not a crate was moved during this state.
     * @param   map             The map that contextualizes the information of the player and crates.
     * @param   history         The history of the state (what moves got us there).
     * @param   historyLength   The length of the history of the state.
     */
    public SokoState(int player, int[] crates, boolean crateMoved, boolean turned, SokoMap map, String history, int historyLength) {

        // Init the arrays
        this.crates = new TreeMap<>();
        if(crateMoved) this.crateMoveCount++;
        if(turned) this.turnCount++;
        
        // Set the locations
        this.player = player;

        // Init the history
        this.history = history;
        this.moveCount = historyLength;

        // Create the crates
        for(int i = 0; i < crates.length; i++) {

            // Grab the location of the crate
            int crateLocation = crates[i];

            // Add to the crate sum
            this.crateCentroid += crateLocation; 

            // Insert a crate into the hashmap
            this.crates.put(
                crateLocation,
                SokoCrate
                    .create(crateLocation)
                    .setN(this.hasObstacle(crateLocation, Location.NORTH, map, crates))
                    .setE(this.hasObstacle(crateLocation, Location.EAST, map, crates))
                    .setW(this.hasObstacle(crateLocation, Location.WEST, map, crates))
                    .setS(this.hasObstacle(crateLocation, Location.SOUTH, map, crates))
                    .build());
        }

        // Set the good crates
        for(int goal : map.getGoalLocations())
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
     * Checks whether or not a crate exists on the cell adjacent to location,
     * specified by direction.
     * 
     * @param   location    The location to start at.
     * @param   direction   The direction of the cell to check.
     * @return              Whether or not a cell exists there.
     */
    public boolean hasCrate(int location, int direction) {
        return this.hasCrate(location + direction);
    }

    /**
     * Retrieves whether or not there's a wall or a crate adjacent to a cell.
     * This version of the function is used during the constructor, since the crates have not yet been initialized.
     * 
     * @param   location    The location to start at.
     * @param   direction   The direction of the neighbor to inspect.
     * @param   map         The map to use.
     * @param   crates      A list of crates; specified during init.
     * @return              Whether or not there is an obstacle there.
     */
    private boolean hasObstacle(int location, int direction, SokoMap map, int[] crates) {
        
        // Check for wall
        if(map.hasWall(location, direction))
            return true;

        // Check for crate
        for(int crate : crates)
            if(crate == location + direction)
                return true;
        
        return false;
    }
    
    /**
     * Retrieves whether or not there's a wall or a crate adjacent to a cell.
     * 
     * @param   location    The location to start at.
     * @param   direction   The direction of the neighbor to inspect.
     * @param   map         The map to use.
     * @return
     */
    public char getObstacle(int location, int direction, SokoMap map) {
        
        // Check for wall
        if(map.hasWall(location, direction))
            return 'w';

        // Check for crate
        if(this.hasCrate(location, direction))
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
        
        // Stores a list of unstuck crates
        Set<Integer> unstuckCrates = new TreeSet<>();
        Set<Integer> visitedCrates = new TreeSet<>();
        Collection<SokoCrate> crateCollection = this.crates.values();

        // Check if all the goals have crates
        for(SokoCrate crate : crateCollection) {

            // Good crates
            if(!crate.isGood())
                allCratesAreGood = false;

            // Preprocess unstuck crates
            if(!crate.isStuck()) 
                unstuckCrates.add(crate.getLocation());
        }

        // We won!
        // It is important to check for this condition first
        // because crates can be stuck in a winning state
        if(allCratesAreGood)
            return StateStatus.WON;

        // Check if at least one crate entered a non-passable cell
        for(SokoCrate crate : crateCollection)
            if(!map.isPassable(crate.getLocation()))
                return StateStatus.LOST;

        // Check if all crates are at least temporarily stuck
        for(SokoCrate crate : crateCollection) 
            if(!crate.isStuck())
                allCratesAreStuck = false;

        // Check if crates are stuck in groups
        for(SokoCrate crate : crateCollection)
            if(!visitedCrates.contains(crate.getLocation()))
                if(crate.isStuckInAGroup(this.crates, unstuckCrates, visitedCrates))
                    return StateStatus.LOST;

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
     * Returns a crate we can use to test stuff.
     * 
     * @param   location    The location of the crate.
     * @return              Crate object or null if not found.
     */
    public SokoCrate getCrate(int location, int direction) {
        if(!this.hasCrate(location, direction))
            return null;
        return this.crates.get(location + direction);
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
     * Returns the last performed move.
     * 
     * @return  The last move performed.
     */
    public char getLastMove() {
        
        // No moves yet
        if(this.moveCount <= 0)
            return '!';
            
        // Get last move
        return this.history.charAt(this.moveCount - 1);
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
    public int getMoveCount() {
        return this.moveCount;
    }

    /**
     * The number of crate moves performed so far.
     * 
     * @return  How many crate moves have we done.
     */
    public int getCrateMoveCount() {
        return this.crateMoveCount;
    }

    /**
     * The number of turns performed so far.
     * 
     * @return  How turns have we done.
     */
    public int getTurnCount() {
        return this.turnCount;
    }

    /**
     * Updates the serials for the object.
     */
    private void computeSerial() {
               
        // The serial
        long number = 0;
        short count = 0;
        int packingSize = 64 / (Location.maskLength << 1);

        // So we can convert to bigint
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Serialize the state
        number += this.player;
        count += 1;
        
        // Encode the crates
        for(int crate : this.crates.keySet()) {
            number <<= (Location.maskLength << 1);
            number += crate;
            count += 1;

            // Serialize the state
            // Unfortunately needs a try catch
            if(count % packingSize == 0) {
                try {
                    dos.writeLong(number);
                    number = 0;

                // Exception handler
                } catch (IOException e) {
                    System.out.println("Something went wrong during state serialization.");
                    e.printStackTrace();
                }    
            }
        }

        // Write the remaining part if it hasn't been
        if(count % packingSize != 0) {
            try {
                dos.writeLong(number);
            } catch(IOException e) {
                System.out.println("Something went wrong during state serialization.");
                e.printStackTrace();
            }
        }

        // Convert to bigint
        this.stateSerial = Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * This allows us to check whether or not we hit the same state twice.
     * Hitting the same state twice indicates a loop, which we try to avoid.
     * 
     * @return  A hash or serial that uniquely represents the state.
     */
    public String getSerial() {

        // If it's already defined
        if(this.stateSerial != null)
            return this.stateSerial;

        // Update serials
        this.computeSerial();

        // Return
        return this.stateSerial;
    }

    /**
     * Returns an estimate of the cost of the state.
     * Higher cost means less priority.
     * Note that we use integers so things are computed much faster.
     * 
     * @return  The estimate of the cost for the state.
     */
    public int getCost(SokoMap map) {

        // The crate-based heuritic
        int crateCount = this.crates.size();
        int crateC = this.crateCentroid;
        int goalC = map.getGoalCentroid();
        int cx = Location.decodeX(crateC) - Location.decodeX(goalC); 
        int cy = Location.decodeY(crateC) - Location.decodeY(goalC); 

        // Let's see how this does
        int crateCost = 0;
        for(int crate : this.crates.keySet())
            crateCost += map.getCellCost(crate);
        
        // C represents the approximate "distance" of all crates from the goals
        float c = (cx * cx + cy * cy) / (crateCount * crateCount);

        // History length and successful crate placements
        float h = 
            +this.moveCount * HEURISTIC_WEIGHT_MOVE_COUNT + 
            +this.turnCount * HEURISTIC_WEIGHT_TURN_COUNT + 
            +this.crateMoveCount * HEURISTIC_WEIGHT_CRATE_MOVE_COUNT +
            +crateCost;
        
        // Number of good crates
        float g = this.getGoodCrateCount();

        float cHeuristic = Heuristic.weight(
            HEURISTIC_INVERT_DISTANCE
                ? Heuristic.invert(Heuristic.bias(c, HEURISTIC_BIAS_DISTANCE))
                : Heuristic.bias(c, HEURISTIC_BIAS_DISTANCE),
            HEURISTIC_WEIGHT_DISTANCE
        );

        float hHeuristic = Heuristic.weight(
            HEURISTIC_INVERT_SOLUTION 
                ? -Heuristic.bias(h, HEURISTIC_BIAS_SOLUTION)
                : Heuristic.bias(h, HEURISTIC_BIAS_SOLUTION),
            HEURISTIC_WEIGHT_SOLUTION
        );

        float gHeuristic = Heuristic.weight(
            HEURISTIC_INVERT_GOOD_COUNT 
                ? Heuristic.invert(Heuristic.bias(g, HEURISTIC_BIAS_GOOD_COUNT))
                : Heuristic.bias(g, HEURISTIC_BIAS_GOOD_COUNT),
            HEURISTIC_WEIGHT_GOOD_COUNT
        );

        return (int) (hHeuristic * gHeuristic * cHeuristic);
    }
}
