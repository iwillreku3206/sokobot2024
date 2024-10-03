package gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import reader.MapData;
import java.awt.*;  

public class GameFrame extends JFrame {
  private GamePanel mainPanel;
  private MapData mapData;

  public GameFrame(MapData mapData) {
    this.mapData = mapData;

    ImageIcon imgIcon = new ImageIcon("../graphics/robot.png");  
    this.setIconImage(imgIcon.getImage());  

    

    this.setSize(800, 600);
    this.setLayout(new GridLayout(0, 1));
    this.setLocationRelativeTo(null);
    this.setTitle("Sokoban");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    this.mainPanel = new GamePanel();
    this.add(mainPanel);

    this.mainPanel.loadMap(mapData);
    this.setVisible(true);

  }

  public void initiateFreePlay() {
    this.mainPanel.initiateFreePlay();
  }

  public void initiateSolution() {
    this.mainPanel.initiateSolution();
  }

  public void restart ()
  {
    this.mainPanel.loadMap(mapData);

    this.mainPanel.initiateFreePlay ();
    
  }
}