import java.util.*;
import tester.*;
import javalib.worldimages.*;
import javalib.impworld.*;
import java.awt.Color;

// represents and edge between two ndoes
class Edge {
  Node n1; // left or top node
  Node n2; // right of bottom node
  int weight;

  Edge(Node n1, Node n2, int weight) {
    this.n1 = n1;
    this.n2 = n2;
    this.weight = weight;
  }

  // draws the edges
  // this works actually by drawing an opening in each node
  // and drawing a white line where an opening should between on top of a square
  // node
  WorldScene draw(WorldScene scene) {
    int n1x = (this.n1.num % MazeImage.numNodes_x) * MazeImage.CELL_SIZE;
    int n1y = (int) (this.n1.num / MazeImage.numNodes_x) * MazeImage.CELL_SIZE;
    int n2x = (this.n2.num % MazeImage.numNodes_x) * MazeImage.CELL_SIZE;
    int n2y = (int) (this.n2.num / MazeImage.numNodes_x) * MazeImage.CELL_SIZE;

    // left to right
    if (n2y == n1y) {
      scene.placeImageXY(
          new RectangleImage(1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID, Color.white),
          (n1x + n2x) / 2 + MazeImage.CELL_SIZE / 2 + 1, n2y + MazeImage.CELL_SIZE / 2 + 1);
    }
    // up and down
    if (n2x == n1x) {
      scene.placeImageXY(
          new RectangleImage(MazeImage.CELL_SIZE - 1, 1, OutlineMode.SOLID, Color.white),
          n2x + MazeImage.CELL_SIZE / 2 + 1, (n1y + n2y) / 2 + MazeImage.CELL_SIZE / 2 + 1);
    }
    return scene;
  }
}

//represents a cell in the the maze
class Node {
  int num;

  Node(int num) {
    this.num = num;
  }

  // draws each node as a square box
  WorldScene draw(WorldScene scene) {
    scene.placeImageXY(
        new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.OUTLINE,
            Color.black).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
        (this.num % MazeImage.numNodes_x) * MazeImage.CELL_SIZE,
        (int) (this.num / MazeImage.numNodes_x) * MazeImage.CELL_SIZE);
    return scene;
  }
}

//comparator used to compare the weights of Edges
class WeightComparator implements Comparator<Edge> {
  @Override
  public int compare(Edge e1, Edge e2) {
    if (e1.weight < e2.weight) {
      return -1;
    }
    if (e1.weight == e2.weight) {
      return 0;
    }
    return 1;
  }
}

//represents a maze that is built using kruskals method
class KruskalMaze {

  HashMap<Node, Node> representatives;
  ArrayList<Edge> edges;

  ArrayList<Edge> workListEdges;
  ArrayList<Node> nodes;

  int numNodesX;
  int numNodesY;

  KruskalMaze() {
    this.workListEdges = new ArrayList<Edge>();
    this.nodes = new ArrayList<Node>();

    this.edges = new ArrayList<Edge>();
    this.representatives = new HashMap<Node, Node>();
  }

  // maps each node to itself in the hasmap representatives
  void mapRepresentatives() {
    for (int i = 0; i < nodes.size(); i++) {
      this.representatives.put(this.nodes.get(i), this.nodes.get(i));
    }
  }

  // initiates kruskals steps
  void doKruskals() {
    while (this.edges.size() != this.nodes.size() - 1) {
      Edge temp = this.workListEdges.get(0);
      this.workListEdges.remove(0);

      if (!this.findRepresentative(temp.n1).equals(this.findRepresentative(temp.n2))) {
        this.representatives.replace(this.findRepresentative(temp.n2),
            this.findRepresentative(temp.n1));
        this.edges.add(temp);
      }
    }
  }

  // gets representatives of a node
  Node findRepresentative(Node n) {
    if (this.representatives.get(n).equals(n)) {
      return n;
    }
    return this.findRepresentative(this.representatives.get(n));

  }

  // generates list of nodes
  void createBaseNodes(int numNodesX, int numNodesY) {
    this.numNodesX = numNodesX;
    this.numNodesY = numNodesY;
    for (int x = 0; x < this.numNodesX * this.numNodesY; x++) {
      this.nodes.add(new Node(x));
    }
  }

