import tester.Tester;

import javalib.worldcanvas.*;
import javalib.worldimages.*; // images, like RectangleImage or OverlayImages
import javalib.funworld.*; // the abstract World class and the big-bang library
import java.awt.Color;

interface ITree {

  // draws the leaf, branches and stem of a tree
  WorldImage draw();

  // computes whether any of the twigs in the tree
  // (either stems or branches) are pointing downward rather than upward
  boolean isDrooping();

  // combines the left and right branches with stem together to form a combined
  // tree
  ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
      ITree otherTree);

  // changes the angles for rotation of the tree
  ITree changeAngles(double thetaChange);

  // computes the width of the tree
  double getWidth();

  // computes the maximum width of the right branch of the tree
  double getMaxRight(double max);

  // computes the maximum width of the left branch of the tree
  double getMaxLeft(double max);

}

// to represent the leaf of a tree
class Leaf implements ITree {
  int size; // represents the radius of the leaf
  Color color; // the color to draw it

  Leaf(int size, Color color) {
    this.size = size;
    this.color = color;
  }

  /*
   * Template: Fields: size -- integer color -- Color Methods on Fields: draw --
   * WorldImage isDrooping -- boolean combine -- ITree changeAngles -- ITree
   * getWidth -- double getMaxRight -- double getMaxLeft -- double Arguments:
   */

  // draws the leaf of a tree
  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, this.color);
  }

  /*
   * Template: Fields: size -- integer color -- Color Methods on Fields: draw --
   * WorldImage isDrooping -- boolean combine -- ITree changeAngles -- ITree
   * getWidth -- double getMaxRight -- double getMaxLeft -- double Arguments:
   */
  // computes whether any of the twigs in the tree (either stems or branches) are
  // pointing downward rather than upward
  public boolean isDrooping() {
    return false;
  }

  /*
   * Template: Fields: size -- integer color -- Color Methods on Fields: draw --
   * WorldImage isDrooping -- boolean combine -- ITree changeAngles -- ITree
   * getWidth -- double getMaxRight -- double getMaxLeft -- double Arguments:
   */
  // combines the left and right branches with stem together to form a combined
  // tree
  public ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
      ITree otherTree) {
    return new Branch(leftLength, rightLength, leftTheta, rightTheta,
        this.changeAngles(leftTheta - 90), otherTree.changeAngles(rightTheta - 90));
  }

  /*
   * Template: Fields: size -- integer color -- Color Methods on Fields: draw --
   * WorldImage isDrooping -- boolean combine -- ITree changeAngles -- ITree
   * getWidth -- double getMaxRight -- double getMaxLeft -- double Arguments:
   */
  // changes the angles for rotation of the tree
  public ITree changeAngles(double thetaChange) {
    return this;
  }

  /*
   * Template: Fields: size -- integer color -- Color Methods on Fields: draw --
   * WorldImage isDrooping -- boolean combine -- ITree changeAngles -- ITree
   * getWidth -- double getMaxRight -- double getMaxLeft -- double Arguments:
   */
  // computes the width of the leaf
  public double getWidth() {
    return 2 * this.size;
  }

  /*
   * Template: Fields: size -- integer color -- Color Methods on Fields: draw --
   * WorldImage isDrooping -- boolean combine -- ITree changeAngles -- ITree
   * getWidth -- double getMaxRight -- double getMaxLeft -- double Arguments:
   */
  // computes the maximum width of the right leaf of the tree
  public double getMaxRight(double max) {
    return max + this.size;
  }

  /*
   * Template: Fields: size -- integer color -- Color Methods on Fields: draw --
   * WorldImage isDrooping -- boolean combine -- ITree changeAngles -- ITree
   * getWidth -- double getMaxRight -- double getMaxLeft -- double Arguments:
   */

  // computes the maximum width of the left leaf of the tree
  public double getMaxLeft(double max) {
    return max - this.size;
  }

}

// represents the stem of the tree
class Stem implements ITree {
  // How long this stick is
  int length;
  // The angle (in degrees) of this stem, relative to the +x axis
  double theta;
  // The rest of the tree
  ITree tree;

  Stem(int length, double theta, ITree tree) {
    this.length = length;
    this.theta = theta;
    this.tree = tree;
  }

  /*
   * Template: Fields: length -- integer theta -- double tree -- ITree Methods on
   * Fields: draw -- WorldImage isDrooping -- boolean combine -- ITree
   * changeAngles -- ITree getWidth -- double getMaxRight -- double getMaxLeft --
   * double Arguments:
   */

