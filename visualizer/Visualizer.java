/**
 * @ Author: Group 23
 * @ Create Time: 2024-10-07 12:51:19
 * @ Modified time: 2024-10-07 15:23:33
 * @ Description:
 * 
 * Visualizes the states visited by the bot.
 */

package visualizer;

import javax.swing.JFrame;

import reader.FileReader;
import reader.MapData;
import solver.SokoSolver;

public class Visualizer {

    private static JFrame frame;
    private static StateVisual visual;
    private static SokoSolver solver;
    
    public static void main(String[] args) throws InterruptedException {
        
        // Init the frame
        frame = new JFrame();
        visual = new StateVisual();
        
        // Read the file first
        FileReader reader = new FileReader();
        MapData map = reader.readFile("original2");
        
        // Init the solver
        solver = new SokoSolver(map.tiles);
        
        // Configure the components
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(visual);
        frame.setVisible(true);

        while(!solver.isDone()) {
            Thread.sleep(69);
            solver.iterate();
            visual.showState(solver.getLastVisitedState(), solver.getMap());
            visual.repaint();
        }
    }
}
