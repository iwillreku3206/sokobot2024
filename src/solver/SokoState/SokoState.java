/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:47:30
 * @ Modified time: 2024-10-03 20:41:44
 * @ Description:
 * 
 * A class that represents the state of the game at any given time.
 * Stores information about the states of the creates too.
 * Note that this class only stores information that changes across states.
 */

package solver.SokoState;

import java.util.ArrayList;
import java.util.List;

import solver.SokoObjects.SokoCrate;
import solver.utils.Location;

public class SokoState {
    
    // A reference to actual crate objects
    // We use these for convenience of computations
    private List<SokoCrate> crateObjects;

    // The location of the crates we have
    private List<Integer> crates;

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
    public SokoState(int player, int[] crates, char[][] map) {

        // Init the arrays
        this.crateObjects = new ArrayList<>();
        this.crates = new ArrayList<>();
        
        // Set the locations
        this.player = player;

        for(int i = 0; i < crates.length; i++) {
            int crateLocation = crates[i];

            this.crates.add(crateLocation);
            // this.crateObjects.add(
            //     SokoCrate
            //         .create(crateLocation)
            //         .setN(map)
            //         .build());
        }
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
