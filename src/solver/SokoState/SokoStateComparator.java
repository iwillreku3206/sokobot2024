/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 18:41:43
 * @ Modified time: 2024-10-03 18:45:20
 * @ Description:
 * 
 * This allows the priority queue to compare states against each other.
 */

package solver.SokoState;

import java.util.Comparator;

public class SokoStateComparator implements Comparator<SokoState> {

    /**
     * Compares to states; returns a number indicating priority of traversal.
     * 
     * @param   state1  The first state to compare.
     * @param   state2  The second state to compare.
     * @return          An integer indicating which state has higher priority.
     */
    @Override
    public int compare(SokoState stateA, SokoState stateB) {
        
        // ! todo update this implementation
        // ! smth like stateA.getPriorityEvaluation() - stateB.getPriorityEvaluation()
        return 1;
    }
}
