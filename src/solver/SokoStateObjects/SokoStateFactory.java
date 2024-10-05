/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 22:44:08
 * @ Modified time: 2024-10-05 19:41:57
 * @ Description:
 * 
 * The sole duty of this class is to instantiate specific instances of the state.
 */

package solver.SokoStateObjects;

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

import solver.SokoObjects.SokoCrate;
import solver.utils.Location;

public class SokoStateFactory {

    // A mapping from directions to moves
    private static Map<Integer, String> DIRECTION_TO_MOVE_MAP = Map.of(
        Location.NORTH, "u",
        Location.EAST, "r",
        Location.WEST, "l",
        Location.SOUTH, "d"
    );
    
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
     * Creates the next state given the current state and direction specified by the player move.
     * 
     * @param   currentState    The current state to move from.
     * @param   moveDirection   The player move to use.
     * @param   map             The map to contextualize the move.
     * @return                  The next state (if its valid).
     */
    public static SokoState createNextState(SokoState currentState, int moveDirection, SokoMap map) {

        // Current player properties
        int player = currentState.getPlayer();
        int newPlayer = player + moveDirection;

        // Other state properties
        int[] crates = currentState.getCrateLocations();
        String history = currentState.getHistory();
        int historyLength = currentState.getHistoryLength();

        // If no wall, check if it has a crate
        switch(currentState.getObstacle(player, moveDirection, map)) {
            
            // Player is obstructed
            case 'w': return null;

            // Player and a crate moves
            case 'c': 
                
                // Grab the crate
                SokoCrate crate = currentState.getCrate(player, moveDirection);

                // If crate can't move
                if(!crate.canMove(moveDirection))
                    return null;

                // Otherwise, move crate
                for(int i = 0; i < crates.length; i++)
                    if(crates[i] == newPlayer)
                        crates[i] += moveDirection;

                // Create new state
                return SokoStateFactory.createMoveStateWCrate(
                    newPlayer, 
                    crates, 
                    map, 
                    history, 
                    historyLength,
                    DIRECTION_TO_MOVE_MAP.get(moveDirection));

            // Only the player moves
            default: 
                return SokoStateFactory.createMoveState(
                    newPlayer, 
                    crates, 
                    map, 
                    history, 
                    historyLength, 
                    DIRECTION_TO_MOVE_MAP.get(moveDirection));
        }
    }
}