  // generates lists of edges with random weights for workListEdges
  void createBaseEdges(Random rand) {
    for (int i = 0; i < this.nodes.size(); i++) {

      // left to right edges
      if ((i + 1) % this.numNodesX != 0) {
        this.workListEdges
            .add(new Edge(this.nodes.get(i), this.nodes.get(i + 1), rand.nextInt(101)));
      }
      // top to bottom edges
      if (i < this.numNodesX * this.numNodesY - this.numNodesX) {
        this.workListEdges.add(
            new Edge(this.nodes.get(i), this.nodes.get(i + this.numNodesY), rand.nextInt(101)));
      }

    }
  }

  // sorts list of edges from shortest to longest by weight
  void sort() {
    Collections.sort(this.workListEdges, new WeightComparator());
  }
}

// represents image of a maze
class MazeImage extends World {

  // represents the maze to be drawn
  KruskalMaze maze;

  // represents the number of colomuns of nodes
  static int numNodes_x = 20;
  // represents the number of rows of nodes
  static int numNodes_y = 20;

  // represents the pixel size of seach node as a square
  static int CELL_SIZE = 30;

  // represents the width of the canvas
  static int CANVAS_SIZE_X = numNodes_x * CELL_SIZE;

  // represents the height of the canvas
  static int CANVAS_SIZE_Y = numNodes_y * CELL_SIZE;

  MazeImage() {
    super();
    maze = new KruskalMaze();
    maze.createBaseNodes(numNodes_x, numNodes_y);
    maze.createBaseEdges(new Random());
    maze.sort();
    maze.mapRepresentatives();
    maze.doKruskals();
  }

  public WorldScene makeScene() {
    return this.drawEdges(this.drawNodes(new WorldScene(CANVAS_SIZE_X, CANVAS_SIZE_Y)));
  }

  public WorldScene drawNodes(WorldScene scene) {
    for (Node n : maze.nodes) {
      scene = n.draw(scene);
    }
    return scene;
  }

  public WorldScene drawEdges(WorldScene scene) {
    for (Edge e : maze.edges) {
      scene = e.draw(scene);
    }
    return scene;
  }
}

class ExamplesKruskal {

  KruskalMaze km1;
  KruskalMaze km2;

  HashMap<Node, Node> r1;
  Node A;
  Node B;
  Node C;
  Node D;
  Node E;
  Node F;

  ArrayList<Edge> testEdges;

  Random rand;
  Random rand1;
  Random rand2;
  Random rand3;
  Random rand4;

  WorldScene testScene;
  WorldScene testScene2;

  MazeImage world1;

  void initConditions() {
    km1 = new KruskalMaze();
    km2 = new KruskalMaze();

    r1 = new HashMap<Node, Node>();
    A = new Node(1);
    B = new Node(2);
    C = new Node(3);
    D = new Node(4);
    E = new Node(5);
    F = new Node(6);

    testEdges = new ArrayList<Edge>();

    rand = new Random(0);
    rand1 = new Random(1);
    rand2 = new Random(2);
    rand3 = new Random(3);
    rand4 = new Random(4);

    testScene = new WorldScene(MazeImage.CANVAS_SIZE_X, MazeImage.CANVAS_SIZE_Y);
    testScene2 = new WorldScene(MazeImage.CANVAS_SIZE_X, MazeImage.CANVAS_SIZE_Y);

    world1 = new MazeImage();

  }

  // test createBaseNodes in KruskalMaze
  void testCreateBaseNodes(Tester t) {
    initConditions();
    t.checkExpect(km1.nodes.size(), 0);
    km1.createBaseNodes(100, 60);
    t.checkExpect(km1.nodes.size(), 6000);
  }

  // test createBaseEdges in KruskalMaze
  void testCreateBaseEdges(Tester t) {
    initConditions();
    t.checkExpect(km1.workListEdges.size(), 0);
    km1.createBaseNodes(100, 60);
    km1.createBaseEdges(rand);
    t.checkExpect(km1.workListEdges.size(), 99 * 60 + 59 * 100);
  }

  // test sort in KruskalMaze
  void testSort(Tester t) {
    initConditions();
    km1.createBaseNodes(100, 60);
    km1.createBaseEdges(rand1);
    t.checkExpect(km1.workListEdges.get(0).weight, 97);
    t.checkExpect(km1.workListEdges.get(1).weight, 5);
    t.checkExpect(km1.workListEdges.get(2).weight, 21);
    km1.sort();
    t.checkExpect(km1.workListEdges.get(0).weight, 0);
    t.checkExpect(km1.workListEdges.get(1).weight, 0);
    t.checkExpect(km1.workListEdges.get(2).weight, 0);
    for (int i = 0; i < km1.workListEdges.size() - 1; i++) {
      t.checkExpect(km1.workListEdges.get(i).weight <= km1.workListEdges.get(i + 1).weight, true);
    }
  }

