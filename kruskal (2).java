import java.util.*;
import tester.*;
import javalib.worldimages.*;
import javalib.impworld.*;
import java.awt.Color;

// represents and edge between two ndoes
class Edge {
  Node n1; // left or top node
  Node n2; // right of bottom node

  // represents if this edge has been visited by search algorithm
  Boolean visited;
  int weight;

  Edge(Node n1, Node n2, int weight) {
    this.n1 = n1;
    this.n2 = n2;
    this.visited = false;
    this.weight = weight;
  }

  // draws the edges
  // this works actually by drawing an opening in each node
  // and drawing a white line where an opening should between on top of a square
  // node
  WorldScene draw(WorldScene scene) {
    Color c = n1.c;
    int n1x = (this.n1.num % MazeImage.numNodes_x) * MazeImage.CELL_SIZE;
    int n1y = (int) (this.n1.num / MazeImage.numNodes_x) * MazeImage.CELL_SIZE;
    int n2x = (this.n2.num % MazeImage.numNodes_x) * MazeImage.CELL_SIZE;
    int n2y = (int) (this.n2.num / MazeImage.numNodes_x) * MazeImage.CELL_SIZE;

    // left to right
    if (n2y == n1y) {
      scene.placeImageXY(new RectangleImage(1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID, c),
          (n1x + n2x) / 2 + MazeImage.CELL_SIZE / 2 + 1, n2y + MazeImage.CELL_SIZE / 2 + 1);
    }
    // up and down
    if (n2x == n1x) {
      scene.placeImageXY(new RectangleImage(MazeImage.CELL_SIZE - 1, 1, OutlineMode.SOLID, c),
          n2x + MazeImage.CELL_SIZE / 2 + 1, (n1y + n2y) / 2 + MazeImage.CELL_SIZE / 2 + 1);
    }
    return scene;
  }
}

//represents a cell in the the maze
class Node {

  int num;
  // white for not visited
  // red is target
  // light blue for visited
  // green for path
  Color c;

  // paths from this node to another node
  ArrayList<Edge> exits;

  Node(int num) {
    this.num = num;
    this.exits = new ArrayList<Edge>();
    this.c = Color.white;
  }

