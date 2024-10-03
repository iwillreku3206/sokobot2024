/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 18:36:35
 * @ Modified time: 2024-10-03 18:39:51
 * @ Description:
 * 
 * Stores a queue containing the states we plan to inspect, ordered by "importance".
 * We'll figure out a way to measure "importance" later on.
 */

package solver.SokoState;

import java.util.PriorityQueue;

public class SokoGame {
    
    // A queue of the states we plan to inspect
    // Note that states are self-contained and need no references to other states to explain themselves.
    // Look at the SokoState file for more info.
    PriorityQueue<SokoState> states;
}
