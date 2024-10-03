package solver;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    // ! note code below this should be moved to another class, idk which yet tho
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // Create map copy
    char[][] map = new char[mapData.length][];

    // Fix the data so we have both the map and the items in on charray
    for(int i = 0; i < mapData.length; i++) {
      
      // Create row
      map[i] = new char[mapData[i].length];

      // Populate data
      for(int j = 0; j < mapData.length; j++) {
        map[i][j] = itemsData[i][j] != ' ' 
          ? itemsData[i][j] 
          : mapData[i][j];
      }
    }
    // ! code above this should be moved
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    // Test
    SokoSolver game = new SokoSolver(map);
    
    String sol = game.solve();

    // ! remove
    System.out.println(sol);

    return sol;
  }

}
