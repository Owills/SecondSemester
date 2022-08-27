import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

abstract class AGameObject {
  int x;
  int y;
  int dx;
  int dy;
  int radius;
  Color color;
  boolean hit; // has a collision occurred

  AGameObject(int x, int y, int dx, int dy, int radius, Color color, boolean hit) {
    this.x = x;
    this.y = y;
    this.dx = dx;
    this.dy = dy;
    this.radius = radius;
    this.color = color;
    this.hit = hit;
  }

  // returns the scene with this AGameObject drawn at the given x and y
  public WorldScene draw(WorldScene scene) {
    return scene.placeImageXY(new CircleImage(this.radius, OutlineMode.SOLID, this.color), this.x,
        this.y);
  }

  // returns true if this AGameObject is outside the border of the game
  boolean isOutside() {
    return this.x < 0 - this.radius || this.x >= 500 + this.radius || this.y >= 500 + this.radius
        || this.y < 0 - this.radius;
  }

  // checks if this AGameObject is marked as having collided with another
  // removes and adds collision bullets at the location if the above is true
  public ILoAGameObject checkRemoveAGameObject(ILoAGameObject rest) {
    if (this.hit) {
      rest = this.addBulletCollision(0, rest);
      return rest.removeCollisions();
    }
    return new ConsLoAGameObject(this, rest.removeCollisions());
  }

  // returns a new AGameObject where x and y are changed by the dx and dy
  // variables
  public abstract AGameObject move();

  // returns a new AGameObject where hit is changed to true
  public abstract AGameObject hit();

  // checks if this AGameObject has collided with other AGameobject
  public boolean collision(AGameObject other) {
    double xDifference = this.x - other.x;
    double yDifference = this.y - other.y;
    double distanceSquared = xDifference * xDifference + yDifference * yDifference;
    boolean collsion = distanceSquared < (this.radius + other.radius) * (this.radius + other.radius)
        - 2;

    return collsion;
  }

  // adds bullets whens bullets collide with ships and are removed
  public abstract ILoAGameObject addBulletCollision(int i, ILoAGameObject loAGameObj);
}

class Ship extends AGameObject {

  Ship(int x, int y, int dx, boolean hit) {
    super(x, y, dx, 0, 20, Color.green, hit);
  }

  // returns a new Ship where hit is changed to true
  public Ship hit() {
    return new Ship(this.x, this.y, this.dx, true);
  }

  // returns a new Ship where x and y are changed by the dx and dy variables
  public Ship move() {
    return new Ship(this.x + dx, this.y, this.dx, this.hit);
  }

  // returns the given ILoAGameObject because the new bullets are spawned
  // at the position of the bullet that collided
  public ILoAGameObject addBulletCollision(int i, ILoAGameObject loAGameObj) {
    return loAGameObj;
  }
}

class Bullet extends AGameObject {

  Bullet(int x, int y, int dx, int dy, int radius, boolean hit) {
    super(x, y, dx, dy, radius, Color.black, hit);
  }

  // returns a new Bullet where hit is changed to true
  public Bullet hit() {
    return new Bullet(this.x, this.y, this.dx, this.dy, this.radius, true);
  }

  // returns a new Bullet where x and y are changed by the dx and dy variables
  public Bullet move() {
    return new Bullet(this.x + dx, this.y + this.dy, this.dx, this.dy, this.radius, this.hit);
  }

  // adds new bullets to the given ILoAGameObject and returns the new list
  public ILoAGameObject addBulletCollision(int i, ILoAGameObject loAGameObj) {
    // radius of the bullet is equal to the number of new bullets that will be
    // spawned
    if (i < this.radius) {
      double angle = i * (360 / (this.radius));

      // velocity slightly increases with each collision
      double velocity = Math.sqrt(this.dx * this.dx + this.dy * this.dy) + 1;

      int newdx = (int) (velocity * Math.cos(Math.toRadians(angle)));
      int newdy = (int) (velocity * Math.sin(Math.toRadians(angle)));

      int newRadius = this.radius + 1;
      // max the size and spawning rate of new bullets to make sure bullets don't take
      // up too much
      // of the screen and don't spawn so many that lag occurs
      if (newRadius > 10) {
        newRadius = 10;
      }
      Bullet bullet = new Bullet(this.x, this.y, newdx, newdy, newRadius, false);
      return this.addBulletCollision(i + 1, loAGameObj.add(bullet));
    }
    return loAGameObj;
  }
}