  // test mapRepresentatives in KruskalMaze
  void testMapRepresentatives(Tester t) {
    initConditions();
    km1.createBaseNodes(100, 60);
    km1.mapRepresentatives();
    for (Map.Entry<Node, Node> entry : km1.representatives.entrySet()) {
      t.checkExpect(entry.getKey(), entry.getValue());
    }
  }

  // test findRepresentative in KruskalMaze
  void testFindRepresentative(Tester t) {
    initConditions();
    r1.put(A, E);
    r1.put(B, A);
    r1.put(C, E);
    r1.put(D, E);
    r1.put(E, E);
    r1.put(F, F);
    km1.representatives = r1;
    t.checkExpect(km1.findRepresentative(A).equals(E), true);
    t.checkExpect(km1.findRepresentative(B).equals(E), true);
    t.checkExpect(km1.findRepresentative(C).equals(E), true);
    t.checkExpect(km1.findRepresentative(D).equals(E), true);
    t.checkExpect(km1.findRepresentative(E).equals(E), true);
    t.checkExpect(km1.findRepresentative(F).equals(F), true);

    initConditions();
    r1.put(A, E);
    r1.put(B, A);
    r1.put(C, E);
    r1.put(D, E);
    r1.put(E, E);
    r1.put(F, D);
    km1.representatives = r1;
    t.checkExpect(km1.findRepresentative(A).equals(E), true);
    t.checkExpect(km1.findRepresentative(B).equals(E), true);
    t.checkExpect(km1.findRepresentative(C).equals(E), true);
    t.checkExpect(km1.findRepresentative(D).equals(E), true);
    t.checkExpect(km1.findRepresentative(E).equals(E), true);
    t.checkExpect(km1.findRepresentative(F).equals(E), true);
  }

  // test DoKruskal in KruskalMaze
  void testDoKruskals(Tester t) {
    // test set made from assigment document
    initConditions();
    r1.put(A, A);
    r1.put(B, B);
    r1.put(C, C);
    r1.put(D, D);
    r1.put(E, E);
    r1.put(F, F);
    km1.representatives = r1;

    testEdges.add(new Edge(E, C, 15));
    testEdges.add(new Edge(C, D, 25));
    testEdges.add(new Edge(A, B, 30));

    testEdges.add(new Edge(B, E, 35));
    testEdges.add(new Edge(B, C, 40));
    testEdges.add(new Edge(F, D, 50));
    testEdges.add(new Edge(A, E, 50));
    testEdges.add(new Edge(B, F, 50));
    km1.workListEdges = testEdges;

    km1.nodes.add(A);
    km1.nodes.add(B);
    km1.nodes.add(C);
    km1.nodes.add(D);
    km1.nodes.add(E);
    km1.nodes.add(F);

    km1.doKruskals();

    t.checkExpect(km1.edges.size(), 5);
    // all have the same representative
    t.checkExpect(km1.findRepresentative(A).equals(F), true);
    t.checkExpect(km1.findRepresentative(B).equals(F), true);
    t.checkExpect(km1.findRepresentative(C).equals(F), true);
    t.checkExpect(km1.findRepresentative(D).equals(F), true);
    t.checkExpect(km1.findRepresentative(E).equals(F), true);
    t.checkExpect(km1.findRepresentative(F).equals(F), true);

    // all nodes have the same representative
    initConditions();
    km2 = new KruskalMaze();
    km2.createBaseNodes(100, 60);
    km2.createBaseEdges(new Random());
    km2.sort();
    km2.mapRepresentatives();
    km2.doKruskals();
    Node testNode = km2.findRepresentative(km2.nodes.get(0));
    for (int i = 0; i < km2.nodes.size(); i++) {
      t.checkExpect(km2.findRepresentative(km2.nodes.get(i)).equals(testNode), true);
    }
  }

  // drawEdge in Edge
  void testdrawEdge(Tester t) {
    initConditions();
    t.checkExpect(testScene, new WorldScene(MazeImage.CANVAS_SIZE_X, MazeImage.CANVAS_SIZE_Y));
    new Edge(A, B, 15).draw(testScene);

    testScene2.placeImageXY(
        new RectangleImage(1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID, Color.white), 61, 16);
    t.checkExpect(testScene, testScene2);

    initConditions();
    km2 = new KruskalMaze();
    km2.createBaseNodes(100, 60);
    km2.createBaseEdges(rand4);
    km2.sort();
    km2.mapRepresentatives();
    km2.doKruskals();

    km2.edges.get(0).draw(testScene);
    testScene2.placeImageXY(
        new RectangleImage(1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID, Color.white), 61, 106);
    t.checkExpect(testScene, testScene2);
  }

