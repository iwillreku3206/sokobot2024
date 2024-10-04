/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-05 00:30:11
 * @ Modified time: 2024-10-05 01:39:24
 * @ Description:
 * 
 * Helps us automate testing.
 */

package tests;

import javax.swing.JFrame;
import java.awt.AWTException;
import java.awt.event.KeyEvent;

import reader.FileReader;
import reader.MapData;
import solver.SokoBot;
import tests.Mocks.TestGamePanel;

public class Tester {

    /**
     * ! todo run the tests here
     * @throws AWTException 
     */
    public static void main(String[] args) throws AWTException {
        
        // Create mock frame
        JFrame frame = new JFrame();
        TestGamePanel panel = null;

        // !config the frame elsewhere
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ! perfect, we can load different maps!
        String[] mapNames = {
            "stuck1",
            "stuck2",
            "base1",
            "base2",
            "base3",
            "base4",
            "base5",
            "base6",
            "fiveboxes1",
            "fiveboxes2",
            "fiveboxes3",
            "fourboxes1",
            "fourboxes2",
            "fourboxes3",
            "threeboxes1",
            "threeboxes2",
            "threeboxes3",
            "twoboxes1",
            "twoboxes2",
            "twoboxes3",
            "original1",
            "original2",
            "original3",
        };
        int counter = 0;

        while(counter < mapNames.length) {

            // Wait for bot to finish
            if(panel != null && !panel.isInitting && !panel.done)
                continue;

            // Read the file
            FileReader reader = new FileReader();
            MapData map = reader.readFile(mapNames[counter++]);

            // Load the map
            panel = new TestGamePanel();

            // Add the panel to the frame
            frame.add(panel);
            frame.setVisible(true);
            frame.repaint();

            // Load the map
            panel.loadMap(map);
            panel.initiateSolution();
            panel.keyPressed(new KeyEvent(panel, 0, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' '));

        }
    }
}
