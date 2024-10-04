/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-05 00:30:11
 * @ Modified time: 2024-10-05 04:39:18
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
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws AWTException, IOException, InterruptedException {
        
        // ! perfect, we can load different maps!
        List<String> mapNames = new ArrayList<>(); 
        
        File folder = new File("maps/tests");
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
                mapNames.add(
                    file.getParentFile().getName() + "\\" +
                    file.getName().split("\\.")[0]);
            }
        }

        int counter = 0;
        JFrame frame = null;
        TestGamePanel panel = null;

        while(counter < mapNames.size()) {

            // Slow down
            Thread.sleep(500);

            // Wait for bot to finish
            if(panel != null && !panel.isInitting && !panel.done)
                continue;

            // Create mock frame
            if(frame != null) frame.dispose();
            frame = new JFrame();

            // !config the frame elsewhere
            frame.setSize(1000, 800);
            frame.setLocationRelativeTo(null);
            frame.setTitle("Tester" + counter);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Read the file
            FileReader reader = new FileReader();
            String mapName = mapNames.get(counter);
            MapData map = reader.readFile(mapName);
            
            // Load the map
            if(panel != null) {
                System.out.println("for: " + mapName);
                System.out.println("time: " + panel.getTime());
                System.out.println("moves: " + panel.getMoves());
                System.out.println();
                panel.close();
                frame.remove(panel);
            }

            panel = new TestGamePanel();

            // Add the panel to the frame
            frame.add(panel);
            frame.setVisible(true);
            frame.repaint();

            // Load the map
            panel.loadMap(map);
            panel.initiateSolution();
            panel.keyPressed(new KeyEvent(panel, 0, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' '));

            counter++;
        }
    }
}
