/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-07 16:43:50
 * @ Modified time: 2024-10-07 20:03:00
 * @ Description:
 * 
 * Grid-related helpers.
 */

package solver.utils;

public class Grid {

    // Grid directions
    public static final byte NORTH  = (byte) 0b11000000; 
    public static final byte EAST   = (byte) 0b00110000; 
    public static final byte SOUTH  = (byte) 0b00001100; 
    public static final byte WEST   = (byte) 0b00000011; 

    // North, east, south, and west states
    public static final byte[] ADJS = {
        NORTH,
        EAST,
        SOUTH,
        WEST,
    };
}