  Node(int num, Color c) {
    this.num = num;
    this.exits = new ArrayList<Edge>();
    this.c = c;
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

  // draws nodes to show which have been visited by search algorithms
  WorldScene drawSearchAlgorithms(WorldScene scene) {
    scene.placeImageXY(
        new RectangleImage(MazeImage.CELL_SIZE - 1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID, c)
            .movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
        (this.num % MazeImage.numNodes_x) * MazeImage.CELL_SIZE + 1,
        (int) (this.num / MazeImage.numNodes_x) * MazeImage.CELL_SIZE + 1);
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

  // workList used to do kruskals method
  ArrayList<Edge> workListEdges;
  ArrayList<Node> nodes;

  // workList used for breadth first search
  Queue<Node> bfsWorklist;
  // workList used for depth first search
  Stack<Node> dfsWorklist;

  HashMap<Node, Edge> cameFromEdge;

  int numNodesX;
  int numNodesY;

  KruskalMaze() {
    this.workListEdges = new ArrayList<Edge>();
    this.nodes = new ArrayList<Node>();

    this.edges = new ArrayList<Edge>();
    this.representatives = new HashMap<Node, Node>();

    this.bfsWorklist = new LinkedList<Node>();
    this.dfsWorklist = new Stack<Node>();
    this.cameFromEdge = new HashMap<Node, Edge>();
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
    for (int x = 0; x < this.numNodesX * this.numNodesY - 1; x++) {
      this.nodes.add(new Node(x));
    }
    this.nodes.add(new Node(this.numNodesX * this.numNodesY - 1, Color.red));
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
            new Edge(this.nodes.get(i), this.nodes.get(i + this.numNodesX), rand.nextInt(101)));
      }

    }
  }

  // sorts list of edges from shortest to longest by weight
  void sort() {
    Collections.sort(this.workListEdges, new WeightComparator());
  }

  // sets the exits in each node so that you can access each edge from its
  // corresponding node
  void setEdges() {
    for (Edge e : this.edges) {
      e.n1.exits.add(e);
      e.n2.exits.add(e);
    }
  }

  // does BFS algorithm
  void doBFS() {
    if (!bfsWorklist.isEmpty()) {
      Node next = bfsWorklist.element();
      if (next.c.equals(Color.blue)) {
        bfsWorklist.remove();
        // call the method again so visually a tile is updated every 5 tics
        this.doBFS();
      }
      else if (next.c.equals(Color.red)) {
        this.reconstruct();
        this.bfsWorklist.removeAll(bfsWorklist);
        return;
      }
      else {
        next.c = Color.blue;
        for (Edge e : next.exits) {
          if (!e.visited) {
            e.visited = true;
            if (!e.n1.c.equals(Color.blue)) {
              bfsWorklist.add(e.n1);
              this.cameFromEdge.put(e.n1, e);
            }
            if (!e.n2.c.equals(Color.blue)) {
              bfsWorklist.add(e.n2);
              this.cameFromEdge.put(e.n2, e);
            }
          }
        }
      }
    }
  }

  // does DFS algorithm
  void doDFS() {
    if (!dfsWorklist.isEmpty()) {
      Node next = dfsWorklist.peek();
      if (next.c.equals(Color.blue)) {
        dfsWorklist.pop();
        this.doDFS();
      }
      else if (next.c.equals(Color.red)) {
        this.reconstruct();
        this.dfsWorklist.removeAll(dfsWorklist);
        return;
      }
      else {
        next.c = Color.blue;
        for (Edge e : next.exits) {
          if (!e.visited) {
            e.visited = true;
            if (!e.n1.c.equals(Color.blue)) {
              dfsWorklist.add(e.n1);
              this.cameFromEdge.put(e.n1, e);
            }
            if (!e.n2.c.equals(Color.blue)) {
              dfsWorklist.add(e.n2);
              this.cameFromEdge.put(e.n2, e);
            }
          }
        }
      }
    }
  }

  // after finishing a search algorithm,
  // reconstruct cameFromEdge to display path from start to end
  void reconstruct() {
    // starts with last node;
    Node currentNode = this.nodes.get(this.nodes.size() - 1);
    while (!this.cameFromEdge.get(currentNode).n1.equals(this.nodes.get(0))) {
      currentNode.c = Color.green;
      Edge currentEdge = this.cameFromEdge.get(currentNode);
      if (currentNode.equals(currentEdge.n1)) {
        currentNode = currentEdge.n2;
      }
      else {
        currentNode = currentEdge.n1;
      }
    }
    currentNode.c = Color.green;
    this.cameFromEdge.get(currentNode).n1.c = Color.green;
  }
}

// represents image of a maze
class MazeImage extends World {

  // represents the maze to be drawn
  KruskalMaze maze;

  // represents the number of colomuns of nodes
  static int numNodes_x = 20;
  // represents the number of rows of nodes
  static int numNodes_y = 10;

  // represents the pixel size of seach node as a square
  static int CELL_SIZE = 30;

  // represents the width of the canvas
  static int CANVAS_SIZE_X = numNodes_x * CELL_SIZE;

  // represents the height of the canvas
  static int CANVAS_SIZE_Y = numNodes_y * CELL_SIZE;

  // every second we do two nodes of searching
  int counter;

  // represents the type of search happening,
  // "BFS" for breadth first search
  // "DFS" for depth first search
  // "" for no search
  String searchType;

  MazeImage() {
    super();
    maze = new KruskalMaze();
    maze.createBaseNodes(numNodes_x, numNodes_y);
    maze.createBaseEdges(new Random());
    maze.sort();
    maze.mapRepresentatives();
    maze.doKruskals();
    this.counter = 0;
    this.searchType = "";
  }

  // draws the scene
  public WorldScene makeScene() {

    return this.drawEdges(this.drawNodes(new WorldScene(CANVAS_SIZE_X, CANVAS_SIZE_Y)));
  }

