/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-03 16:56:07
 * @ Modified time: 2024-10-07 20:10:16
 * @ Description:
 * 
 * A class that represents a crate's state.
 * This is important for us to know whether or not a crate is stuck or not.
 * (Or rather to separate the logic for determining stuckness more cleanly).
 */

package solver.SokoObjects;

import java.util.Map;
import java.util.Set;

import solver.utils.*;

public class SokoCrate {

    // Bit combinations for crates that are temporarily stuck 
    public static final byte[] STUCKSTATES = {
        
        (byte) (Grid.NORTH  + Grid.EAST),   // North and east are occupied by wall or crate
        (byte) (Grid.EAST   + Grid.SOUTH),  // East and south are occupied by wall or crate
        (byte) (Grid.SOUTH  + Grid.WEST),   // South and west are occupied by wall or crate
        (byte) (Grid.WEST   + Grid.NORTH),  // West and north are occupied by wall or crate
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
    // If a given pair of bits shows 11, it means an adjacent WALL or CRATE is present there.
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
     * Returns whether or not the crate is stuck temporarily.
     * 
     * @return  The temporary stuckness state of the crate.
     */
    public boolean isStuck() {
        
        // For each of the temporary stuck states, check if its the case that at least one is satisfied
        for(int i = 0; i < 4; i++)
            if((STUCKSTATES[i] & this.neighbors) == STUCKSTATES[i])
                return true;
                
        // Otherwise, return false
        return false;
    }

    /**
     * Returns whether or not the crate is stuck with other crates that are stuck.
     * 
     * @param   crates          All crates.
     * @param   unstuckCrates   Crates that are unstuck.
     * @param   visitedCrates   Crates visited by this method and other method calls on other crates.
     * @return                  Whether or not the crate is stuck in a group.
     */
    public boolean isStuckInAGroup(Map<Integer, SokoCrate> crates, Set<Integer> unstuckCrates, Set<Integer> visitedCrates) {
        
        // If visited, just return its stuck state.
        if(visitedCrates.contains(this.location))
            return !unstuckCrates.contains(this.location) && !this.isGood;

        // Add to visited
        visitedCrates.add(this.location);

        // All neighbors are stuck
        boolean allNeighborsAreStuck = true;

        // Check if all neighbors are indeed stuck
        for(int direction : Location.DIRECTIONS)
            if(crates.containsKey(this.location + direction))
                if(!crates.get(this.location + direction).isStuckInAGroup(crates, unstuckCrates, visitedCrates))
                    allNeighborsAreStuck = false;

        // Otherwise, return the stuck state of the other crates and itself
        return !unstuckCrates.contains(this.location) && !this.isGood && allNeighborsAreStuck;
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
            case Location.NORTH:    return (Grid.NORTH & this.neighbors)    != Grid.NORTH;
            case Location.EAST:     return (Grid.EAST & this.neighbors)     != Grid.EAST;
            case Location.SOUTH:    return (Grid.SOUTH & this.neighbors)    != Grid.SOUTH;
            case Location.WEST:     return (Grid.WEST & this.neighbors)     != Grid.WEST;
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
        public Builder setN(boolean hasNeighbor) { 
            this.neighbors |= hasNeighbor ? Grid.NORTH : 0;
            return this;
        };

        // Set the east neighbor
        public Builder setE(boolean hasNeighbor) { 
            this.neighbors |= hasNeighbor ? Grid.EAST : 0;
            return this;
        };

        // Set the south neighbor
        public Builder setS(boolean hasNeighbor) { 
            this.neighbors |= hasNeighbor ? Grid.SOUTH : 0;
            return this;
        };

        // Set the west neighbor
        public Builder setW(boolean hasNeighbor) { 
            this.neighbors |= hasNeighbor ? Grid.WEST : 0;
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