interface ILoAGameObject {

  // adds a AGameObject to the first spot in the list
  ILoAGameObject add(AGameObject other);

  // updates the position of each ship
  ILoAGameObject move();

  // check which GameObjects that are off screen and removes them
  ILoAGameObject removeOutside();

  // check which GameObjects that are marked for collision and removes them
  ILoAGameObject removeCollisions();

  // draws the objects in the scene
  WorldScene draw(WorldScene scene);

  // gets the length of the list
  int length();

  // check collision with other list of game objects and removes objects the
  // collide with objects in other
  ILoAGameObject checkCollision(ILoAGameObject others);

  // check collision with a AGameObject
  boolean checkCollision(AGameObject other);
}

class MtLoAGameObject implements ILoAGameObject {

  MtLoAGameObject() {
  }

  // adds a AGameObject to the first spot in the list
  public ILoAGameObject add(AGameObject other) {
    return new ConsLoAGameObject(other, this);
  }

  public ILoAGameObject move() {
    return this;
  }

  // there will always be a "empty list" that ships can be added to
  public ILoAGameObject removeOutside() {
    return this;
  }

  public ILoAGameObject removeCollisions() {
    return this;
  }

  public WorldScene draw(WorldScene scene) {
    return scene;
  }

  public int length() {
    return 0;
  }

  public ILoAGameObject checkCollision(ILoAGameObject others) {
    return this;
  }

  public boolean checkCollision(AGameObject other) {
    return false;
  }
}

class ConsLoAGameObject implements ILoAGameObject {
  AGameObject first;
  ILoAGameObject rest;

  ConsLoAGameObject(AGameObject first, ILoAGameObject rest) {
    this.first = first;
    this.rest = rest;
  }

  // adds a AGameObject to the first spot in the list
  public ILoAGameObject add(AGameObject other) {
    return new ConsLoAGameObject(other, this);
  }

  // updates the position of each ship
  public ILoAGameObject move() {
    return new ConsLoAGameObject(this.first.move(), this.rest.move());
  }

  // check which GameObjects that are off screen and removes them
  public ILoAGameObject removeOutside() {
    if (this.first.isOutside()) {
      return this.rest.removeOutside();
    }
    return new ConsLoAGameObject(this.first, this.rest.removeOutside());
  }

  // check which GameObjects that are marked for collision and removes them
  public ILoAGameObject removeCollisions() {
    return this.first.checkRemoveAGameObject(this.rest);
  }

  // draws the objects in the scene
  public WorldScene draw(WorldScene scene) {
    return this.rest.draw(this.first.draw(scene));
  }

  // gets the length of the list
  public int length() {
    return 1 + rest.length();
  }

  // check collision with other list of game objects and removes objects the
  // collide with objects in other
  public ILoAGameObject checkCollision(ILoAGameObject others) {
    boolean collisionWithFirst = others.checkCollision(first);
    if (collisionWithFirst) {
      return new ConsLoAGameObject(this.first.hit(), rest.checkCollision(others));
    }
    return new ConsLoAGameObject(this.first, rest.checkCollision(others));
  }

  // check collision with a AGameObject
  public boolean checkCollision(AGameObject other) {
    boolean collision = this.first.collision(other);
    if (collision) {
      return true;
    }
    return rest.checkCollision(other);
  }

}

class MyGame extends World {

  int numBullets; // number of bullets the player can shoot

  int shipsDestroyed; // number of ships that have been destroyed

  int shipSpawnCounter; // counters the number of ticks that have passed
  int width;
  int height;
  ILoAGameObject ships;
  ILoAGameObject bullets;
  WorldScene scene;

  boolean welcomeScreen;

  Random rand;

  // initial constructor
  MyGame(int numBullets) {
    this.numBullets = numBullets;
    this.shipsDestroyed = 0;

    this.shipSpawnCounter = 0;
    this.width = 500;
    this.height = 500;
    this.welcomeScreen = true;
    this.scene = new WorldScene(this.width, this.height);
    this.ships = new MtLoAGameObject();
    this.bullets = new MtLoAGameObject();
    this.rand = new Random();
  }

