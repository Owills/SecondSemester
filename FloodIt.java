import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
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
    return scene.placeImageXY(
        new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
            this.color).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
        this.x, this.y);
  }

}

//represents the game board
class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<ArrayList<Cell>> board;

  // represents the board size
  static final int BOARD_SIZE = 8;
  // represents canvas size
  static final int CANVAS_SIZE = 800;
  // represents length of a cell
  static final int CELL_SIZE = (int) (CANVAS_SIZE / BOARD_SIZE);

  int turnsTaken;

  FloodItWorld() {
    super();
    this.board = new ArrayList<ArrayList<Cell>>();
    this.makeBoard(new Random());
    this.connectBoard();
    this.turnsTaken = 0;
  }

  // constructor for tests that does't make or connect board
  FloodItWorld(boolean tests) {
    super();
    this.board = new ArrayList<ArrayList<Cell>>();
    this.turnsTaken = 0;
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

  // makes scene
  public WorldScene makeScene() {
    return this.drawBoard(new WorldScene(CANVAS_SIZE, CANVAS_SIZE));
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
}

class ExamplesWorld {

  FloodItWorld testWorld;
  FloodItWorld testWorld1;
  FloodItWorld testWorld2;

  Random rand;
  Random rand1;
  Random rand2;
  Random rand3;

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
  boolean testDrawCell(Tester t) {
    initConditions();
    return t
        .checkExpect(cell1.drawCell(scene), scene.placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.blue).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            10, 10))
        && t.checkExpect(cell.drawCell(scene), scene.placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            0, 0))
        && t.checkExpect(cell2.drawCell(scene), scene.placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.yellow).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            20, 20));
  }

  // test drawBoard in FloodItWorld
  void testDrawBoard(Tester t) {
    initConditions();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    temp.add(cell);
    testWorld.board.add(temp);
    t.checkExpect(testWorld.drawBoard(scene),
        scene.placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            0, 0));
    temp.add(cell1);
    testWorld1.board.add(temp);
    t.checkExpect(testWorld1.drawBoard(scene), scene
        .placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            0, 0)
        .placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.blue).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            10, 10));
  }

  //test makeScene in FloodItWorld
  void testMakeScene(Tester t) {
    initConditions();
    ArrayList<Cell> temp = new ArrayList<Cell>();
    temp.add(cell);
    testWorld.board.add(temp);
    t.checkExpect(testWorld.makeScene(),
        new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE).placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            0, 0));
    temp.add(cell1);
    testWorld1.board.add(temp);
    t.checkExpect(testWorld1.makeScene(),
        new WorldScene(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE).placeImageXY(
            new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE, OutlineMode.SOLID,
                Color.black).movePinhole(-FloodItWorld.CELL_SIZE / 2, -FloodItWorld.CELL_SIZE / 2),
            0, 0)
            .placeImageXY(new RectangleImage(FloodItWorld.CELL_SIZE, FloodItWorld.CELL_SIZE,
                OutlineMode.SOLID, Color.blue).movePinhole(-FloodItWorld.CELL_SIZE / 2,
                    -FloodItWorld.CELL_SIZE / 2),
                10, 10));
  }

  // runs game
  void testBigBang(Tester t) {
    // FloodItWorld world = new FloodItWorld();
    // world.bigBang(FloodItWorld.CANVAS_SIZE, FloodItWorld.CANVAS_SIZE, 1.0 /
    // 30.0);
  }
}