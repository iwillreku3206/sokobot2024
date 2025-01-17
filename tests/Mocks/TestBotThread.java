package tests.Mocks;

import solver.SokoBot;

public class TestBotThread extends Thread {
  private SokoBot sokoBot;
  private int width;
  private int height;
  private char[][] mapData;
  private char[][] itemsData;

  private String solution = null;

  public TestBotThread(int width, int height, char[][] mapData, char[][] itemsData) {
    sokoBot = new SokoBot();
    this.width = width;
    this.height = height;
    this.mapData = mapData;
    this.itemsData = itemsData;
  }


  @Override
  public void run() {
    solution = sokoBot.solveSokobanPuzzle(width, height, mapData, itemsData);
  }

  public String getSolution() {
    return solution;
  }

  public int getChildNodesCreated() {
    return sokoBot.getCreateChildNodes();
  }

  public int getExpandedNodes() {
    return sokoBot.getExpandedNodes();
  }

  public int getInitialCost(){
    return sokoBot.getInitialCost();
  }

  public float getcHeuristicCost(){
    return sokoBot.getcHeuristicCost();
  }
  
  public float gethHeuristicCost(){
    return sokoBot.gethHeuristicCost();
  }

  public float getgHeuristicCost(){
    return sokoBot.getgHeuristicCost();
  }
  
}