  // The constructor for use in "real" games
  // constructor when adding new ships and bullets
  MyGame(int shipSpawnCounter, int shipsDestroyed, int numBullets, ILoAGameObject ships,
      ILoAGameObject bullets) {
    this.numBullets = numBullets;
    this.shipsDestroyed = shipsDestroyed;
    this.shipSpawnCounter = shipSpawnCounter;
    this.width = 500;
    this.height = 500;
    this.welcomeScreen = false;
    this.ships = ships;
    this.bullets = bullets;
    this.scene = new WorldScene(this.width, this.height);
    this.rand = new Random();
  }

  // The constructors used to test world
  MyGame(int numBullets, Random rand) {
    this.numBullets = numBullets;
    this.shipsDestroyed = 0;

    this.shipSpawnCounter = 0;
    this.width = 500;
    this.height = 500;
    this.welcomeScreen = true;
    this.scene = new WorldScene(this.width, this.height);
    this.ships = new MtLoAGameObject();
    this.bullets = new MtLoAGameObject();
    this.rand = rand;
  }

  MyGame(int shipSpawnCounter, int shipsDestroyed, int numBullets, ILoAGameObject ships,
      ILoAGameObject bullets, Random rand) {
    this.numBullets = numBullets;
    this.shipsDestroyed = shipsDestroyed;
    this.shipSpawnCounter = shipSpawnCounter;
    this.width = 500;
    this.height = 500;
    this.welcomeScreen = false;
    this.ships = ships;
    this.bullets = bullets;
    this.scene = new WorldScene(this.width, this.height);
    this.rand = rand;
  }

  @Override
  public WorldScene makeScene() {

    if (this.welcomeScreen) {
      scene = this.addWelcomeMessage(scene);
    }
    else {
      scene = this.drawShips(scene);
      scene = this.drawBullets(scene);
      scene = this.addInfoToScene(scene);
    }

    return scene;
  }

  WorldScene addWelcomeMessage(WorldScene scene) {
    return scene.placeImageXY(new TextImage("Game will start shortly.", Color.green), 250, 250);
  }

  // draws each Ship in the scene
  WorldScene drawShips(WorldScene scene) {
    return this.ships.draw(scene);
  }

  // draws each Bullet in the scene
  WorldScene drawBullets(WorldScene scene) {
    return this.bullets.draw(scene);
  }

  // puts text on screen regarding the information of the following:
  // the number of bullets on the screen
  // the number of ships on screen
  // the number of bullets the player has left
  // the number of ships the player has destroyed so far
  WorldScene addInfoToScene(WorldScene scene) {
    return scene
        .placeImageXY(
            new TextImage(
                "Bullets on Screen: " + Integer.toString(this.bullets.length())
                    + ". Number of Ships: " + Integer.toString(this.ships.length()) + ".",
                Color.black),
            145, 20)
        .placeImageXY(
            new TextImage("Bullets left: " + Integer.toString(this.numBullets)
                + ". Ships Destroyed: " + Integer.toString(this.shipsDestroyed) + ".", Color.black),
            130, 40);
  }

  @Override
  // This method gets called every tick rate seconds ( see bellow in example
  // class).
  public MyGame onTick() {

    if (this.shipSpawnCounter > 30) { // spawn ships every 30 ticks
      this.shipSpawnCounter = 0;

      int randShipSpawn = 1 + (this.rand.nextInt(3));

      this.ships = addShips(this.ships, randShipSpawn);
    }

    this.shipSpawnCounter += 1;
    return this.moveGameObjects().checkCollisionGameObjects().removeGameObjects();
  }

  // spawns new ships on the map every second at
  // randomly locations on the left and right sides of the screen
  public ILoAGameObject addShips(ILoAGameObject ships, int i) {
    if (i > 0) {
      int randX = this.width * this.rand.nextInt(2);
      int randY = 40 + this.rand.nextInt(this.height - 80);
      int dx = -5;
      if (randX == 0) {
        dx = 5;
      }
      AGameObject ship = new Ship(randX, randY, dx, false);
      return addShips(ships.add(ship), i - 1);
    }
    return ships;
  }

  // moves each Bullet and Ship in the the list of bullets and list of ships
  // respectively
  public MyGame moveGameObjects() {
    return new MyGame(this.shipSpawnCounter, this.shipsDestroyed, this.numBullets,
        this.ships.move(), this.bullets.move());
  }

  // checks each Bullet and Ship in the the list of bullets and list of ships
  // respectively
  // to see if they have collided with another and marks each that have collided
  public MyGame checkCollisionGameObjects() {
    return new MyGame(this.shipSpawnCounter, this.shipsDestroyed, this.numBullets,
        this.ships.checkCollision(this.bullets), this.bullets.checkCollision(this.ships));
  }

