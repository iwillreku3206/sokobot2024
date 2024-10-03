/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 22:44:08
 * @ Modified time: 2024-10-03 22:59:57
 * @ Description:
 * 
 * The sole duty of this class is to instantiate specific instances of the state.
 */

package solver.SokoStateObjects;

public class SokoStateFactory {
    
    /**
     * Creates an initial state with no history.
     * 
     * @param   player      An integer representing the location of the player.
     * @param   crates      Integers representing the location of the crates.
     * @param   map         The map that contextualizes the information of the player and crates.
     */
    public static SokoState createInitialState(int player, int[] crates, SokoMap map) {
        return new SokoState(player, crates, map, "");
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves north.
     */
    public static SokoState createNextStateNorth(SokoState currentState, SokoMap map) {
        return null;    // ! todo
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves east.
     */
    public static SokoState createNextStateEast(SokoState currentState, SokoMap map) {
        return null;    // ! todo
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves west.
     */
    public static SokoState createNextStateWest(SokoState currentState, SokoMap map) {
        return null;    // ! todo
    }

    /**
     * Creates a new state with the associated movement.
     * 
     * @param   currentState    The state we're currently in.
     * @param   map             The map context of the state.
     * @return                  A new state if the player moves south.
     */
    public static SokoState createNextStateSouth(SokoState currentState, SokoMap map) {
        return null;    // ! todo
    }
}