  // draws the stem of the tree
  public WorldImage draw() {
    double dx = this.length * Math.cos(Math.toRadians(this.theta));
    double dy = this.length * -Math.sin(Math.toRadians(this.theta));
    Posn position = new Posn((int) dx, (int) dy);
    WorldImage lineImage1 = (new LineImage(position, Color.gray)).movePinhole(dx, dy / 2);
    return new OverlayImage(this.tree.draw(), lineImage1);
  }

  /*
   * Template: Fields: length -- integer theta -- double tree -- ITree Methods on
   * Fields: draw -- WorldImage isDrooping -- boolean combine -- ITree
   * changeAngles -- ITree getWidth -- double getMaxRight -- double getMaxLeft --
   * double Arguments:
   */

  // computes whether any of the twigs in the tree (either stems or branches) are
  // pointing downward rather than upward
  public boolean isDrooping() {
    if (this.length * -Math.sin(Math.toRadians(this.theta)) > 0) {
      return true;
    }
    return tree.isDrooping();
  }

  /*
   * Template: Fields: length -- integer theta -- double tree -- ITree Methods on
   * Fields: draw -- WorldImage isDrooping -- boolean combine -- ITree
   * changeAngles -- ITree getWidth -- double getMaxRight -- double getMaxLeft --
   * double Arguments:
   */

  // combines the left and right branches with stem together to form a combined
  // tree
  public ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
      ITree otherTree) {
    return new Branch(leftLength, rightLength, leftTheta, rightTheta,
        this.changeAngles(leftTheta - 90), otherTree.changeAngles(rightTheta - 90));
  }

  /*
   * Template: Fields: length -- integer theta -- double tree -- ITree Methods on
   * Fields: draw -- WorldImage isDrooping -- boolean combine -- ITree
   * changeAngles -- ITree getWidth -- double getMaxRight -- double getMaxLeft --
   * double Arguments:
   */

  // changes the angles for rotation of the tree
  public ITree changeAngles(double thetaChange) {
    return new Stem(this.length, this.theta + thetaChange, tree.changeAngles(thetaChange));
  }

  /*
   * Template: Fields: length -- integer theta -- double tree -- ITree Methods on
   * Fields: draw -- WorldImage isDrooping -- boolean combine -- ITree
   * changeAngles -- ITree getWidth -- double getMaxRight -- double getMaxLeft --
   * double Arguments:
   */

  // computes the width of the tree
  public double getWidth() {
    return this.getMaxRight(0) - this.getMaxLeft(0);
  }

  /*
   * Template: Fields: length -- integer theta -- double tree -- ITree Methods on
   * Fields: draw -- WorldImage isDrooping -- boolean combine -- ITree
   * changeAngles -- ITree getWidth -- double getMaxRight -- double getMaxLeft --
   * double Arguments:
   */

  // computes the maximum width of the right sten of the tree
  public double getMaxRight(double max) {
    double stemLean = this.length * Math.cos(Math.toRadians(this.theta));
    double rightMax = this.tree.getMaxRight(max + stemLean);
    if (max > rightMax) {
      return max;
    }
    return rightMax;
  }

  /*
   * Template: Fields: length -- integer theta -- double tree -- ITree Methods on
   * Fields: draw -- WorldImage isDrooping -- boolean combine -- ITree
   * changeAngles -- ITree getWidth -- double getMaxRight -- double getMaxLeft --
   * double Arguments:
   */

  // computes the maximum width of the left stem of the tree
  public double getMaxLeft(double max) {
    double stemLean = this.length * Math.cos(Math.toRadians(this.theta));
    double leftMax = this.tree.getMaxLeft(max + stemLean);
    if (max < leftMax) {
      return max;
    }
    return leftMax;
  }

}

// represents the branch of a tree
class Branch implements ITree {
  // How long the left and right branches are
  int leftLength;
  int rightLength;
  // The angle (in degrees) of the two branches, relative to the +x axis,
  double leftTheta;
  double rightTheta;
  // The remaining parts of the tree
  ITree left;
  ITree right;

  Branch(int leftLength, int rightLength, double leftTheta, double rightTheta, ITree left,
      ITree right) {
    this.leftLength = leftLength;
    this.rightLength = rightLength;
    this.leftTheta = leftTheta;
    this.rightTheta = rightTheta;
    this.left = left;
    this.right = right;
  }

  /*
   * Template: Fields: leftLength -- integer rightLength -- integer leftTheta --
   * double rightTheta -- double left -- ITree right -- ITree Methods on Fields:
   * draw -- WorldImage isDrooping -- boolean combine -- ITree changeAngles --
   * ITree getWidth -- double getMaxRight -- double getMaxLeft -- double
   * Arguments:
   */

