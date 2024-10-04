/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-05 00:30:11
 * @ Modified time: 2024-10-05 02:57:30
 * @ Description:
 * 
 * Helps us automate testing.
 */

package tests;

import javax.swing.JFrame;
import java.awt.AWTException;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

import reader.FileReader;
import reader.MapData;
import tests.mocks.TestGamePanel;

public class Tester {

    /**
     * ! todo run the tests here
     * @throws AWTException 
     * @throws IOException 
     */
    public static void main(String[] args) throws AWTException, IOException {
        
        // Create mock frame
        JFrame frame = new JFrame();
        TestGamePanel panel = null;

        // !config the frame elsewhere
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ! perfect, we can load different maps!
        List<String> mapNames = new ArrayList<>(); 
        
        File folder = new File("maps/");
        File[] files = folder.listFiles();
        if(files != null) {

            // Go through all files
            for(File file : files) {

                // Not a file
                if(!file.isFile())
                    continue;

                // Only text files
                if(!file.getName().contains(".txt"))
                    continue;

                // Get only the filename
                mapNames.add(file.getName().split("\\.")[0]);
            }
        }
                
        int counter = 0;

        while(counter < mapNames.size()) {

            // Wait for bot to finish
            if(panel != null && !panel.isInitting && !panel.done)
                continue;

            // Read the file
            FileReader reader = new FileReader();
            MapData map = reader.readFile(mapNames.get(counter++));

            // Load the map
            if(panel != null) frame.remove(panel);
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
