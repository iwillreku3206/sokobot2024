/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 18:41:43
 * @ Modified time: 2024-10-04 23:41:45
 * @ Description:
 * 
 * This allows the priority queue to compare states against each other.
 */

package solver.SokoStateObjects;

import java.util.Comparator;

public class SokoStateComparator implements Comparator<SokoState> {

    // Hold a reference to the map needed to contextualize costs
    private SokoMap map;

    /**
     * Create a new comparator with a reference to the map to use.
     * It's better for this map to hold the map reference because it contextualizes the cost of the state.
     * 
     * @param   map     The map to use.
     */
    public SokoStateComparator(SokoMap map) {
        this.map = map;
    }

    /**
     * Compares to states; returns a number indicating priority of traversal.
     * 
     * @param   state1  The first state to compare.
     * @param   state2  The second state to compare.
     * @return          An integer indicating which state has higher priority.
     */
    @Override
    public int compare(SokoState stateA, SokoState stateB) {
        
        // Literally just compare their costs
        return stateA.getCost(this.map) - stateB.getCost(this.map);
    }
}
