/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 18:36:35
 * @ Modified time: 2024-10-04 23:40:17
 * @ Description:
 * 
 * Stores a queue containing the states we plan to inspect, ordered by "importance".
 * We'll figure out a way to measure "importance" later on.
 */

package solver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import solver.SokoStateObjects.SokoMap;
import solver.SokoStateObjects.SokoState;
import solver.SokoStateObjects.SokoStateComparator;
import solver.SokoStateObjects.SokoStateFactory;
import solver.utils.Location;

public class SokoSolver {

    // The actual map that stores other info common to the states
    // For instance, wall and goal placement
    private SokoMap map;
    
    // A queue of the states we plan to inspect
    // Note that states are self-contained and need no references to other states to explain themselves.
    // Look at the SokoState file for more info.
    private PriorityQueue<SokoState> states;

    // Visited states 
    private Set<BigInteger> visitedStates;

    /**
     * Initialize the game.
     * Initially, we should have a single state in the queue.
     * We then add the next possible VALID states.
     */
    public SokoSolver(char[][] charMap) {

        // Create the map
        this.map = new SokoMap(charMap);

        // Init visited
        this.visitedStates = new TreeSet<>();

        // Create the comparator
        SokoStateComparator comparator = new SokoStateComparator(this.map);

        // Init the priority queue with an initial size of 32
        // The comparator compares the states priority evaluations
        this.states = new PriorityQueue<SokoState>(32, comparator);

        // The initial state
        SokoState initialState = SokoStateFactory.createInitialState(
            this.getInitialPlayerState(charMap), 
            this.getInitialCratesState(charMap), 
            this.map);

        // Add initial state to queue
        this.states.add(initialState);
    }

    /**
     * Returns an integer representing the initial location of the player in the map.
     * 
     * @param   charMap     The map to lift information from.   
     * @return              The location of the player read from the map.
     */
    private int getInitialPlayerState(char[][] charMap) {

        // Return the location of the player
        for(int y = 0; y < charMap.length; y++)
            for(int x = 0; x < charMap[0].length; x++)
                if(charMap[y][x] == '@' || charMap[y][x] == '+')
                    return Location.encode(x, y);

        // No player found, should not happen
        return -1;
    }

    /**
     * Returns an array of integers representing the locations of the crates in the map. 
     * 
     * @param   charMap     The map to lift information from.
     * @return              The location of the crates.
     */
    private int[] getInitialCratesState(char[][] charMap) {

        // List of crate locs
        List<Integer> crates = new ArrayList<>();

        // Append crates
        for(int y = 0; y < charMap.length; y++)
            for(int x = 0; x < charMap[0].length; x++)
                if(charMap[y][x] == '$' || charMap[y][x] == '*')
                    crates.add(Location.encode(x, y));
                    
        // Return an array of ints
        return crates
            .stream()
            .mapToInt(i -> i)
            .toArray();
    }

    /**
     * Attempts to solve the puzzle.
     * 
     * @return  A string containing the attempted solution.
     */
    public String solve() {
        
        // While we have states to inspect
        while(!this.states.isEmpty()) {
            
            // Get the latest in the queue
            SokoState state = this.states.poll();

            // Add the state serials to their sets
            this.visitedStates.add(state.getSerial());

            // If we won
            if(state.getStatus(this.map) == SokoState.StateStatus.WON)
                return state.getHistory();

            // If the state is a dud
            if(state.getStatus(this.map) == SokoState.StateStatus.LOST)
                continue;

            // Otherwise, keep checking
            SokoState[] newStates = {
                SokoStateFactory.createNextStateNorth(state, this.map),
                SokoStateFactory.createNextStateEast(state, this.map),
                SokoStateFactory.createNextStateWest(state, this.map),
                SokoStateFactory.createNextStateSouth(state, this.map),
            };

            // Add the valid states we haven't visited
            for(SokoState newState : newStates) {
                
                // Invalid state
                if(newState == null)
                    continue;

                // The state has been visited
                if(this.visitedStates.contains(newState.getSerial()))
                    continue;

                // Otherwise, queue the state
                this.states.add(newState);
            }
        }

        return "No solution found.";
    }
}