  // draws the branch of a tree
  public WorldImage draw() {
    double leftDX = this.leftLength * Math.cos(Math.toRadians(this.leftTheta));
    double leftDY = this.leftLength * -Math.sin(Math.toRadians(this.leftTheta));

    double rightDX = this.rightLength * Math.cos(Math.toRadians(this.rightTheta));
    double rightDY = this.rightLength * -Math.sin(Math.toRadians(this.rightTheta));

    Posn leftPosition = new Posn((int) leftDX, (int) leftDY);
    Posn rightPosition = new Posn((int) rightDX, (int) rightDY);

    WorldImage lineImage1 = (new LineImage(leftPosition, Color.gray)).movePinhole((-leftDX / 2),
        (-leftDY / 2));

    WorldImage lineImage2 = (new LineImage(rightPosition, Color.gray)).movePinhole(-rightDX / 2,
        -rightDY / 2);

    WorldImage combineLines = (new OverlayOffsetAlign(AlignModeX.PINHOLE, AlignModeY.PINHOLE,
        lineImage1, 0, 0, lineImage2));

    WorldImage addRightTree = new OverlayImage(this.right.draw(),
        combineLines.movePinhole(rightDX, rightDY));

    WorldImage addLeftTree = new OverlayImage(this.left.draw(),
        addRightTree.movePinhole(leftDX - rightDX, leftDY - rightDY));

    return addLeftTree.movePinhole(-leftDX, -leftDY);
  }

  /*
   * Template: Fields: leftLength -- integer rightLength -- integer leftTheta --
   * double rightTheta -- double left -- ITree right -- ITree Methods on Fields:
   * draw -- WorldImage isDrooping -- boolean combine -- ITree changeAngles --
   * ITree getWidth -- double getMaxRight -- double getMaxLeft -- double
   * Arguments:
   */
  // computes whether any of the twigs in the tree (either stems or branches) are
  // pointing downward rather than upward
  public boolean isDrooping() {
    if (this.leftLength * -Math.sin(Math.toRadians(this.leftTheta)) > 0
        || this.rightLength * -Math.sin(Math.toRadians(this.rightTheta)) > 0) {
      return true;
    }
    return this.left.isDrooping() || this.right.isDrooping();
  }

  /*
   * Template: Fields: leftLength -- integer rightLength -- integer leftTheta --
   * double rightTheta -- double left -- ITree right -- ITree Methods on Fields:
   * draw -- WorldImage isDrooping -- boolean combine -- ITree changeAngles --
   * ITree getWidth -- double getMaxRight -- double getMaxLeft -- double
   * Arguments:
   */
  // combines the left and right branches with stem together to form a combined
  // tree
  public ITree combine(int leftLength, int rightLength, double leftTheta, double rightTheta,
      ITree otherTree) {
    ITree test = new Branch(leftLength, rightLength, leftTheta, rightTheta,
        this.changeAngles(leftTheta - 90), otherTree.changeAngles(rightTheta - 90));
    return test;
  }

  /*
   * Template: Fields: leftLength -- integer rightLength -- integer leftTheta --
   * double rightTheta -- double left -- ITree right -- ITree Methods on Fields:
   * draw -- WorldImage isDrooping -- boolean combine -- ITree changeAngles --
   * ITree getWidth -- double getMaxRight -- double getMaxLeft -- double
   * Arguments:
   */
  // changes the angles for rotation of the tree
  public ITree changeAngles(double thetaChange) {
    return new Branch(this.leftLength, this.rightLength, leftTheta + thetaChange,
        rightTheta + thetaChange, this.left.changeAngles(thetaChange),
        this.right.changeAngles(thetaChange));
  }

  /*
   * Template: Fields: leftLength -- integer rightLength -- integer leftTheta --
   * double rightTheta -- double left -- ITree right -- ITree Methods on Fields:
   * draw -- WorldImage isDrooping -- boolean combine -- ITree changeAngles --
   * ITree getWidth -- double getMaxRight -- double getMaxLeft -- double
   * Arguments:
   */
  // computes the width of the tree
  public double getWidth() {
    double maxRight = this.getMaxRight(0);
    double maxLeft = this.getMaxLeft(0);
    return maxRight - maxLeft;

  }