  // removes Each Bullet and Ship that is outside the game or has been marked for
  // collision
  public MyGame removeGameObjects() {
    int shipsRemoved = this.ships.length() - this.ships.removeCollisions().length();
    return new MyGame(this.shipSpawnCounter, this.shipsDestroyed + shipsRemoved, this.numBullets,
        this.ships.removeOutside().removeCollisions(),
        this.bullets.removeOutside().removeCollisions());
  }

  public MyGame onKeyEvent(String key) {
    // if the user presses space we add a bullet at the center of the screen
    if (key.equals(" ") && this.numBullets > 0) { 
      AGameObject bullet = new Bullet(249, 500, 0, -10, 2, false);
      return new MyGame(this.shipSpawnCounter, this.shipsDestroyed, this.numBullets - 1, this.ships,
          this.bullets.add(bullet));
    }
    else {
      return this;
    }
  }

  // Check to see if we need to end the game.
  @Override
  public WorldEnd worldEnds() {
    if (this.numBullets == 0 && this.bullets.length() <= 0) {
      return new WorldEnd(true, this.makeEndScene());
    }
    else {
      return new WorldEnd(false, this.makeEndScene());
    }
  }

  public WorldScene makeEndScene() {
    WorldScene endScene = new WorldScene(this.width, this.height);
    return endScene.placeImageXY(new TextImage("Game Over", Color.red), 250, 250);
  }

}

class ExamplesMyWorldProgram {
  // Examples for the game
  AGameObject ship = new Ship(20, 20, 30, false);
  AGameObject shipMove = new Ship(50, 20, 30, false);
  AGameObject shipHit = new Ship(20, 20, 30, true);
  AGameObject unmovingShip = new Ship(20, 20, 0, false);
  AGameObject shipOutside = new Ship(-100, -100, 0, false);

  AGameObject bullet = new Bullet(5, 5, 10, 0, 2, false);
  AGameObject bulletHit = new Bullet(5, 5, 10, 0, 2, true);
  AGameObject bulletMove = new Bullet(15, 5, 10, 0, 2, false);
  AGameObject bulletOutside = new Bullet(-100, -100, 10, 2, 10, false);

  AGameObject bulletCollision1 = new Bullet(5, 5, -11, 0, 3, false);
  AGameObject bulletCollision2 = new Bullet(5, 5, 11, 0, 3, false);

  ILoAGameObject emptyList = new MtLoAGameObject();
  ILoAGameObject list1 = new ConsLoAGameObject(bullet, emptyList);
  ILoAGameObject list2 = new ConsLoAGameObject(bulletHit, list1);
  ILoAGameObject list3 = new ConsLoAGameObject(bulletMove, list2);

  ILoAGameObject list4 = new ConsLoAGameObject(ship, emptyList);
  ILoAGameObject list5 = new ConsLoAGameObject(shipHit, list4);
  ILoAGameObject list6 = new ConsLoAGameObject(ship, list4);
  ILoAGameObject list7 = new ConsLoAGameObject(shipMove, list5);
  ILoAGameObject list8 = new ConsLoAGameObject(shipOutside, list4);
  ILoAGameObject list9 = new ConsLoAGameObject(ship, emptyList);

  Random rand = new Random(0);
  Random rand1 = new Random(1);
  Random rand2 = new Random(2);
  Random rand3 = new Random(3);
  Random rand4 = new Random(4);
  Random rand5 = new Random(5);

  MyGame testWorld = new MyGame(10, rand);
  MyGame testWorld2 = new MyGame(0, 0, 10, list4, list1, rand);

  MyGame testWorld3 = new MyGame(31, 1, 9, list4, list1, rand);

  MyGame testWorld4OnTick = new MyGame(1, 1, 9, new ConsLoAGameObject(new Ship(5, 409, 5, false),
      new ConsLoAGameObject(new Ship(5, 240, 5, false), emptyList)), list1.move(), rand);

  MyGame testWorld6 = new MyGame(0, 0, 10, emptyList, emptyList, rand);

  WorldScene testScene = new WorldScene(500, 500);

  // Tests for the move method on AGameObject
  boolean testMoveAGameObject(Tester t) {
    return t.checkExpect(this.ship.move(), shipMove)
        && t.checkExpect(unmovingShip.move(), unmovingShip)
        && t.checkExpect(bullet.move(), bulletMove);
  }

