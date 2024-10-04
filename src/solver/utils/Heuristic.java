/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-04 20:18:22
 * @ Modified time: 2024-10-04 22:25:22
 * @ Description:
 * 
 * Helper functions for the heuristic computation.
 * These are all static.
 */

package solver.utils;

public class Heuristic {
    
    /**
     * Linear interpolation between two values by amount.
     * 1.0 means x1, 0.0 means x2, 0.5 means their average.
     * 
     * @param   x1          The first value.
     * @param   x2          The second value.
     * @param   amount      How much to interpolate between the two.
     * @return              The interpolated value.
     */
    private static float lerp(float x1, float x2, float amount) {
        
        // Clamp the amount first
        amount = amount < 0 ? 0 : amount;
        amount = amount > 1 ? 1 : amount;

        // Do the lerp
        return x1 * amount + x2 * (1 - amount);
    }

    /**
     * Returns the inverse of a value.
     * Prevents x from being 0.
     * 
     * @param   x   The value to invert.
     * @return      The reciprocal of the value.
     */
    public static float invert(float x) {
        
        // Clamp x to 0
        x = x < 0 ? 0 : x;

        // Return inverted value
        return 1 / (x + 1);
    }

    /**
     * Lerps a function with 1 (since all heuristics should be multiplied).
     * 
     * @param   x       The value to lerp with 1.
     * @param   weight  The amount to lerp by.
     * @return          The lerped value.
     */
    public static float weight(float x, float weight) {

        // Do a positive weighting
        if(weight >= 0)
            return lerp(x, 1, weight);

        // Do a negative weighting
        return -lerp(x, 1, -weight);
    }

    /**
     * Adds a bias to a given value.
     * 
     * @param   x       The value to bias.
     * @param   bias    The bias to add.
     * @return          The biased value.
     */
    public static float bias(float x, float bias) {
        return x + bias;
    }
}