  /*
   * Template: Fields: leftLength -- integer rightLength -- integer leftTheta --
   * double rightTheta -- double left -- ITree right -- ITree Methods on Fields:
   * draw -- WorldImage isDrooping -- boolean combine -- ITree changeAngles --
   * ITree getWidth -- double getMaxRight -- double getMaxLeft -- double
   * Arguments:
   */
  // computes the maximum width of the right branch of the tree
  public double getMaxRight(double max) {
    double rightBranchX = this.rightLength * Math.cos(Math.toRadians(this.rightTheta));
    double leftBranchX = this.leftLength * Math.cos(Math.toRadians(this.leftTheta));

    double rightMax = this.right.getMaxRight(max + rightBranchX);
    double leftMax = this.left.getMaxRight(max + leftBranchX);

    if (max > rightMax && max > leftMax) {
      return max;
    }
    if (rightMax > max && rightMax >= leftMax) {
      return rightMax;
    }

    return leftMax;
  }

  /*
   * Template: Fields: leftLength -- integer rightLength -- integer leftTheta --
   * double rightTheta -- double left -- ITree right -- ITree Methods on Fields:
   * draw -- WorldImage isDrooping -- boolean combine -- ITree changeAngles --
   * ITree getWidth -- double getMaxRight -- double getMaxLeft -- double
   * Arguments:
   */
  // computes the maximum width of the left branch of the tree
  public double getMaxLeft(double max) {
    double rightBranchX = this.rightLength * Math.cos(Math.toRadians(this.rightTheta));
    double leftBranchX = this.leftLength * Math.cos(Math.toRadians(this.leftTheta));

    double rightMax = this.right.getMaxLeft(max + rightBranchX);
    double leftMax = this.left.getMaxLeft(max + leftBranchX);
    if (max < rightMax && max < leftMax) {
      return max;
    }
    if (rightMax < max && rightMax <= leftMax) {
      return rightMax;
    }
    if (leftMax < max && leftMax <= rightMax) {
      return leftMax;
    }
    return max + leftBranchX;
  }
}

// representing examples for the tree
class ExamplesTrees {
  ITree tree1 = new Branch(30, 30, 135, 40, new Leaf(10, Color.RED), new Leaf(15, Color.BLUE));
  ITree tree2 = new Branch(30, 30, 115, 65, new Leaf(15, Color.GREEN), new Leaf(8, Color.ORANGE));

  ITree drooping = new Branch(30, 30, 115, -65, new Leaf(15, Color.GREEN),
      new Leaf(8, Color.ORANGE));
  ITree leftLeaningBranch = new Branch(100, 100, 180, 120, new Leaf(8, Color.GREEN),
      new Leaf(8, Color.ORANGE));
  ITree rightLeaningBranch = new Branch(30, 30, 60, 0, new Leaf(8, Color.GREEN),
      new Leaf(8, Color.ORANGE));

  ITree crisscrossingTree = new Branch(30, 30, 120, 50, tree1, leftLeaningBranch);

  ITree stem1 = new Stem(40, 90, tree1);
  ITree stem2 = new Stem(50, 90, tree2);

  ITree combine1 = tree1.combine(40, 50, 150, 30, tree2);
  ITree combine2 = combine1.combine(40, 50, 150, 30, tree1);

  // test for displaying the tree
  void testDrawTree(WorldImage i) {
    WorldCanvas c = new WorldCanvas(500, 500);
    WorldScene s = new WorldScene(500, 500);
    WorldScene full = s.placeImageXY(new VisiblePinholeImage(i), 250, 250);
    c.drawScene(full);
    c.show();
  }

  // test for drawing the tree
  boolean testTree(Tester t) {
    testDrawTree(crisscrossingTree.draw());
    // testDrawTree(tree2.draw());
    return true;
  }

  // test for whether any of the twigs in the tree (either stems or branches) are
  // pointing downward rather than upward
  boolean testIsDrooping(Tester t) {
    return t.checkExpect(tree1.isDrooping(), false) && t.checkExpect(drooping.isDrooping(), true);
  }

  // test for getting width of the tree
  boolean testGetwidth(Tester t) {
    return t.checkInexact(leftLeaningBranch.getWidth(), 108.0, 0.1)
        && t.checkInexact(crisscrossingTree.getWidth(), 103.717, 0.1)
        && t.checkInexact(rightLeaningBranch.getWidth(), 38.0, 0.1)
        && t.checkInexact(stem1.getWidth(), 69.19, 0.01)
        && t.checkInexact(tree1.getWidth(), 69.19, 0.01);

  }

  // test for getting the left maximum width of the tree
  boolean testmaxleft(Tester t) {
    return t.checkInexact(tree1.getMaxLeft(2), -31.21, 0.01);
  }

  // test for getting the right maximum width of the tree
  boolean testmaxright(Tester t) {
    return t.checkInexact(tree1.getMaxRight(2), 39.98, 0.01);
  }

}