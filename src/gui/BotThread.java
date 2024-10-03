package gui;

import solver.SokoBot;

public class BotThread extends Thread {
  private SokoBot sokoBot;
  private int width;
  private int height;
  private char[][] mapData;
  private char[][] itemsData;

  private String solution = null;

  public BotThread(int width, int height, char[][] mapData, char[][] itemsData) {
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
}