  // draws all the nodes
  public WorldScene drawNodes(WorldScene scene) {
    for (Node n : maze.nodes) {
      scene = n.draw(n.drawSearchAlgorithms(scene));
    }
    return scene;
  }

  // draws all the edges
  public WorldScene drawEdges(WorldScene scene) {
    for (Edge e : maze.edges) {
      scene = e.draw(scene);
    }
    return scene;
  }

  // every 5 ticks, if we are doing BFS or DPS iniates one step in the process
  @Override
  public void onTick() {
    this.counter += 1;
    if (counter > 5) {
      counter = 0;
      if (searchType.equals("BFS")) {
        this.maze.doBFS();
      }
      else if (searchType.equals("DFS")) {
        this.maze.doDFS();
      }
    }
  }

  public void onKeyEvent(String key) {
    // if the user presses b we start breadth first search
    // if the user presses d we start depth first search
    // if the user presses r we clear both searches
    if (key.equals("b") && this.searchType.equals("")) {
      this.maze.setEdges();
      this.maze.bfsWorklist.add(this.maze.nodes.get(0));
      this.searchType = "BFS";
    }
    else if (key.equals("d") && this.searchType.equals("")) {
      this.maze.setEdges();
      this.maze.dfsWorklist.add(this.maze.nodes.get(0));
      this.searchType = "DFS";
    }
    if (key.equals("r")) {
      for (Node n : this.maze.nodes) {
        n.c = Color.white;
      }
      for (Edge e : this.maze.edges) {
        e.visited = false;
      }
      this.maze.cameFromEdge = new HashMap<Node, Edge>();
      this.maze.bfsWorklist = new LinkedList<Node>();
      this.maze.dfsWorklist = new Stack<Node>();
      this.maze.nodes.get(this.maze.nodes.size() - 1).c = Color.red;
      this.searchType = "";
    }
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
  Random rand5;
  Random rand6;

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
    rand5 = new Random(5);
    rand6 = new Random(6);

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
    testScene2.placeImageXY(
        new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.SOLID, Color.white)
            .movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
        30, 0);
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
    testScene2.placeImageXY(
        new RectangleImage(MazeImage.CELL_SIZE, MazeImage.CELL_SIZE, OutlineMode.SOLID, Color.white)
            .movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
        30, 0);
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

  // onTick in MazeImage
  void testOnTick(Tester t) {
    initConditions();
    t.checkExpect(world1.counter, 0);
    world1.onTick();
    t.checkExpect(world1.counter, 1);
    world1.counter = 6;
    world1.onTick();
    t.checkExpect(world1.counter, 0);
    world1.onKeyEvent("d");
    t.checkExpect(world1.maze.dfsWorklist.size(), 1);
    world1.onTick();
    t.checkExpect(world1.counter, 1);
    t.checkExpect(world1.maze.dfsWorklist.size(), 1);
    world1.counter = 6;
    world1.onTick();
    t.checkExpect(world1.counter, 0);
    t.checkExpect(world1.maze.dfsWorklist.size() > 1, true);
  }

  // drawSearchAlgorithms in Node
  void testDrawSearchAlgorithms(Tester t) {
    initConditions();
    t.checkExpect(testScene, testScene2);
    A.drawSearchAlgorithms(testScene);
    testScene2.placeImageXY(
        new RectangleImage(MazeImage.CELL_SIZE - 1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID,
            Color.white).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
        (0 % MazeImage.numNodes_x) * MazeImage.CELL_SIZE + 1,
        (int) (0 / MazeImage.numNodes_x) * MazeImage.CELL_SIZE + 1);

    initConditions();
    t.checkExpect(testScene, testScene2);
    A.c = Color.red;
    A.drawSearchAlgorithms(testScene);
    testScene2.placeImageXY(
        new RectangleImage(MazeImage.CELL_SIZE - 1, MazeImage.CELL_SIZE - 1, OutlineMode.SOLID,
            Color.red).movePinhole(-MazeImage.CELL_SIZE / 2, -MazeImage.CELL_SIZE / 2),
        (0 % MazeImage.numNodes_x) * MazeImage.CELL_SIZE + 1,
        (int) (0 / MazeImage.numNodes_x) * MazeImage.CELL_SIZE + 1);
  }

