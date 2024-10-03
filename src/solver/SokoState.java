/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:47:30
 * @ Modified time: 2024-10-03 16:55:57
 * @ Description:
 * 
 * A class that represents the state of the game at any given time.
 * Stores information about the states of the creates too.
 * Has a copy of the map for that state.
 */

package solver;

public class SokoState {
    
    private char[][] map;
    private SokoCrate crates;
}
