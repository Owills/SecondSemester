import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.worldimages.*;
import javalib.impworld.*;
import java.awt.Color;

//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // primary constructor
  Cell(int x, int y, boolean flooded) {
    this.x = x;
    this.y = y;
    this.flooded = flooded;
    this.color = this.setCellColor(new Random());
    // null Cells are outside the board
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // constructor with seed
  Cell(int x, int y, boolean flooded, Random rand) {
    this.x = x;
    this.y = y;
    this.flooded = flooded;
    this.color = this.setCellColor(rand);
    // null Cells are outside the board
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  // Produces a random color that is to be used by the cell
  Color setCellColor(Random rand) {
    ArrayList<Color> colors = new ArrayList<Color>(Arrays.asList(Color.red, Color.green, Color.blue,
        Color.black, Color.pink, Color.black, Color.cyan, Color.yellow));

    int r = rand.nextInt(colors.size());
    return colors.get(r);
  }

  public WorldScene drawCell(WorldScene scene) {
    scene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            this.color).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        this.x, this.y);
    return scene;
  }

  // mutates the given cell to be flooded if it is not null (which would indicate
  // that it is not in the board) and is the same color as the given color
  public void floodNeighbor(ArrayList<Cell> nextGenFlooded, Cell neighbor, Color color) {
    if (neighbor == null) {
      return;
    }
    else if (neighbor.color == color) {
      neighbor.flooded = true;
      nextGenFlooded.add(neighbor);
    }
  }
}

