/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 22:44:08
 * @ Modified time: 2024-10-04 23:12:34
 * @ Description:
 * 
 * The sole duty of this class is to instantiate specific instances of the state.
 */

package solver.SokoStateObjects;

import solver.SokoObjects.SokoCrate;
import solver.utils.Location;

public class SokoStateFactory {
    
    /**
     * Creates an initial state with no history.
     * 
     * @param   player      An integer representing the location of the player.
     * @param   crates      Integers representing the location of the crates.
     * @param   map         The map that contextualizes the information of the player and crates.
     */
    public static SokoState createInitialState(int player, int[] crates, SokoMap map) {
        return new SokoState(player, crates, false, map, "", 0);
    }

    /**
     * Creates an new state with inherited history.
     * 
     * @param   player          An integer representing the location of the player.
     * @param   crates          Integers representing the location of the crates.
     * @param   map             The map that contextualizes the information of the player and crates.
     * @param   history         The history of moves for that state.
     * @param   historyLength   The length of the history for that state.
     * @param   move            A new move by the player.
     */
    private static SokoState createMoveState(int player, int[] crates, SokoMap map, String history, int historyLength, String move) {
        return new SokoState(player, crates, false, map, history + move, historyLength + 1);
    }

    /**
     * Creates an new state with inherited history.
     * This differs from the method above in that a crate moved during this state.
     * 
     * @param   player          An integer representing the location of the player.
     * @param   crates          Integers representing the location of the crates.
     * @param   map             The map that contextualizes the information of the player and crates.
     * @param   history         The history of moves for that state.
     * @param   historyLength   The length of the history for that state.
     * @param   move            A new move by the player.
     */
    private static SokoState createMoveStateWCrate(int player, int[] crates, SokoMap map, String history, int historyLength, String move) {
        return new SokoState(player, crates, true, map, history + move, historyLength + 1);
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves north.
     */
    public static SokoState createNextStateNorth(SokoState currentState, SokoMap map) {
        
        // Player location
        int player = currentState.getPlayer();
        int[] crates = currentState.getCrateLocations();
        String history = currentState.getHistory();
        int historyLength = currentState.getHistoryLength();

        // If no wall, check if it has a crate
        switch(currentState.getObstacleNorth(player, map)) {
            
            // Player is obstructed
            case 'w': return null;

            // Player and a crate moves
            case 'c': 
                
                // Grab the crate
                SokoCrate crate = currentState.getCrate(player + Location.NORTH);

                // If crate can't move
                if(!crate.canMoveNorth())
                    return null;

                // Otherwise, move crate
                for(int i = 0; i < crates.length; i++)
                    if(crates[i] == player + Location.NORTH)
                        crates[i] += Location.NORTH;

                // Create new state
                return SokoStateFactory.createMoveStateWCrate(player + Location.NORTH, crates, map, history, historyLength, "u");

            // Only the player moves
            default: return SokoStateFactory.createMoveState(player + Location.NORTH, crates, map, history, historyLength, "u");
        }
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves east.
     */
    public static SokoState createNextStateEast(SokoState currentState, SokoMap map) {

        // Player location
        int player = currentState.getPlayer();
        int[] crates = currentState.getCrateLocations();
        String history = currentState.getHistory();
        int historyLength = currentState.getHistoryLength();

        // If no wall, check if it has a crate
        switch(currentState.getObstacleEast(player, map)) {
            
            // Player is obstructed
            case 'w': return null;

            // Player and a crate moves
            case 'c': 
                
                // Grab the crate
                SokoCrate crate = currentState.getCrate(player + Location.EAST);

                // If crate can't move
                if(!crate.canMoveEast())
                    return null;

                // Otherwise, move crate
                for(int i = 0; i < crates.length; i++)
                    if(crates[i] == player + Location.EAST)
                        crates[i] += Location.EAST;

                // Create new state
                return SokoStateFactory.createMoveStateWCrate(player + Location.EAST, crates, map, history, historyLength, "r");

            // Only the player moves
            default: return SokoStateFactory.createMoveState(player + Location.EAST, crates, map, history, historyLength, "r");
        }
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves west.
     */
    public static SokoState createNextStateWest(SokoState currentState, SokoMap map) {

        // Player location
        int player = currentState.getPlayer();
        int[] crates = currentState.getCrateLocations();
        String history = currentState.getHistory();
        int historyLength = currentState.getHistoryLength();

        // If no wall, check if it has a crate
        switch(currentState.getObstacleWest(player, map)) {
            
            // Player is obstructed
            case 'w': return null;

            // Player and a crate moves
            case 'c': 
                
                // Grab the crate
                SokoCrate crate = currentState.getCrate(player + Location.WEST);

                // If crate can't move
                if(!crate.canMoveWest())
                    return null;

                // Otherwise, move crate
                for(int i = 0; i < crates.length; i++)
                    if(crates[i] == player + Location.WEST)
                        crates[i] += Location.WEST;

                // Create new state
                return SokoStateFactory.createMoveStateWCrate(player + Location.WEST, crates, map, history, historyLength, "l");

            // Only the player moves
            default: return SokoStateFactory.createMoveState(player + Location.WEST, crates, map, history, historyLength, "l");
        }
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves south.
     */
    public static SokoState createNextStateSouth(SokoState currentState, SokoMap map) {

        // Player location
        int player = currentState.getPlayer();
        int[] crates = currentState.getCrateLocations();
        String history = currentState.getHistory();
        int historyLength = currentState.getHistoryLength();

        // If no wall, check if it has a crate
        switch(currentState.getObstacleSouth(player, map)) {
            
            // Player is obstructed
            case 'w': return null;

            // Player and a crate moves
            case 'c': 
                
                // Grab the crate
                SokoCrate crate = currentState.getCrate(player + Location.SOUTH);

                // If crate can't move
                if(!crate.canMoveSouth())
                    return null;

                // Otherwise, move crate
                for(int i = 0; i < crates.length; i++)
                    if(crates[i] == player + Location.SOUTH)
                        crates[i] += Location.SOUTH;

                // Create new state
                return SokoStateFactory.createMoveStateWCrate(player + Location.SOUTH, crates, map, history, historyLength, "d");

            // Only the player moves
            default: return SokoStateFactory.createMoveState(player + Location.SOUTH, crates, map, history, historyLength, "d");
        }
    }
}
