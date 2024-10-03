/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:56:07
 * @ Modified time: 2024-10-03 18:29:16
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

package solver;

public class SokoCrate {

    // Bit combinations representing crates that are permanently stuck
    public static final byte[] STUCKSTATES_PERMANENT = {
        (byte) 0b11110000,  // North and east are occupied by wall
        (byte) 0b00111100,  // East and south are occupied by wall
        (byte) 0b00001111,  // South and west are occupied by wall
        (byte) 0b11000011,  // West and north are occupied by wall
    };

    // Bit combinations for crates that are temporarily stuck 
    public static final byte[] STUCKSTATES_TEMPORARY = {
        (byte) 0b01010000,  // North and east are occupied by wall
        (byte) 0b00010100,  // East and south are occupied by wall
        (byte) 0b00000101,  // South and west are occupied by wall
        (byte) 0b01000001,  // West and north are occupied by wall
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
    
    /**
     * Creates a new crate state based on provided input.
     * 
     * @param   neighbors   The neighbor states.
     */
    private SokoCrate(byte neighbors) {
        this.neighbors = neighbors;
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
     * The builder class to help us create Crate states.
     */
    public class Builder {
        
        // The neighbor state
        byte neighbors;

        // No default params, so leave empty
        public Builder() {}

        // Set the north neighbor
        public Builder setN(char c) { 
            this.neighbors |= c == 'c' ? 0b11000000 : 0b01000000;
            return this;
        };

        // Set the east neighbor
        public Builder setE(char c) { 
            this.neighbors |= c == 'c' ? 0b00110000 : 0b00010000;
            return this;
        };

        // Set the south neighbor
        public Builder setS(char c) { 
            this.neighbors |= c == 'c' ? 0b00001100 : 0b00000100;
            return this;
        };

        // Set the west neighbor
        public Builder setW(char c) { 
            this.neighbors |= c == 'c' ? 0b00000011 : 0b00000001;
            return this;
        };

        // Build the crate state
        public SokoCrate build() {
            return new SokoCrate(this.neighbors);
        }
    }

    /**
     * Requests for a new instance of the crate state builder.
     * 
     * @return  A new builder for the crate state.
     */
    public Builder create() {
        return new Builder();
    }
}