//represents the game board
class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;

  // the cells that need to be changed via waterfall
  ArrayList<ArrayList<Cell>> q;

  // represents the color of the flooded area
  Color floodedColor;

  // represents the board size
  static final int BOARD_SIZE = 8;
  // represents canvas size
  static final int CANVAS_SIZE = 800;
  // represents length of a cell
  static final int CELL_SIZE = (int) (CANVAS_SIZE / BOARD_SIZE);

  int turnsTaken;

  FloodItWorld() {
    super();
    this.initConditions();
  }

  // constructor for tests that does't make or connect board
  FloodItWorld(boolean tests) {
    super();
    this.board = new ArrayList<ArrayList<Cell>>();
    this.q = new ArrayList<ArrayList<Cell>>();
  }

  // sets the starting conditions for a new game
  void initConditions() {
    this.board = new ArrayList<ArrayList<Cell>>();
    this.makeBoard(new Random());
    this.connectBoard();
    this.turnsTaken = 0;
    this.q = new ArrayList<ArrayList<Cell>>();
    this.floodedColor = this.board.get(0).get(0).color;
    // flood adjecent square the are the same color as the initial flooded square
    this.floodWorld();
  }

  // initializes board cells
  void makeBoard(Random rand) {
    for (int i = 0; i < BOARD_SIZE; i++) {
      ArrayList<Cell> temp = new ArrayList<Cell>();
      for (int j = 0; j < BOARD_SIZE; j++) {
        temp.add(new Cell(i * CELL_SIZE, j * CELL_SIZE, false, rand));
      }
      this.board.add(temp);
    }
    this.board.get(0).get(0).flooded = true;
  }

  // connects each cell to its neighboring cells
  void connectBoard() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (i != 0) {
          this.board.get(i).get(j).left = this.board.get(i - 1).get(j);
        }
        if (i != BOARD_SIZE - 1) {
          this.board.get(i).get(j).right = this.board.get(i + 1).get(j);
        }
        if (j != 0) {
          this.board.get(i).get(j).top = this.board.get(i).get(j - 1);
        }
        if (j != BOARD_SIZE - 1) {
          this.board.get(i).get(j).bottom = this.board.get(i).get(j + 1);
        }
      }
    }
  }

  // makes scenes and triggers waterfall for filling into flooded cells
  public WorldScene makeScene() {
    this.waterfall();
    return this.checkWinOrLose(
        this.addInfoToScene(this.drawBoard(new WorldScene(CANVAS_SIZE, CANVAS_SIZE))));
  }

  // checks if player has won or lost and lets them know
  WorldScene checkWinOrLose(WorldScene scene) {
    // player has lost
    if (this.turnsTaken >= FloodItWorld.BOARD_SIZE * 2) {
      scene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black),
          FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
      scene.placeImageXY(new TextImage("No Turns Left, Press R to restart the game", Color.white),
          FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
    }

    boolean nonFloodedTile = false;
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        if (!this.board.get(i).get(j).flooded) {
          nonFloodedTile = true;
          break;
        }
      }
    }

    // player has won
    if (!nonFloodedTile) {
      scene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black),
          FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
      scene.placeImageXY(new TextImage("You have won, Press R to restart the game", Color.white),
          FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
    }
    return scene;
  }

  // adds info about number of turns taken and the maximum number of turns
  WorldScene addInfoToScene(WorldScene scene) {
    scene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black), 145, 20);
    scene.placeImageXY(new TextImage("Turns Taken " + Integer.toString(this.turnsTaken)
        + " Maximum Turns " + Integer.toString(FloodItWorld.BOARD_SIZE * 2) + ".", Color.white),
        145, 20);
    return scene;
  }

  // draws the board
  WorldScene drawBoard(WorldScene scene) {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        scene = this.board.get(i).get(j).drawCell(scene);
      }
    }
    return scene;
  }

  public void onKeyEvent(String key) {
    // if the user presses space we add a bullet at the center of the screen
    if (key.equals("r")) {
      this.initConditions();
    }
  }

  // increases the value of turnsTaken on mouse click, and for each cell, checks
  // if the coordinates of mouse
  // click were within the bounds of a cell
  // if so, prompts flooding with floodWorld function
  public void onMousePressed(Posn mouse) {
    super.onMouseClicked(mouse);
    for (ArrayList<Cell> arr : this.board) {
      for (Cell c : arr) {
        // if click is inside the given cell
        if (this.cellDetection(mouse, c) && q.size() == 0
            && this.turnsTaken < FloodItWorld.BOARD_SIZE * 2) {
          this.floodedColor = c.color;
          this.floodWorld();
          this.turnsTaken++;
        }
      }
    }
  }

  // loops through list to find flooded cells. Changes flooded cells to the color
  // of the cell that
  // was clicked in onMouseClicked method and then calls floodNeighbor on c's
  // neighbors to check if
  // they should also be flooded
  void floodWorld() {
    for (ArrayList<Cell> arr : this.board) {
      for (Cell c : arr) {
        if (c.flooded) {
          ArrayList<Cell> temp = new ArrayList<Cell>();
          temp.add(c);
          c.floodNeighbor(temp, c.left, this.floodedColor);
          c.floodNeighbor(temp, c.right, this.floodedColor);
          c.floodNeighbor(temp, c.top, this.floodedColor);
          c.floodNeighbor(temp, c.bottom, this.floodedColor);
          q.add(temp);
        }
      }
    }
  }

  // were the coordinates within the bounds of a given cell?
  boolean cellDetection(Posn click, Cell c) {
    return click.x >= c.x && click.y >= c.y && click.x <= c.x + CANVAS_SIZE / BOARD_SIZE
        && click.y <= c.y + CANVAS_SIZE / BOARD_SIZE;
  }

  // floods world it waterfall
  void waterfall() {
    if (q.size() == 0) {
      return;
    }
    for (Cell c : q.get(0)) {
      c.color = this.floodedColor;
    }
    q.remove(0);
  }
}

class ExamplesWorld {

  FloodItWorld testWorld;
  FloodItWorld testWorld1;
  FloodItWorld testWorld2;

  Random rand;
  Random rand1;
  Random rand2;
  Random rand3;
  Random rand4;

  Cell cell;
  Cell cell1;
  Cell cell2;

  WorldScene scene;

  void initConditions() {
    testWorld = new FloodItWorld(false);
    testWorld1 = new FloodItWorld(false);
    testWorld2 = new FloodItWorld(false);

    rand = new Random(0);
    rand1 = new Random(1);
    rand2 = new Random(2);
    rand3 = new Random(3);
    rand4 = new Random(4);

    cell = new Cell(0, 0, true, rand2);
    cell1 = new Cell(10, 10, false, rand2);
    cell2 = new Cell(20, 20, false, rand2);

    scene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
  }

