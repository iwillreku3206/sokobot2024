package gui;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.Font;
import javax.swing.Timer;
import java.io.File;

import javax.imageio.ImageIO;
import reader.MapData;

public class GamePanel extends JPanel implements KeyListener, ActionListener {

  private boolean mapLoaded = false;
  private int playerRow = -1;
  private int playerColumn = -1;
  private int rows;
  private int columns;
  private char[][] map;
  private char[][] items;

  private BufferedImage BRICK_SPRITE;
  private BufferedImage GOAL_SPRITE;
  private BufferedImage CRATE_SPRITE;
  private BufferedImage CRATE_ON_GOAL_SPRITE;
  private BufferedImage PLAYER_SPRITE;

  private final int UPPER_LEFT_X = 10;
  private final int UPPER_LEFT_Y = 10;
  private final int TILE_SIZE = 32;

  private boolean freePlay = false;
  private boolean waitingForSpace = false;
  private String solutionString = "";
  private int solutionCtr = -1;

  private Timer animationTimer;

  private Font statusFont;
  private Font statusValueFont;
  private String statusString = "";

  private final String STATUS_WAITING_FOR_SPACE = "Push SPACE to start Bot...";
  private final String STATUS_WAITING_FOR_SOLUTION = "Waiting for solution...";
  private final String STATUS_SOLUTION_TIMEOUT = "TIME'S UP! Bot took too long thinking...";
  private final String STATUS_PLAYING_SOLUTION = "Playing solution...";
  private final String STATUS_FINISHED_PLAYING_SOLUTION = "SOLUTION FINISHED!";
  private final String STATUS_FREE_PLAY = "FREE PLAY MODE!";

  private String solutionTimeString = "";

  private int progress = 0;
  private int moves = 0;
  private int boxCount = 0;
  private int goalCount = 0;
  private int playerCount = 0;

  private BotThread solutionThread;
  private Timer solutionTimer;
  private Timer checkForSolutionTimer;
  private long solutionStartTime;
  private long solutionEndTime;

  private final int SOLUTION_TIME_LIMIT = 15000;

  public GamePanel() {
    this.setBackground(Color.BLACK);
    loadImages();
    this.addKeyListener(this);
    this.setFocusable(true);
    this.statusFont = new Font("SansSerif", Font.BOLD, 16);
    this.statusValueFont = new Font("SansSerif", Font.PLAIN, 16);
  }

