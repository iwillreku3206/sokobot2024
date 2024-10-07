/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:56:07
 * @ Modified time: 2024-10-07 15:04:14
 * @ Description:
 * 
 * A class that represents a crate's state.
 * This is important for us to know whether or not a crate is stuck or not.
 * We have two types of stuck:
 * 
 *      A. Permanently stuck:
 *          At least two adjacent sides of the crate are walls.
 *      B. Temporarily stuck:
 *          At least two adjacent sidesof the crate are walls or crates.
 * 
 * We know we've reached a bum state when either 
 * 
 *      (1) all crates are temporarily stuck (B)            AND at least one of them is not on a goal
 *      (2) at least one crate is permanently stuck (A)     AND at least one of them is not on a goal
 */

package solver.SokoObjects;

import solver.utils.*;

public class SokoCrate {

    // North, east, south, and west states
    public static final byte[] STUCKSTATES = {
        (byte) 0b01000000,  // North
        (byte) 0b00010000,  // East
        (byte) 0b00000100,  // South
        (byte) 0b00000001,  // West
    };

    // Bit combinations for crates that are temporarily stuck 
    public static final byte[] STUCKSTATES_TEMPORARY = {
        
        (byte) (STUCKSTATES[0] + STUCKSTATES[1]),   // North and east are occupied by wall or crate
        (byte) (STUCKSTATES[1] + STUCKSTATES[2]),   // East and south are occupied by wall or crate
        (byte) (STUCKSTATES[2] + STUCKSTATES[3]),   // South and west are occupied by wall or crate
        (byte) (STUCKSTATES[3] + STUCKSTATES[0]),   // West and north are occupied by wall or crate
    };

    // Bit combinations representing crates that are permanently stuck
    public static final byte[] STUCKSTATES_PERMANENT = {

        (byte) (STUCKSTATES_TEMPORARY[0] + (STUCKSTATES_TEMPORARY[0] << 1)),  // North and east are occupied by wall
        (byte) (STUCKSTATES_TEMPORARY[1] + (STUCKSTATES_TEMPORARY[1] << 1)),  // East and south are occupied by wall
        (byte) (STUCKSTATES_TEMPORARY[2] + (STUCKSTATES_TEMPORARY[2] << 1)),  // South and west are occupied by wall
        (byte) (STUCKSTATES_TEMPORARY[3] + (STUCKSTATES_TEMPORARY[3] << 1)),  // West and north are occupied by wall
    };

    // VERY IMPORTANT: The 'neighbors' variable does not represent the number of neighbors for a given crate.
    // This represents the state of the adjacent cells of the crate.
    //
    // For the given byte 
    //
    //      0b00000000
    //
    //        ^^        These two bits represent the North neighbor          
    //          ^^      ''    ''  ''   ''        ''  East neighbor          
    //            ^^    ''    ''  ''   ''        ''  South neighbor          
    //              ^^  ''    ''  ''   ''        ''  West neighbor
    // 
    // If a given pair of bits shows 01, it means an adjacent CRATE is present in that location.
    // If a given pair of bits shows 11, it means an adjacent WALL is present there.
    //
    // We do this for the sake of convenience (when checking what stuff are beside the crate)
    // and for efficiency (bitwise ops are fast)
    private byte neighbors;

    // The location of the crate
    private int location;

    // Whether or not the crate is on a goal
    private boolean isGood;
    
    /**
     * Creates a new crate state based on provided input.
     * 
     * @param   builder     The builder whose state to use for init.
     */
    private SokoCrate(Builder builder) {
        this.location = builder.location;
        this.neighbors = builder.neighbors;
    }

    /**
     * Returns whether or not the crate is stuck permanently.
     * 
     * @return  The permanent stuckness state of the crate.
     */
    public boolean isStuckPermanently() {

        // For each of the permanent stuck states, check if its the case that at least one is satisfied
        for(int i = 0; i < 4; i++)
            if((STUCKSTATES_PERMANENT[i] & this.neighbors) == STUCKSTATES_PERMANENT[i])
                return true;

        // Otherwise, return false
        return false;
    }