  // test makeBoard method in FloodItWorld
  void testMakeBoard(Tester t) {
    initConditions();
    testWorld.makeBoard(rand);
    t.checkExpect(testWorld.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld.board.get(0).size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld.board.get(0).get(0).color, Color.black);
    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).color,
        Color.black);

    testWorld1.makeBoard(rand1);
    t.checkExpect(testWorld1.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld1.board.get(0).size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld1.board.get(0).get(0).color, Color.black);
    t.checkExpect(
        testWorld1.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).color,
        Color.red);
  }

  // test connectBoard method in FloodItWorld
  void testConncetBoard(Tester t) {
    initConditions();
    testWorld.makeBoard(rand);
    t.checkExpect(testWorld.board.get(0).get(0).left, null);
    t.checkExpect(testWorld.board.get(0).get(0).right, null);
    t.checkExpect(testWorld.board.get(0).get(0).bottom, null);
    t.checkExpect(testWorld.board.get(0).get(0).top, null);

    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).left,
        null);
    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).right,
        null);
    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).bottom,
        null);
    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).top,
        null);

    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).left, null);
    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).right, null);
    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).bottom, null);
    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).top, null);

    testWorld.connectBoard();
    t.checkExpect(testWorld.board.get(0).get(0).left, null);
    t.checkExpect(testWorld.board.get(0).get(0).right.color, Color.pink);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.color, Color.cyan);
    t.checkExpect(testWorld.board.get(0).get(0).top, null);

    t.checkExpect(testWorld.board.get(FloodItWorld.BOARD_SIZE - 1)
        .get(FloodItWorld.BOARD_SIZE - 1).left.color, Color.yellow);
    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).right,
        null);
    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).bottom,
        null);
    t.checkExpect(
        testWorld.board.get(FloodItWorld.BOARD_SIZE - 1).get(FloodItWorld.BOARD_SIZE - 1).top.color,
        Color.black);

    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).left.color, Color.pink);
    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).right.color, Color.black);
    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).bottom.color, Color.red);
    t.checkExpect(testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
        .get((int) (FloodItWorld.BOARD_SIZE / 2)).top.color, Color.blue);

    t.checkExpect(
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)).left.right,
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)));
    t.checkExpect(
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)).right.left,
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)));
    t.checkExpect(
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)).top.bottom,
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)));
    t.checkExpect(
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)).bottom.top,
        testWorld.board.get((int) (FloodItWorld.BOARD_SIZE / 2))
            .get((int) (FloodItWorld.BOARD_SIZE / 2)));
  }

  // test setCellColor in Cell
  boolean testSetCellColor(Tester t) {
    initConditions();
    return t.checkExpect(cell.setCellColor(rand2), Color.red)
        && t.checkExpect(cell1.setCellColor(rand2), Color.black)
        && t.checkExpect(cell2.setCellColor(rand2), Color.cyan);
  }

  // test drawCell in Cell
  void testDrawCell(Tester t) {
    initConditions();
    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));
    cell1.drawCell(scene);
    WorldScene testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            Color.blue).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        10, 10);
    t.checkExpect(scene, testScene);

    initConditions();
    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));
    cell.drawCell(scene);
    testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        0, 0);
    t.checkExpect(scene, testScene);

    initConditions();
    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));
    cell2.drawCell(scene);
    testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            Color.yellow).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        20, 20);
    t.checkExpect(scene, testScene);
  }

  // test drawBoard in FloodItWorld
  void testDrawBoard(Tester t) {
    initConditions();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    temp.add(cell);
    testWorld.board.add(temp);
    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));
    testWorld.drawBoard(scene);

    WorldScene testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        0, 0);
    t.checkExpect(scene, testScene);

    temp.add(cell1);
    testWorld1.board.add(temp);
    testWorld1.drawBoard(scene);
    testScene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            Color.blue).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        10, 10);
    t.checkExpect(scene, testScene);
  }

  // test makeScene in FloodItWorld
  void testMakeScene(Tester t) {
    initConditions();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    cell.flooded = false;
    temp.add(cell);
    testWorld.board.add(temp);
    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));
    WorldScene testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        0, 0);
    testScene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black), 145, 20);
    testScene.placeImageXY(new TextImage("Turns Taken " + Integer.toString(0) + " Maximum Turns "
        + Integer.toString(FloodItWorld.BOARD_SIZE * 2) + ".", Color.white), 145, 20);
    t.checkExpect(testWorld.makeScene(), testScene);

    temp.add(cell1);
    testWorld1.board.add(temp);
    testWorld1.makeScene();
    testScene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            Color.blue).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        10, 10);
    testScene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black), 145, 20);
    testScene.placeImageXY(new TextImage("Turns Taken " + Integer.toString(0) + " Maximum Turns "
        + Integer.toString(FloodItWorld.BOARD_SIZE * 2) + ".", Color.white), 145, 20);
    t.checkExpect(testWorld1.makeScene(), testScene);
  }

  // test checkWinOrLose in FloodItWorld
  void testCheckWinOrLose(Tester t) {
    initConditions();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    cell.flooded = false;
    temp.add(cell);
    testWorld.board.add(temp);

    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));
    // hasn't won or lose
    testWorld.checkWinOrLose(scene);
    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));

    // player has flooded all the cells and won
    initConditions();
    cell.flooded = false;
    testWorld.checkWinOrLose(scene);
    WorldScene testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black),
        FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
    testScene.placeImageXY(new TextImage("You have won, Press R to restart the game", Color.white),
        FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
    t.checkExpect(scene, testScene);

    initConditions();
    temp = new ArrayList<Cell>();
    cell.flooded = false;
    temp.add(cell);
    testWorld.board.add(temp);
    // player has taken the maximum number of turns and has lost
    testWorld.turnsTaken = FloodItWorld.BOARD_SIZE * 2;
    testWorld.checkWinOrLose(scene);

    testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black),
        FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
    testScene.placeImageXY(new TextImage("No Turns Left, Press R to restart the game", Color.white),
        FloodItWorld.CANVAS_SIZE / 2, FloodItWorld.CANVAS_SIZE / 2);
    t.checkExpect(scene, testScene);

  }

  // test addInfoToScene in FloodItWorld
  void testAddInfoToScene(Tester t) {
    initConditions();
    t.checkExpect(scene, new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE));

    testWorld.addInfoToScene(scene);
    WorldScene testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black), 145, 20);
    testScene.placeImageXY(new TextImage("Turns Taken " + Integer.toString(0) + " Maximum Turns "
        + Integer.toString(FloodItWorld.BOARD_SIZE * 2) + ".", Color.white), 145, 20);

    t.checkExpect(scene, testScene);

    testWorld.turnsTaken = 3;

    testWorld.addInfoToScene(scene);
    testScene = new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE);
    testScene.placeImageXY(new RectangleImage(250, 20, OutlineMode.SOLID, Color.black), 145, 20);
    testScene.placeImageXY(new TextImage("Turns Taken " + Integer.toString(3) + " Maximum Turns "
        + Integer.toString(FloodItWorld.BOARD_SIZE * 2) + ".", Color.white), 145, 20);
    t.checkExpect(scene, testScene);

  }

  // test floodNeighbor in Cell
  void testFloodNeighbor(Tester t) {
    initConditions();
    ArrayList<Cell> nextGenFlooded = new ArrayList<Cell>();
    // passed in null neighbor cell so nothing changes
    cell.floodNeighbor(nextGenFlooded, null, Color.black);
    t.checkExpect(nextGenFlooded.size(), 0);

    // passed itself as its own neighbor which should change itself to flooded
    // add add itself to the arrayList
    cell.flooded = false;
    t.checkExpect(cell.flooded, false);
    cell.floodNeighbor(nextGenFlooded, cell, cell.color);
    t.checkExpect(nextGenFlooded.size(), 1);
    t.checkExpect(cell.flooded, true);
    t.checkExpect(nextGenFlooded.get(0), cell);

    initConditions();
    nextGenFlooded = new ArrayList<Cell>();
    t.checkExpect(cell1.flooded, false);
    cell.floodNeighbor(nextGenFlooded, cell1, cell1.color);
    t.checkExpect(nextGenFlooded.size(), 1);
    t.checkExpect(cell.flooded, true);
    t.checkExpect(cell1.flooded, true);
    t.checkExpect(nextGenFlooded.get(0), cell1);

    initConditions();
    nextGenFlooded = new ArrayList<Cell>();
    t.checkExpect(cell1.flooded, false);
    t.checkExpect(cell2.flooded, false);
    cell1.floodNeighbor(nextGenFlooded, cell2, cell.color);
    t.checkExpect(nextGenFlooded.size(), 0);
    t.checkExpect(cell1.flooded, false);
    t.checkExpect(cell2.flooded, false);

    initConditions();
    nextGenFlooded = new ArrayList<Cell>();
    // set test cells to be connect
    cell.left = cell1;
    cell1.right = cell;
    cell.right = cell2;
    cell2.left = cell;

    t.checkExpect(cell.flooded, true);
    t.checkExpect(cell1.flooded, false);
    t.checkExpect(cell2.flooded, false);
    cell.floodNeighbor(nextGenFlooded, cell1, cell.color);
    t.checkExpect(nextGenFlooded.size(), 0);
    t.checkExpect(cell1.flooded, false);
    t.checkExpect(cell2.flooded, false);

    cell.floodNeighbor(nextGenFlooded, cell2, cell.color);
    t.checkExpect(nextGenFlooded.size(), 0);
    t.checkExpect(cell1.flooded, false);
    t.checkExpect(cell2.flooded, false);

    cell.floodNeighbor(nextGenFlooded, cell1, cell1.color);
    t.checkExpect(nextGenFlooded.size(), 1);
    t.checkExpect(cell1.flooded, true);
    t.checkExpect(cell2.flooded, false);

    cell.floodNeighbor(nextGenFlooded, cell2, cell2.color);
    t.checkExpect(nextGenFlooded.size(), 2);
    t.checkExpect(cell1.flooded, true);
    t.checkExpect(cell2.flooded, true);
  }

  // test initCondtions in FloodItWorld
  void testInitCondtion(Tester t) {
    initConditions();
    testWorld.board = null;
    t.checkExpect(testWorld.floodedColor, null);
    t.checkExpect(testWorld.board, null);
    t.checkExpect(testWorld.turnsTaken, 0);

    testWorld.initConditions();
    t.checkExpect(testWorld.floodedColor, testWorld.board.get(0).get(0).color);
    t.checkExpect(testWorld.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld.turnsTaken, 0);

    testWorld1.board = null;
    t.checkExpect(testWorld1.floodedColor, null);
    t.checkExpect(testWorld1.board, null);
    t.checkExpect(testWorld1.turnsTaken, 0);
    testWorld1.initConditions();
    t.checkExpect(testWorld1.floodedColor, testWorld1.board.get(0).get(0).color);
    t.checkExpect(testWorld1.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld1.turnsTaken, 0);

    testWorld2.board = null;
    t.checkExpect(testWorld2.floodedColor, null);
    t.checkExpect(testWorld2.board, null);
    t.checkExpect(testWorld2.turnsTaken, 0);
    testWorld2.initConditions();
    t.checkExpect(testWorld2.floodedColor, testWorld2.board.get(0).get(0).color);
    t.checkExpect(testWorld2.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld2.turnsTaken, 0);
  }

  // test onKeyEvent in FloodItWorld
  void testOnKeyEvent(Tester t) {
    initConditions();
    testWorld.board = null;
    t.checkExpect(testWorld.floodedColor, null);
    t.checkExpect(testWorld.board, null);
    t.checkExpect(testWorld.turnsTaken, 0);

    testWorld.onKeyEvent("");
    t.checkExpect(testWorld.floodedColor, null);
    t.checkExpect(testWorld.board, null);
    t.checkExpect(testWorld.turnsTaken, 0);

    testWorld.onKeyEvent("r");
    t.checkExpect(testWorld.floodedColor, testWorld.board.get(0).get(0).color);
    t.checkExpect(testWorld.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld.turnsTaken, 0);

    testWorld1.board = null;
    t.checkExpect(testWorld1.floodedColor, null);
    t.checkExpect(testWorld1.board, null);
    t.checkExpect(testWorld1.turnsTaken, 0);
    testWorld1.onKeyEvent("r");
    t.checkExpect(testWorld1.floodedColor, testWorld1.board.get(0).get(0).color);
    t.checkExpect(testWorld1.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld1.turnsTaken, 0);

    testWorld2.board = null;
    t.checkExpect(testWorld2.floodedColor, null);
    t.checkExpect(testWorld2.board, null);
    t.checkExpect(testWorld2.turnsTaken, 0);
    testWorld2.onKeyEvent("r");
    t.checkExpect(testWorld2.floodedColor, testWorld2.board.get(0).get(0).color);
    t.checkExpect(testWorld2.board.size(), FloodItWorld.BOARD_SIZE);
    t.checkExpect(testWorld2.turnsTaken, 0);
  }

  // test onMousePressed in FloodItWorld
  void testOnMousePressed(Tester t) {
    initConditions();
    testWorld.initConditions();
    // artificial color that is not a current color that the board can generate
    testWorld.floodedColor = Color.darkGray;
    Color c = testWorld.floodedColor;
    // mouse position outside game so nothing should happen
    testWorld.onMousePressed(new Posn(-10, -10));

    t.checkExpect(testWorld.floodedColor, c);

    while (testWorld.q.size() != 0) {
      testWorld.q.remove(0);
    }

    t.checkExpect(testWorld.q.size(), 0);
    t.checkExpect(testWorld.turnsTaken, 0);
    // click on the first square
    testWorld.onMousePressed(new Posn(1, 1));
    t.checkExpect(testWorld.floodedColor, testWorld.board.get(0).get(0).color);
    t.checkExpect(testWorld.q.size() > 0, true);
    t.checkExpect(testWorld.turnsTaken, 1);
    t.checkExpect(testWorld.board.get(0).get(0).flooded, true);

    initConditions();

    testWorld1.initConditions();
    // artificial color that is not a current color that the board can generate

    while (testWorld1.q.size() != 0) {
      testWorld1.q.remove(0);
    }

    t.checkExpect(testWorld1.q.size(), 0);
    t.checkExpect(testWorld1.turnsTaken, 0);
    // click on the first square
    testWorld1.onMousePressed(new Posn(1, 1));
    t.checkExpect(testWorld1.floodedColor, testWorld1.board.get(0).get(0).color);
    t.checkExpect(testWorld1.q.size() > 0, true);
    t.checkExpect(testWorld1.turnsTaken, 1);
    t.checkExpect(testWorld1.board.get(0).get(0).flooded, true);
  }

  // test floodWorld in FloodItWorld
  void testFloodWorld(Tester t) {
    initConditions();
    testWorld.makeBoard(rand4);
    testWorld.connectBoard();

    t.checkExpect(testWorld.floodedColor, null);
    t.checkExpect(testWorld.q.size(), 0);
    t.checkExpect(testWorld.board.get(0).get(0).color, Color.black);
    t.checkExpect(testWorld.board.get(0).get(0).flooded, true);
    t.checkExpect(testWorld.board.get(0).get(0).left, null);
    t.checkExpect(testWorld.board.get(0).get(0).top, null);
    t.checkExpect(testWorld.board.get(0).get(0).right.color, Color.red);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.color, Color.yellow);
    t.checkExpect(testWorld.board.get(0).get(0).right.flooded, false);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.flooded, false);

    testWorld.floodedColor = Color.red;
    testWorld.floodWorld();

    t.checkExpect(testWorld.floodedColor, Color.red);
    t.checkExpect(testWorld.q.size(), 2);
    t.checkExpect(testWorld.board.get(0).get(0).color, Color.black);
    t.checkExpect(testWorld.board.get(0).get(0).flooded, true);
    t.checkExpect(testWorld.board.get(0).get(0).left, null);
    t.checkExpect(testWorld.board.get(0).get(0).top, null);
    t.checkExpect(testWorld.board.get(0).get(0).right.color, Color.red);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.color, Color.yellow);
    t.checkExpect(testWorld.board.get(0).get(0).right.flooded, true);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.flooded, false);

    testWorld.waterfall();
    t.checkExpect(testWorld.floodedColor, Color.red);
    t.checkExpect(testWorld.q.size(), 1);
    t.checkExpect(testWorld.board.get(0).get(0).color, Color.red);
    t.checkExpect(testWorld.board.get(0).get(0).flooded, true);
    t.checkExpect(testWorld.board.get(0).get(0).left, null);
    t.checkExpect(testWorld.board.get(0).get(0).top, null);
    t.checkExpect(testWorld.board.get(0).get(0).right.color, Color.red);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.color, Color.yellow);
    t.checkExpect(testWorld.board.get(0).get(0).right.flooded, true);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.flooded, false);

    testWorld.floodedColor = Color.yellow;
    testWorld.floodWorld();

    t.checkExpect(testWorld.floodedColor, Color.yellow);
    t.checkExpect(testWorld.q.size(), 5);
    t.checkExpect(testWorld.board.get(0).get(0).color, Color.red);
    t.checkExpect(testWorld.board.get(0).get(0).flooded, true);
    t.checkExpect(testWorld.board.get(0).get(0).left, null);
    t.checkExpect(testWorld.board.get(0).get(0).top, null);
    t.checkExpect(testWorld.board.get(0).get(0).right.color, Color.red);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.color, Color.yellow);
    t.checkExpect(testWorld.board.get(0).get(0).right.flooded, true);
    t.checkExpect(testWorld.board.get(0).get(0).bottom.flooded, true);
  }

  // test cellDetection in FloodItWorld
  boolean testCellDetection(Tester t) {
    initConditions();
    return t.checkExpect(testWorld.cellDetection(new Posn(-1, -1), cell), false)
        && t.checkExpect(testWorld.cellDetection(new Posn(0, 0), cell), true)
        && t.checkExpect(testWorld.cellDetection(
            new Posn(FloodItWorld.CELL_SIZE + 1, FloodItWorld.CELL_SIZE + 1), cell), false)
        && t.checkExpect(testWorld.cellDetection(new Posn(-1, -1), cell1), false)
        && t.checkExpect(testWorld.cellDetection(new Posn(1, 1), cell1), false)
        && t.checkExpect(testWorld.cellDetection(new Posn(10, 10), cell1), true)
        && t.checkExpect(testWorld.cellDetection(new Posn(-1, -1), cell2), false)
        && t.checkExpect(testWorld.cellDetection(new Posn(1, 1), cell2), false)
        && t.checkExpect(testWorld.cellDetection(new Posn(20, 20), cell2), true);
  }

  // test waterFall in FloodItWorld
  void testWaterFall(Tester t) {
    initConditions();

    // reset the world until the initial conditions need to flood
    while (testWorld.q.size() != 1) {
      testWorld.onKeyEvent("r");
    }

    int qSize = testWorld.q.size();
    Cell temp = testWorld.q.get(0).get(0);
    // set to impossible color
    temp.color = Color.darkGray;
    t.checkExpect(temp.color == testWorld.floodedColor, false);

    testWorld.waterfall();
    t.checkExpect(testWorld.q.size(), 0);
    t.checkExpect(temp.color == testWorld.floodedColor, true);

    // does nothing
    testWorld.waterfall();
    t.checkExpect(testWorld.q.size(), 0);

    while (testWorld1.q.size() == 0) {
      testWorld1.onKeyEvent("r");
    }
    qSize = testWorld1.q.size();
    temp = testWorld1.q.get(0).get(0);
    // set to impossible color
    temp.color = Color.darkGray;
    t.checkExpect(temp.color == testWorld1.floodedColor, false);

    testWorld1.waterfall();
    t.checkExpect(testWorld1.q.size(), qSize - 1);
    t.checkExpect(temp.color == testWorld1.floodedColor, true);
  }

  // runs game
  void testBigBang(Tester t) {
    FloodItWorld world = new FloodItWorld();
    world.bigBang(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE, 1.0 / 30.0);
  }

}