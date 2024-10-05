# Intelligent Systems Through Sukoban

> **Tentative Outline of README**
> **Tentative Outline of README**
> 
1. Overview

    1.1 brief description of game

    1.2 NP-hard problems

2. A Foreword on Implementation Technicalities 

    2.1 Storing coordinates core effectively

        * using a single integer and sharing bits

    2.2 Separating state from constants

        * using a class for the state + another class for what doesn't change
   
    2.3 Rationale of overarching design patterns

        * using a state factory

        * having a separate crate entity to facilitate code expressiveness

3. Approach

    3.1 State search

    3.2 Types of states

        * winning condition (all crates on goals)
    
        * at least one crate permanently stuck
    
        * all crates temporarily stuck

    3.3 State serialization
    
        * helps avoid repeating states

    3.4 State priorities
    
        3.4.1 Move count heuristic
    
        3.4.2 Centroid distance heuristic
    
        3.4.3 Good crates heuristic

4. Testing Framework
   
    4.1 Testing approach
    
        * to automate the testing process, a mock of the original Java files were created
    
        * These were then used to check and play the solutions the bot would be given
    
        * it was easier to copy over the provided implementation of the game rather than to code one from scratch
    
        * also it looked more visually appealing to watch

    4.1 The Test class
    
        * helps isolate tests
    
        * makes sure to instantiate the involved objects each time, so no state is preserved

    4.2 The problem with having a Java test driver 
    
        * apparently, if a method isn't finished running within a thread, calling its .interrupt() method does nothing
    
        * the only way to kill those threads would be by exiting the main program thread
    
        * it is thus necessary to start each test as a separate process

    4.3 `tester.py`
    
        * this represents the test driver

    4.4 Map generation and map corpuses
    
        * explain valid file formats 
    
        * the map generation code was lifted from [here](https://github.com/xbandrade/sokoban-solver-generator/commits?author=xbandrade)

* [x] stuck1.txt        (No Solution Found * true) 
* [x] stuck2.txt        (No Solution Found * true) 
* [x] base1.txt
* [x] base2.txt
* [x] base3.txt
* [x] base4.txt
* [x] twoboxes1.txt
* [x] twoboxes2.txt
* [x] twoboxes3.txt
* [x] threeboxes1.txt
* [x] threeboxes2.txt
* [x] threeboxes3.txt
* [x] fourboxes1.txt
* [x] fourboxes2.txt 
* [x] fourboxes3.txt
* [x] fiveboxes1.txt
* [x] fiveboxes2.txt
* [x] fiveboxes3.txt
* [x] original1.txt
* [ ] original2.txt     (TLE)
* [ ] original3.txt     (TLE)


This is actually pretty hard.

Note: if class instantiation becomes a significant overhead, we might refactor our code to use static methods instead (i.e., because OOP is very inefficient for these types of applications, we might resort to procedural idioms if our algo is still shit).

TODO

1. replace headers with images (figma)
2. complete the missing sections





So... refactor the StateFactory class... too much redundancy.