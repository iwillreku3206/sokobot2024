package solver;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    // Print the data to the screen for debug
    for(int i = 0; i < mapData.length; i++) {
      for(int j = 0; j < mapData.length; j++) {
        char block = mapData[i][j];
        char item = itemsData[i][j];

        if(item != ' ')
          System.out.print(item);
        else
          System.out.print(block);
      }
      System.out.print('\n');
    }

    System.out.println(((byte) 0b11111111) & ((byte) -120));
    
    return "lr";
  }

}
