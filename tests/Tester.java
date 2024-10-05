/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-05 00:30:11
 * @ Modified time: 2024-10-05 13:58:47
 * @ Description:
 * 
 * Helps us automate testing.
 */

package tests;

public class Tester {

    /**
     * Runs an isolated test.
     * 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        
        // Grab the test name and file name
        if(args.length < 2)
            return;

        String testName = args[0];  
        String mapName = args[1];
        
        // Create the test and run it
        Test test = new Test(testName, mapName);
        test.run();
        test.end();

        // Force exit to kill all threads
        // Our threads don't wanna die through .interrupt() smh
        System.exit(0);
    }
}
