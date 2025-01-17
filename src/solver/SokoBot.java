package solver;

public class SokoBot {
  private SokoSolver game;
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {

    // ! note code below this should be moved to another class, idk which yet tho
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // Create map copy
    char[][] map = new char[mapData.length][];

    // Fix the data so we have both the map and the items in on charray
    for(int y = 0; y < mapData.length; y++) {
      
      // Create row
      map[y] = new char[mapData[y].length];

      // Populate data
      for(int x = 0; x < mapData[y].length; x++) {
        map[y][x] = mapData[y][x];
        
        // There's an item there
        if(itemsData[y][x] != ' ') {

          // If the map is empty there
          if(map[y][x] == ' ') {
            map[y][x] = itemsData[y][x];
          
          // There's a goal
          } else if(map[y][x] == '.') {

            // Crate on goal
            if(itemsData[y][x] == '$')
              map[y][x] = '*';
            
            // Player on goal
            if(itemsData[y][x] == '@')
              map[y][x] = '+';
          }
        }
          
      }
    }
    // ! code above this should be moved
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    // Test
    this.game = new SokoSolver(map);
    
    String sol = this.game.solve();

    return sol;
  }

  public int getCreateChildNodes() {
      return this.game.getChildNodesCreated();
  }

  public int getExpandedNodes() {
      return this.game.getExpandedNodes();
  }

  public int getInitialCost(){
      return this.game.getInitialCost();
  }
  
  public float getcHeuristicCost(){
    return this.game.getcHeuristicCost();
  }

  public float gethHeuristicCost(){
    return this.game.gethHeuristicCost();
  }

  public float getgHeuristicCost(){
    return this.game.getgHeuristicCost();
  }
}
