/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 18:46:54
 * @ Modified time: 2024-10-03 18:55:38
 * @ Description:
 * 
 * Utility functions for compressing location information into integers.
 * Helps us encode states much more effectively.
 * Only has static methods.
 */

package solver.utils;

public class Location {

    // Mask length and mask itself
    public static final int maskLength = 8;
    public static final int mask = (1 << maskLength) - 1;

    /**
     * Returns a single integer that stores information about a coordinate pair.
     * 
     * @param   x   The x-coordinate of the location.
     * @param   y   The y-coordinate of the location.
     * @return      A single integer representing the location.
     */
    public static int encode(int x, int y) {
        return 
            (x & mask) << maskLength |
            (y & mask);
    }

    /**
     * Returns an array containing x and y coords.
     *  
     * @param   location    The location integer.
     * @return              A pair containing the coords of the original point.
     */
    public static int[] decode(int location) {
        return new int[] {
            location >> maskLength,
            location & mask,
        };
    }
}