  private void loadImages() {
    try {
      BRICK_SPRITE = ImageIO.read(new File("src/graphics/brick.png"));
      GOAL_SPRITE = ImageIO.read(new File("src/graphics/goal.png"));
      CRATE_SPRITE = ImageIO.read(new File("src/graphics/crate.png"));
      CRATE_ON_GOAL_SPRITE = ImageIO.read(new File("src/graphics/crategoal.png"));
      PLAYER_SPRITE = ImageIO.read(new File("src/graphics/robot.png"));
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }

  public void loadMap(MapData mapData) {
    progress = 0;
    moves = 0;

    map = new char[mapData.rows][mapData.columns];
    items = new char[mapData.rows][mapData.columns];
    playerCount = 0;
    boxCount = 0;
    goalCount = 0;

    for (int i = 0; i < mapData.rows; i++) {
      for (int j = 0; j < mapData.columns; j++) {
        switch (mapData.tiles[i][j]) {
          case '#':
            map[i][j] = '#';
            items[i][j] = ' ';
            break;
          case '@':
            map[i][j] = ' ';
            items[i][j] = '@';
            playerCount++;
            playerRow = i;
            playerColumn = j;
            break;
          case '$':
            map[i][j] = ' ';
            items[i][j] = '$';
            boxCount++;
            break;
          case '.':
            map[i][j] = '.';
            items[i][j] = ' ';
            goalCount++;
            break;
          case '+':
            map[i][j] = '.';
            items[i][j] = '@';
            playerRow = i;
            playerColumn = j;
            playerCount++;
            goalCount++;
            break;
          case '*':
            map[i][j] = '.';
            items[i][j] = '$';
            boxCount++;
            goalCount++;
            progress++;
            break;
          case ' ':
            map[i][j] = ' ';
            items[i][j] = ' ';
            break;
        }
      }
    }

    rows = mapData.rows;
    columns = mapData.columns;

    if (playerCount == 1 && boxCount == goalCount && boxCount > 0) {
      freePlay = false;
      mapLoaded = true;
      this.repaint();
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.clearRect(0, 0, this.getWidth(), this.getHeight());
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, this.getWidth(), this.getHeight());

    if (mapLoaded) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
          BufferedImage target = null;
          if (map[i][j] == '#') {
            target = BRICK_SPRITE;
          } else if (map[i][j] == '.') {
            target = GOAL_SPRITE;
          }
          if (target != null) {
            g.drawImage(target, UPPER_LEFT_X + j * TILE_SIZE,
                UPPER_LEFT_Y + i * TILE_SIZE, TILE_SIZE, TILE_SIZE,
                this);
          }
          target = null;
          if (items[i][j] == '$' && map[i][j] == '.') {
            target = CRATE_ON_GOAL_SPRITE;
          } else if (items[i][j] == '$' && map[i][j] != '.') {
            target = CRATE_SPRITE;
          } else if (items[i][j] == '@') {
            target = PLAYER_SPRITE;
          }
          if (target != null) {
            g.drawImage(target, UPPER_LEFT_X + j * TILE_SIZE,
                UPPER_LEFT_Y + i * TILE_SIZE, TILE_SIZE, TILE_SIZE,
                this);
          }
        }
      }

