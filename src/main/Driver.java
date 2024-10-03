package main;

import gui.GameFrame;
import reader.FileReader;
import reader.MapData;

public class Driver {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.err.println("Usage: Driver <map name> <mode>");
      System.exit(1);
    }

    String mapName = args[0];
    String mode = args[1];

    FileReader fileReader = new FileReader();
    MapData mapData = fileReader.readFile(mapName);

    GameFrame gameFrame = new GameFrame(mapData);

    if (mode.equals("fp")) {
      gameFrame.initiateFreePlay();
    } else if (mode.equals("bot")) {
      gameFrame.initiateSolution();
    }
  }
}