  // drawNode in Node
  void testDrawNode(Tester t) {
    initConditions();
    t.checkExpect(testScene, new WorldScene(MazeImage.CANVAS_SIZE_X, MazeImage.CANVAS_SIZE_Y));
    A.draw(testScene);
    testScene2
        .placeImageXY(
            new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.OUTLINE,
                Color.black).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
            30, 0);
    t.checkExpect(testScene, testScene2);

    initConditions();
    t.checkExpect(testScene, new WorldScene(MazeImage.CANVAS_SIZE_X, MazeImage.CANVAS_SIZE_Y));
    B.draw(testScene);
    testScene2
        .placeImageXY(
            new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.OUTLINE,
                Color.black).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
            60, 0);
    t.checkExpect(testScene, testScene2);

    initConditions();
    t.checkExpect(testScene, new WorldScene(MazeImage.CANVAS_SIZE_X, MazeImage.CANVAS_SIZE_Y));
    C.draw(testScene);
    testScene2
        .placeImageXY(
            new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.OUTLINE,
                Color.black).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
            90, 0);
    t.checkExpect(testScene, testScene2);
  }

  // makeScene in MazeImage
  void testMakeScene(Tester t) {
    initConditions();
    world1.maze = km1;
    t.checkExpect(world1.makeScene(), testScene2);
    world1.maze.nodes.add(A);
    testScene2
        .placeImageXY(
            new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.OUTLINE,
                Color.black).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
            30, 0);

    t.checkExpect(world1.makeScene(), testScene2);

    initConditions();
    world1.maze = km1;
    t.checkExpect(world1.makeScene(), testScene2);
    world1.maze.edges.add(new Edge(A, B, 15));
    testScene2.placeImageXY(
        new RectangleImage(1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID, Color.white), 61, 16);
    t.checkExpect(world1.makeScene(), testScene2);
  }

  // drawEdges in MazeImage
  void testDrawEdges(Tester t) {
    initConditions();
    world1.maze = km1;
    t.checkExpect(world1.drawEdges(testScene), testScene2);
    world1.maze.edges.add(new Edge(A, B, 15));
    testScene2.placeImageXY(
        new RectangleImage(1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID, Color.white), 61, 16);

    t.checkExpect(world1.drawEdges(testScene), testScene2);
  }

  // drawNodes in MazeImage
  void testDrawNodes(Tester t) {
    initConditions();
    world1.maze = km1;
    t.checkExpect(world1.drawNodes(testScene), testScene2);
    world1.maze.nodes.add(A);
    testScene2
        .placeImageXY(
            new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.OUTLINE,
                Color.black).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
            30, 0);

    t.checkExpect(world1.drawNodes(testScene), testScene2);
  }

  // compare in weightCompartor
  void testCompareTo(Tester t) {
    initConditions();
    t.checkExpect(new WeightComparator().compare(new Edge(E, C, 15), new Edge(E, C, 15)), 0);
    t.checkExpect(new WeightComparator().compare(new Edge(E, C, 15), new Edge(A, B, 15)), 0);
    t.checkExpect(new WeightComparator().compare(new Edge(A, B, 15), new Edge(E, C, 15)), 0);

    t.checkExpect(new WeightComparator().compare(new Edge(E, C, 15), new Edge(E, C, 25)), -1);
    t.checkExpect(new WeightComparator().compare(new Edge(E, C, 15), new Edge(A, B, 25)), -1);
    t.checkExpect(new WeightComparator().compare(new Edge(A, B, 15), new Edge(E, C, 25)), -1);

    t.checkExpect(new WeightComparator().compare(new Edge(E, C, 25), new Edge(E, C, 15)), 1);
    t.checkExpect(new WeightComparator().compare(new Edge(E, C, 25), new Edge(A, B, 15)), 1);
    t.checkExpect(new WeightComparator().compare(new Edge(A, B, 25), new Edge(E, C, 15)), 1);
  }

  // runs game
  void testBigBang(Tester t) {
    MazeImage world = new MazeImage();
    world.bigBang(MazeImage.CANVAS_SIZE_X + 1, MazeImage.CANVAS_SIZE_Y + 1, 1.0 / 30.0);
  }
}
