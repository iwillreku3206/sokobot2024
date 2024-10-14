/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-05 00:32:46
 * @ Modified time: 2024-10-05 14:12:14
 * @ Description:
 * 
 * Represents an isolated test.
 */

package tests;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import reader.FileReader;
import reader.MapData;
import solver.SokoBot;
import tests.Mocks.TestGamePanel;

public class Test {

    // Some metadata about the run
    private String name;
    private String mapName;

    // Components
    private JFrame frame;
    private TestGamePanel panel;

    /**
     * Instantiates a new test.
     */
    public Test(String name, String mapName) {

        this.name = name;
        this.mapName = mapName;

        // Create the components
        this.frame = new JFrame();
        this.panel = new TestGamePanel();

        // Configure the components
        this.frame.setSize(1000, 800);
        this.frame.setLocationRelativeTo(null);
        this.frame.setTitle(name);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Read the map file
        FileReader reader = new FileReader();
        MapData map = reader.readFile(mapName);
            
        // Add the panel to the frame
        frame.add(this.panel);
        frame.setVisible(true);
        frame.repaint();

        // Load the map
        panel.loadMap(map);
        panel.initiateSolution();
    }

    /**
     * Runs the test and returns the output.
     * @throws InterruptedException 
     */
    public void run() throws InterruptedException {

        // Start the solve
        panel.keyPressed(new KeyEvent(panel, 0, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' '));

        // Wait for the game to finish...
        while(!panel.isDone()) {
            Thread.sleep(500);
        }

        // Once done, log the status
        this.log();
    }

    /**
     * Logs the result of the test.
     */
    private void log() {
        FileReader reader = new FileReader();
        MapData map = reader.readFile(this.mapName);

        String testStats[] = {
            "Test Name:          " + name,
            "Test File:          " + mapName,
            "Time Taken:         " + this.panel.getTime(),
            "Number of Moves:    " + this.panel.getMoves(),
            "Number of Crates:   " + this.panel.getCrates(),
            "Won:                " + this.panel.hasWon(),
            "Solution:           " + this.panel.getSolution(),
            "Width of map:       " + map.columns,
            "Height of map:      " + map.rows,
            "Number of Blocks:   " + map.tiles.length
        };

        StringBuilder statisticsOut = new StringBuilder();
        for (String testStat : testStats){
            statisticsOut.append(testStat + "\n");
        }

        // Printing to console takes a while. Preprocess it 
        System.out.println(statisticsOut);

    }
    /**
     * Closes the test and performs cleanup.
     */
    public void end() {
        this.frame.dispose();
    }
}