  // onKeyEvent in MazeImages
  void testOnKeyEvent(Tester t) {
    initConditions();
    t.checkExpect(world1.searchType, "");
    world1.onKeyEvent("d");
    t.checkExpect(world1.searchType, "DFS");
    t.checkExpect(world1.maze.dfsWorklist.size(), 1);
    world1.onKeyEvent("d");
    t.checkExpect(world1.searchType, "DFS");
    t.checkExpect(world1.maze.dfsWorklist.size(), 1);
    world1.onKeyEvent("b");
    t.checkExpect(world1.searchType, "DFS");
    t.checkExpect(world1.maze.dfsWorklist.size(), 1);
    t.checkExpect(world1.maze.bfsWorklist.size(), 0);
    initConditions();
    t.checkExpect(world1.searchType, "");
    world1.onKeyEvent("b");
    t.checkExpect(world1.searchType, "BFS");
    t.checkExpect(world1.maze.bfsWorklist.size(), 1);
    world1.onKeyEvent("r");
    t.checkExpect(world1.searchType, "");
    t.checkExpect(world1.maze.bfsWorklist.size(), 0);
    world1.onKeyEvent("b");
    t.checkExpect(world1.searchType, "BFS");
    t.checkExpect(world1.maze.bfsWorklist.size(), 1);
  }

  // setEdges in KruskalsMaze
  void testSetEdges(Tester t) {
    initConditions();
    km1 = new KruskalMaze();
    km1.createBaseNodes(100, 60);
    km1.createBaseEdges(rand5);
    km1.sort();
    km1.mapRepresentatives();
    km1.doKruskals();
    t.checkExpect(km1.nodes.get(0).exits.size(), 0);
    km1.setEdges();
    t.checkExpect(km1.nodes.get(0).exits.size() != 0, true);
    t.checkExpect(km1.nodes.get(0).exits.get(0).n1, km1.nodes.get(0));

    km2 = new KruskalMaze();
    km2.createBaseNodes(100, 60);
    km2.createBaseEdges(rand5);
    km2.sort();
    km2.mapRepresentatives();
    km2.doKruskals();
    t.checkExpect(km2.nodes.get(0).exits.size(), 0);
    km2.setEdges();
    t.checkExpect(km2.nodes.get(0).exits.size() != 0, true);
    t.checkExpect(km2.nodes.get(0).exits.get(0).n1, km2.nodes.get(0));
  }

  // BFS in KruskalsMaze
  void testBFS(Tester t) {
    initConditions();
    km1 = new KruskalMaze();
    km1.createBaseNodes(2, 2);
    km1.createBaseEdges(rand6);
    km1.sort();
    km1.mapRepresentatives();
    km1.doKruskals();
    km1.setEdges();
    km1.bfsWorklist.add(km1.nodes.get(0));
    t.checkExpect(km1.bfsWorklist.size(), 1);
    t.checkExpect(km1.cameFromEdge.size(), 0);
    t.checkExpect(km1.nodes.get(0).c, Color.white);
    t.checkExpect(km1.nodes.get(1).c, Color.white);
    t.checkExpect(km1.nodes.get(2).c, Color.white);
    t.checkExpect(km1.nodes.get(3).c, Color.red);
    km1.doBFS();
    t.checkExpect(km1.bfsWorklist.size(), 3);
    t.checkExpect(km1.cameFromEdge.size(), 2);
    t.checkExpect(km1.nodes.get(0).c, Color.blue);
    t.checkExpect(km1.nodes.get(1).c, Color.white);
    t.checkExpect(km1.nodes.get(2).c, Color.white);
    t.checkExpect(km1.nodes.get(3).c, Color.red);

    km1.doBFS();
    t.checkExpect(km1.bfsWorklist.size(), 3);
    t.checkExpect(km1.cameFromEdge.size(), 3);
    t.checkExpect(km1.nodes.get(0).c, Color.blue);
    t.checkExpect(km1.nodes.get(1).c, Color.blue);
    t.checkExpect(km1.nodes.get(2).c, Color.white);
    t.checkExpect(km1.nodes.get(3).c, Color.red);

    km1.doBFS();
    t.checkExpect(km1.bfsWorklist.size(), 2);
    t.checkExpect(km1.cameFromEdge.size(), 3);
    t.checkExpect(km1.nodes.get(0).c, Color.blue);
    t.checkExpect(km1.nodes.get(1).c, Color.blue);
    t.checkExpect(km1.nodes.get(2).c, Color.blue);
    t.checkExpect(km1.nodes.get(3).c, Color.red);

    km1.doBFS();
    t.checkExpect(km1.bfsWorklist.size(), 0);
    t.checkExpect(km1.cameFromEdge.size(), 3);
    t.checkExpect(km1.nodes.get(0).c, Color.green);
    t.checkExpect(km1.nodes.get(1).c, Color.green);
    t.checkExpect(km1.nodes.get(2).c, Color.blue);
    t.checkExpect(km1.nodes.get(3).c, Color.green);
  }