      g.setColor(new Color(150, 214, 124));
      g.fillRect(0, this.getHeight() - 32, this.getWidth(), 32);
      g.setColor(Color.RED);
      g.setFont(this.statusFont);
      g.drawString(this.statusString, this.getWidth() - 375, this.getHeight() - 12);
      g.setColor(Color.BLACK);
      g.setFont(this.statusFont);
      g.drawString("MOVES: ", 8, this.getHeight() - 12);
      g.drawString("PROGRESS: ", 176, this.getHeight() - 12);
      g.setFont(this.statusValueFont);
      g.drawString("" + moves, 80, this.getHeight() - 12);
      g.drawString(progress + " / " + boxCount, 286, this.getHeight() - 12);
      g.drawString(this.solutionTimeString, this.getWidth() - 60, this.getHeight() - 12);
    }
  }

  public void initiateFreePlay() {
    this.statusString = STATUS_FREE_PLAY;
    waitingForSpace = false;
    freePlay = true;
  }

  public void initiateSolution() {
    this.statusString = STATUS_WAITING_FOR_SPACE;
    waitingForSpace = true;
    freePlay = false;
  }

  // 0 - Up, 1 - down, 2 - left, 3 - right
  private void executeMove(int direction) {
    int ptRow = -1;
    int ptCol = -1;
    int btRow = -1;
    int btCol = -1;
    if (direction == 0) {
      ptRow = playerRow - 1;
      ptCol = playerColumn;
      btRow = playerRow - 2;
      btCol = playerColumn;
    } else if (direction == 1) {
      ptRow = playerRow + 1;
      ptCol = playerColumn;
      btRow = playerRow + 2;
      btCol = playerColumn;
    } else if (direction == 2) {
      ptRow = playerRow;
      ptCol = playerColumn - 1;
      btRow = playerRow;
      btCol = playerColumn - 2;
    } else if (direction == 3) {
      ptRow = playerRow;
      ptCol = playerColumn + 1;
      btRow = playerRow;
      btCol = playerColumn + 2;
    }
    handleMovement(ptRow, ptCol, btRow, btCol);
  }

  private void handleMovement(int ptRow, int ptCol, int btRow, int btCol) {
    if (ptRow < 0 || ptRow >= rows || ptCol < 0 || ptCol >= columns) {
      return;
    }
    if (map[ptRow][ptCol] == '#') {
      return;
    }
    if (items[ptRow][ptCol] != '$') {
      items[playerRow][playerColumn] = ' ';
      items[ptRow][ptCol] = '@';
      playerRow = ptRow;
      playerColumn = ptCol;
    } else if (items[ptRow][ptCol] == '$') {
      if (btRow < 0 || btRow >= rows || btCol < 0 || btCol >= columns) {
        return;
      }
      if (map[btRow][btCol] == '#' || items[btRow][btCol] == '$') {
        return;
      }
      if (map[btRow][btCol] == '.') {
        progress++;
      }
      if (map[ptRow][ptCol] == '.') {
        progress--;
      }
      items[btRow][btCol] = '$';
      items[playerRow][playerColumn] = ' ';
      items[ptRow][ptCol] = '@';
      playerRow = ptRow;
      playerColumn = ptCol;
    }

    moves++;

    this.repaint();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (freePlay) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
          executeMove(0);
          break;
        case KeyEvent.VK_DOWN:
          executeMove(1);
          break;
        case KeyEvent.VK_LEFT:
          executeMove(2);
          break;
        case KeyEvent.VK_RIGHT:
          executeMove(3);
          break;
        case KeyEvent.VK_ESCAPE:
          GameFrame topFrame = (GameFrame) SwingUtilities.getWindowAncestor(this);

          topFrame.restart();
          break;
      }
    } else if (waitingForSpace) {
      if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        waitingForSpace = false;
        this.statusString = STATUS_WAITING_FOR_SOLUTION;

        char[][] mapDataCopy = new char[rows][columns];
        char[][] itemsDataCopy = new char[rows][columns];

        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < columns; j++) {
            mapDataCopy[i][j] = map[i][j];
            itemsDataCopy[i][j] = items[i][j];
          }
        }

        solutionThread = new BotThread(columns, rows, mapDataCopy, itemsDataCopy);
        solutionThread.start();
        solutionStartTime = System.nanoTime();
        solutionTimer = new Timer(SOLUTION_TIME_LIMIT, this);
        solutionTimer.start();
        checkForSolutionTimer = new Timer(30, this);
        checkForSolutionTimer.start();

        this.repaint();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  public void playSolution(String solutionString) {
    playSolution(solutionString, 100);
  }

  public void playSolution(String solutionString, int delay) {
    freePlay = false;
    this.statusString = STATUS_PLAYING_SOLUTION;
    this.solutionString = solutionString;
    this.solutionCtr = 0;
    this.animationTimer = new Timer(delay, this);
    this.animationTimer.start();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == animationTimer) {
      if (this.solutionCtr >= this.solutionString.length()) {
        this.animationTimer.stop();
        this.statusString = STATUS_FINISHED_PLAYING_SOLUTION;
        this.repaint();
        return;
      }
      int nextMove = this.solutionString.charAt(this.solutionCtr++);
      switch (nextMove) {
        case 'u':
          executeMove(0);
          break;
        case 'd':
          executeMove(1);
          break;
        case 'l':
          executeMove(2);
          break;
        case 'r':
          executeMove(3);
          break;
      }
    } else if (e.getSource() == checkForSolutionTimer) {
      if (!solutionThread.isAlive()) {
        // Solution was found
        solutionTimer.stop();
        checkForSolutionTimer.stop();
        String solution = solutionThread.getSolution();
        this.playSolution(solution);
      }
      long elapsedSolutionTime = System.nanoTime() - solutionStartTime;
      this.solutionTimeString = String.format("%.2f", elapsedSolutionTime / 1000000000.0) + "s";
      this.repaint();
    } else if (e.getSource() == solutionTimer) {
      // Solution was not found
      solutionTimer.stop();
      checkForSolutionTimer.stop();
      long elapsedSolutionTime = System.nanoTime() - solutionStartTime;
      this.solutionTimeString = String.format("%.2f", elapsedSolutionTime / 1000000000.0);
      this.statusString = STATUS_SOLUTION_TIMEOUT;
      this.repaint();
    }
  }
}