  // Tests for the hit method on AGameObject
  boolean testHitbullet(Tester t) {
    return t.checkExpect(bullet.hit(), bulletHit) && t.checkExpect(bulletHit.hit(), bulletHit)
        && t.checkExpect(shipHit.hit(), shipHit) && t.checkExpect(this.ship.hit(), this.shipHit);
  }

  // tests for the isOutside method on AGameObject
  boolean testIsOutside(Tester t) {
    return t.checkExpect(bullet.isOutside(), false) && t.checkExpect(ship.isOutside(), false)
        && t.checkExpect(bulletOutside.isOutside(), true)
        && t.checkExpect(shipOutside.isOutside(), true);
  }

  // tests for the collision method on AGameObject
  boolean testCollsion(Tester t) {
    return t.checkExpect(bullet.collision(shipOutside), false)
        && t.checkExpect(bulletMove.collision(ship), true)
        && t.checkExpect(bulletOutside.collision(shipOutside), true)
        && t.checkExpect(shipOutside.collision(bulletMove), false);
  }

  // tests for the draw method on AGameObject
  boolean testDraw(Tester t) {
    return t.checkExpect(bullet.draw(testScene),
        testScene.placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.black), 5, 5))
        && t.checkExpect(ship.draw(testScene),
            testScene.placeImageXY(new CircleImage(20, OutlineMode.SOLID, Color.green), 20, 20));
  }

  // test for the addBulletCollision method on AGameObject
  boolean testAddBulletCollision(Tester t) {
    return t.checkExpect(bullet.addBulletCollision(2, emptyList), emptyList)
        && t.checkExpect(bullet.addBulletCollision(0, emptyList),
            new ConsLoAGameObject(bulletCollision1,
                new ConsLoAGameObject(bulletCollision2, emptyList)))
        && t.checkExpect(ship.addBulletCollision(1, list1), list1);
  }

  // test for the checkRemoveAGameObject method on AGameObject
  boolean testCheckRemoveAGameObject(Tester t) {
    return t.checkExpect(bullet.checkRemoveAGameObject(emptyList), list1)
        && t.checkExpect(ship.checkRemoveAGameObject(list5), list6)
        && t.checkExpect(bulletHit.checkRemoveAGameObject(list1), new ConsLoAGameObject(
            bulletCollision1, new ConsLoAGameObject(bulletCollision2, list1)));
  }

  // Tests for the add method on ILoAGameObject
  boolean testAddAGame(Tester t) {
    return t.checkExpect(emptyList.add(bullet), new ConsLoAGameObject(bullet, emptyList))
        && t.checkExpect(list1.add(ship), new ConsLoAGameObject(ship, list1))
        && t.checkExpect(list3.add(shipMove), new ConsLoAGameObject(shipMove, list3));
  }

  // Tests for the move method on ILoAGameObject
  boolean testMoveILoAGameObject(Tester t) {
    return t.checkExpect(emptyList.move(), emptyList)
        && t.checkExpect(list1.move(), new ConsLoAGameObject(bulletMove, emptyList))
        && t.checkExpect(list4.move(), new ConsLoAGameObject(shipMove, emptyList));
  }

  // Tests for theremoveOutside on ILoAGameObject
  boolean testRemoveOutside(Tester t) {
    return t.checkExpect(emptyList.removeOutside(), emptyList)
        && t.checkExpect(list1.removeOutside(), list1)
        && t.checkExpect(list8.removeOutside(), list4);
  }

  // Tests for the removeCollisions on ILoAGameObject
  boolean testRemoveCollisions(Tester t) {
    return t.checkExpect(emptyList.removeCollisions(), emptyList)
        && t.checkExpect(list1.removeCollisions(), list1)
        && t.checkExpect(list5.removeCollisions(), list4)
        && t.checkExpect(list2.removeCollisions(), new ConsLoAGameObject(bulletCollision1,
            new ConsLoAGameObject(bulletCollision2, list1)));
  }

  // Tests for the draw on ILoAGameObject
  boolean testDrawILoAGameObject(Tester t) {
    return t.checkExpect(emptyList.draw(testScene), testScene)
        && t.checkExpect(list1.draw(testScene),
            testScene.placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.black), 5, 5))
        && t.checkExpect(list4.draw(testScene),
            testScene.placeImageXY(new CircleImage(20, OutlineMode.SOLID, Color.green), 20, 20));
  }

  // Tests for the length method ILoAGameObject
  boolean testListLength(Tester t) {
    return t.checkExpect(list1.length(), 1) && t.checkExpect(list2.length(), 2)
        && t.checkExpect(list3.length(), 3) && t.checkExpect(emptyList.length(), 0);
  }

  // Tests for the checkCollision(ILoAGameObject others) method ILoAGameObject
  boolean testCheckCollision1(Tester t) {
    return t.checkExpect(list1.checkCollision(list4), new ConsLoAGameObject(bulletHit, emptyList))
        && t.checkExpect(list1.checkCollision(emptyList), list1)
        && t.checkExpect(list4.checkCollision(list1), new ConsLoAGameObject(shipHit, emptyList));
  }

  // Tests for the checkCollision(AGameObject other) method ILoAGameObject
  boolean testCheckCollision2(Tester t) {
    return t.checkExpect(list1.checkCollision(ship), true)
        && t.checkExpect(list1.checkCollision(shipOutside), false)
        && t.checkExpect(list4.checkCollision(bullet), true);
  }

  // Test for the makeScene method on MyGame
  boolean testMakeScene(Tester t) {
    return t
        .checkExpect(testWorld2.makeScene(), testScene
            .placeImageXY(new CircleImage(20, OutlineMode.SOLID, Color.green), 20, 20)
            .placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.black), 5, 5)
            .placeImageXY(new TextImage("Bullets on Screen: 1. Number of Ships: 1.", Color.black),
                145, 20)
            .placeImageXY(new TextImage("Bullets left: 10. Ships Destroyed: 0.", Color.black), 130,
                40))
        && t.checkExpect(testWorld.makeScene(), testScene
            .placeImageXY(new TextImage("Game will start shortly.", Color.green), 250, 250));
  }

  // Test for the addWelcomeMessage method on MyGame
  boolean testAddWelcomeMessage(Tester t) {
    return t.checkExpect(testWorld2.addWelcomeMessage(testScene),
        testScene.placeImageXY(new TextImage("Game will start shortly.", Color.green), 250, 250))
        && t.checkExpect(testWorld.addWelcomeMessage(testScene), testScene
            .placeImageXY(new TextImage("Game will start shortly.", Color.green), 250, 250));
  }

  // Test for the drawShip method on MyGame
  boolean testDrawShip(Tester t) {
    return t.checkExpect(testWorld2.drawShips(testScene),
        testScene.placeImageXY(new CircleImage(20, OutlineMode.SOLID, Color.green), 20, 20))
        && t.checkExpect(testWorld.drawShips(testScene), testScene);
  }

  // Test for the drawBullet method on MyGame
  boolean testDrawBullet(Tester t) {
    return t.checkExpect(testWorld2.drawBullets(testScene),
        testScene.placeImageXY(new CircleImage(2, OutlineMode.SOLID, Color.black), 5, 5))
        && t.checkExpect(testWorld.drawBullets(testScene), testScene);
  }

  // Test for the addInfoToScene method on MyGame
  boolean testAddInfoToScene(Tester t) {
    return t
        .checkExpect(testWorld2.addInfoToScene(testScene),
            testScene.placeImageXY(
                new TextImage("Bullets on Screen: 1. Number of Ships: 1.", Color.black), 145, 20)
                .placeImageXY(new TextImage("Bullets left: 10. Ships Destroyed: 0.", Color.black),
                    130, 40))
        && t.checkExpect(testWorld3.addInfoToScene(testScene),
            testScene.placeImageXY(
                new TextImage("Bullets on Screen: 1. Number of Ships: 1.", Color.black), 145, 20)
                .placeImageXY(new TextImage("Bullets left: 9. Ships Destroyed: 1.", Color.black),
                    130, 40));
  }

  // Test for the onTick method on MyGame
  boolean testOnTick(Tester t) {
    return t.checkExpect((new MyGame(0, 0, 10, list4, list1, rand1)).onTick(),
        new MyGame(1, 0, 10, list4.move(), list1.move(), rand1))
        && t.checkExpect((new MyGame(31, 1, 9, emptyList, list1, rand2)).onTick(),
            testWorld4OnTick);
  }

  // Test for the addShips method on MyGame
  boolean testAddShips(Tester t) {
    return t.checkExpect(
        (new MyGame(0, 0, 10, list4, list1, rand3)).addShips(new MtLoAGameObject(), 2),
        new ConsLoAGameObject(new Ship(0, 401, 5, false),
            new ConsLoAGameObject(new Ship(500, 360, -5, false), emptyList)))
        && t.checkExpect((new MyGame(0, 0, 10, list4, list1, rand4)).addShips(list9, 0), list9);
  }

  // Test for the moveGameObjects method of MyGame
  boolean testMoveGameObjects(Tester t) {
    return t.checkExpect(testWorld.moveGameObjects(), testWorld6)
        && t.checkExpect((new MyGame(0, 0, 10, list4, list1, rand5)).moveGameObjects(),
            new MyGame(0, 0, 10, list4.move(), list1.move(), rand));
  }

  // Test for the checkCollisionGameObjects method of MyGame
  boolean testCheckCollisionGameObjects(Tester t) {
    return t.checkExpect((new MyGame(0, 0, 10, list4, list1, rand5)).checkCollisionGameObjects(),
        (new MyGame(0, 0, 10, new ConsLoAGameObject(shipHit, emptyList),
            new ConsLoAGameObject(bulletHit, emptyList), rand5)))
        && t.checkExpect(
            (new MyGame(0, 0, 10, list6, emptyList, rand5)).checkCollisionGameObjects(),
            (new MyGame(0, 0, 10, list6, emptyList)))
        && t.checkExpect((new MyGame(0, 0, 10, list6, list1, rand5)).checkCollisionGameObjects(),
            (new MyGame(0, 0, 10,
                new ConsLoAGameObject(shipHit, new ConsLoAGameObject(shipHit, emptyList)),
                new ConsLoAGameObject(bulletHit, emptyList))));
  }

  // Test for the removeGameObjects method of MyGame
  boolean testRemoveGameObjects(Tester t) {
    return t.checkExpect((new MyGame(0, 0, 10, list4, list1, rand5))
        .removeGameObjects(), new MyGame(0, 0, 10, list4, list1, rand5)) && t
            .checkExpect(
                (new MyGame(0, 0, 10, list4, list1, rand5)).checkCollisionGameObjects()
                    .removeGameObjects(),
                new MyGame(0, 1, 10, emptyList,
                    new ConsLoAGameObject(new Bullet(5, 5, -11, 0, 3, false),
                        new ConsLoAGameObject(new Bullet(5, 5, 11, 0, 3, false), emptyList)),
                    rand5));
  }

  // Test for the onKeyEvent method of MyGame
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect((new MyGame(0, 0, 10, emptyList, emptyList, rand5)).onKeyEvent(" "),
        new MyGame(0, 0, 9, emptyList,
            new ConsLoAGameObject(new Bullet(249, 500, 0, -10, 2, false), emptyList), rand5))
        && t.checkExpect((new MyGame(0, 0, 10, emptyList, list1, rand5)).onKeyEvent(" "),
            new MyGame(0, 0, 9, emptyList,
                new ConsLoAGameObject(new Bullet(249, 500, 0, -10, 2, false), list1), rand5))
        && t.checkExpect(testWorld.onKeyEvent("c"), testWorld);
  }

  // Test for the worldEnds method of MyGame
  boolean testWorldEnds(Tester t) {
    return t.checkExpect((new MyGame(0, 0, 10, emptyList, emptyList, rand5)).worldEnds(),
        new WorldEnd(false,
            testScene.placeImageXY(new TextImage("Game Over", Color.red), 250, 250)))
        && t.checkExpect((new MyGame(0, 0, 0, emptyList, emptyList, rand5)).worldEnds(),
            new WorldEnd(true,
                testScene.placeImageXY(new TextImage("Game Over", Color.red), 250, 250)))
        && t.checkExpect((new MyGame(0, 0, 0, list4, list1, rand5)).worldEnds(), new WorldEnd(false,
            testScene.placeImageXY(new TextImage("Game Over", Color.red), 250, 250)));
  }

  // Test for the makeEndScene method of MyGame
  boolean testMakeEndScene(Tester t) {
    return t.checkExpect(testWorld.makeEndScene(),
        testScene.placeImageXY(new TextImage("Game Over", Color.red), 250, 250))
        && t.checkExpect(testWorld2.makeEndScene(),
            testScene.placeImageXY(new TextImage("Game Over", Color.red), 250, 250));
  }

  // runs game
  boolean testBigBang(Tester t) {
    MyGame world = new MyGame(10); // create a new game with the given number of bullets
    return world.bigBang(500, 500, 1.0 / 30.0);
  }
}
