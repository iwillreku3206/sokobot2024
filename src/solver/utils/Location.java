/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 18:46:54
 * @ Modified time: 2024-10-07 21:43:12
 * @ Description:
 * 
 * Utility functions for compressing location information into integers.
 * Helps us encode states much more effectively.
 * Only has static methods.
 */

package solver.utils;

public class Location {

    // Mask length and mask itself
    public static final int maskLength = 5;
    public static final int mask = (1 << maskLength) - 1;

    // North, east, south and west helpers
    public static final int NORTH = -1;
    public static final int SOUTH = 01;
    public static final int EAST = (01 << maskLength);
    public static final int WEST = (-1 << maskLength);

    // An array for easy iters
    public static final int[] DIRECTIONS = {
        NORTH, 
        SOUTH,
        EAST,
        WEST,
    };

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
    public static short[] decode(int location) {
        return new short[] {
            (short) (location >> maskLength),
            (short) (location & mask),
        };
    }

    /**
     * Returns x coords.
     *  
     * @param   location    The location integer.
     * @return              The x-coordinate.
     */
    public static short decodeX(int location) {
        return (short) (location >> maskLength);
    }

    /**
     * Returns y coords.
     *  
     * @param   location    The location integer.
     * @return              The y-coordinate.
     */
    public static short decodeY(int location) {
        return (short) (location & mask);
    }
}