    /**
     * Returns whether or not the crate is stuck temporarily.
     * 
     * @return  The temporary stuckness state of the crate.
     */
    public boolean isStuckTemporarily() {

        // If it's already stuck permanently, it can't be stuck temporarily
        if(this.isStuckPermanently())
            return false;
        
        // For each of the temporary stuck states, check if its the case that at least one is satisfied
        for(int i = 0; i < 4; i++)
            if((STUCKSTATES_TEMPORARY[i] & this.neighbors) == STUCKSTATES_TEMPORARY[i])
                return true;
                
        // Otherwise, return false
        return false;
    }

    /**
     * Returns whether or not the crate can move in the given direction.
     * 
     * @param   direction   The direction to check.
     * @return              Whether or not the move is viable.
     */
    public boolean canMove(int direction) {
        
        // Return appropriate check
        switch(direction) {
            case Location.NORTH:    return (STUCKSTATES[0] & this.neighbors) != STUCKSTATES[0];
            case Location.EAST:     return (STUCKSTATES[1] & this.neighbors) != STUCKSTATES[1];
            case Location.SOUTH:    return (STUCKSTATES[2] & this.neighbors) != STUCKSTATES[2];
            case Location.WEST:     return (STUCKSTATES[3] & this.neighbors) != STUCKSTATES[3];
        }

        // Invalid value
        return false;
    }

    /**
     * Returns the location of the crate, as a single integer.
     * 
     * @return  A single int holding the crate location.
     */
    public int getLocation() {
        return this.location;
    }

    /**
     * I really hate how we have to break this class's immutable nature... but here we are for optimization.
     * 
     * @param   isGood  Whether or not the crate is on top of a goal.
     */
    public void setGood(boolean isGood) {
        this.isGood = isGood; 
    }

    /**
     * Returns whether or not the crate is good.
     * 
     * @return  A boolean indicating whether or not the crate is on a goal.
     */
    public boolean isGood() {
        return this.isGood;
    }

    /**
     * The builder class to help us create Crate states.
     */
    public static class Builder {
        
        // The default state
        public byte neighbors = 0;
        public int location = 0;

        // Location is always required
        public Builder(short x, short y) {
            this.location = Location.encode(x, y);
        }

        // Location is always required
        public Builder(int location) {
            this.location = location;
        }

        // Set the north neighbor
        public Builder setN(char c) { 
            this.neighbors |= 
                (c == 'w' ? 0b11000000 : 
                (c == 'c' ? 0b01000000 : 0b00000000));
            return this;
        };

        // Set the east neighbor
        public Builder setE(char c) { 
            this.neighbors |= 
                (c == 'w' ? 0b00110000 : 
                (c == 'c' ? 0b00010000 : 0b00000000));
            return this;
        };

        // Set the south neighbor
        public Builder setS(char c) { 
            this.neighbors |= 
                (c == 'w' ? 0b00001100 : 
                (c == 'c' ? 0b00000100 : 0b00000000));
            return this;
        };

        // Set the west neighbor
        public Builder setW(char c) { 
            this.neighbors |= 
                (c == 'w' ? 0b00000011 : 
                (c == 'c' ? 0b00000001 : 0b00000000));
            return this;
        };

        // Build the crate state
        public SokoCrate build() {
            return new SokoCrate(this);
        }
    }

    /**
     * Requests for a new instance of the crate state builder.
     * 
     * @param   x   The x-coordinate of the crate.
     * @param   y   The y-coordinate of the crate.
     * @return      A new builder for the crate state.
     */
    public static Builder create(short x, short y) {
        return new Builder(x, y);
    }

    /**
     * Requests for a new instance of the crate state builder.
     * 
     * @param   location    An int representing the location of the crate.
     * @return              A new builder for the crate state.
     */
    public static Builder create(int location) {
        return new Builder(location);
    }
}
