/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 18:36:35
 * @ Modified time: 2024-10-03 20:59:27
 * @ Description:
 * 
 * Stores a queue containing the states we plan to inspect, ordered by "importance".
 * We'll figure out a way to measure "importance" later on.
 */

package solver.SokoState;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import solver.utils.Location;

public class SokoGame {

    // The actual map that stores other info common to the states
    // For instance, wall and goal placement
    private SokoMap map;
    
    // A queue of the states we plan to inspect
    // Note that states are self-contained and need no references to other states to explain themselves.
    // Look at the SokoState file for more info.
    private PriorityQueue<SokoState> states;

    /**
     * Initialize the game.
     * Initially, we should have a single state in the queue.
     * We then add the next possible VALID states.
     */
    public SokoGame(char[][] charMap) {

        // Create the map
        this.map = new SokoMap(charMap);

        // Init the priority queue with an initial size of 32
        // The comparator compares the states priority evaluations
        this.states = new PriorityQueue<SokoState>(32, new SokoStateComparator());

        // The initial state
        SokoState initialState = new SokoState(
            this.getInitialPlayerState(charMap), 
            this.getInitialCratesState(charMap), 
            charMap);

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
            for(int x = 0; x < charMap.length; x++)
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
            for(int x = 0; x < charMap.length; x++)
                if(charMap[y][x] == '$' || charMap[y][x] == '*')
                    crates.add(Location.encode(x, y));
                    
        // Return an array of ints
        return crates
            .stream()
            .mapToInt(i -> i)
            .toArray();
    }
}