  // DFS in KruskalsMaze
  void testDFS(Tester t) {
    initConditions();
    km1 = new KruskalMaze();
    km1.createBaseNodes(2, 2);
    km1.createBaseEdges(rand6);
    km1.sort();
    km1.mapRepresentatives();
    km1.doKruskals();
    km1.setEdges();
    km1.dfsWorklist.add(km1.nodes.get(0));
    t.checkExpect(km1.dfsWorklist.size(), 1);
    t.checkExpect(km1.cameFromEdge.size(), 0);
    t.checkExpect(km1.nodes.get(0).c, Color.white);
    t.checkExpect(km1.nodes.get(1).c, Color.white);
    t.checkExpect(km1.nodes.get(2).c, Color.white);
    t.checkExpect(km1.nodes.get(3).c, Color.red);
    km1.doDFS();
    t.checkExpect(km1.dfsWorklist.size(), 3);
    t.checkExpect(km1.cameFromEdge.size(), 2);
    t.checkExpect(km1.nodes.get(0).c, Color.blue);
    t.checkExpect(km1.nodes.get(1).c, Color.white);
    t.checkExpect(km1.nodes.get(2).c, Color.white);
    t.checkExpect(km1.nodes.get(3).c, Color.red);

    km1.doDFS();
    t.checkExpect(km1.dfsWorklist.size(), 3);
    t.checkExpect(km1.cameFromEdge.size(), 2);
    t.checkExpect(km1.nodes.get(0).c, Color.blue);
    t.checkExpect(km1.nodes.get(1).c, Color.white);
    t.checkExpect(km1.nodes.get(2).c, Color.blue);
    t.checkExpect(km1.nodes.get(3).c, Color.red);

    km1.doDFS();
    t.checkExpect(km1.dfsWorklist.size(), 3);
    t.checkExpect(km1.cameFromEdge.size(), 3);
    t.checkExpect(km1.nodes.get(0).c, Color.blue);
    t.checkExpect(km1.nodes.get(1).c, Color.blue);
    t.checkExpect(km1.nodes.get(2).c, Color.blue);
    t.checkExpect(km1.nodes.get(3).c, Color.red);

    km1.doDFS();
    t.checkExpect(km1.dfsWorklist.size(), 0);
    t.checkExpect(km1.cameFromEdge.size(), 3);
    t.checkExpect(km1.nodes.get(0).c, Color.green);
    t.checkExpect(km1.nodes.get(1).c, Color.green);
    t.checkExpect(km1.nodes.get(2).c, Color.blue);
    t.checkExpect(km1.nodes.get(3).c, Color.green);
  }

  // runs game
  void testBigBang(Tester t) {
    // MazeImage world = new MazeImage();
    // world.bigBang(MazeImage.CANVAS_SIZE_X + 1, MazeImage.CANVAS_SIZE_Y + 1, 1.0 /
    // 30.0);
  }
}
