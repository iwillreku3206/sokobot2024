/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-07 16:43:50
 * @ Modified time: 2024-10-07 16:45:45
 * @ Description:
 * 
 * Grid-related helpers.
 */

package solver.utils;

public class Grid {

    // Grid directions
    public static final byte NORTH  = 0b01000000; 
    public static final byte EAST   = 0b00010000; 
    public static final byte SOUTH  = 0b00000100; 
    public static final byte WEST   = 0b00000001; 

    // North, east, south, and west states
    public static final byte[] ADJS = {
        NORTH,
        EAST,
        SOUTH,
        WEST,
    };
}
