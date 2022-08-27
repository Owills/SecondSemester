import tester.Tester;


//represents and single Shape or Combo that connects on or more pictures
interface IPicture {
  int getWidth();
  
  int countShapes();
  
  int comboDepth();
  
  IPicture mirror();
  
  String pictureRecipe(int depth);
}

//represents a shape where kind is the type of shape such as a "circle" or "square"
// and size represents how big the shape is

class Shape implements IPicture {
  String kind;
  int size;
  
  Shape(String kind, int size) {
    this.kind = kind;
    this.size = size;
  }
  
  /* Template:
   * Fields:
   * kind -- string
   * size -- int
   * Methods on Fields:
   * Arguments:
   */
  // returns the size of the shape
  
  public int getWidth() {
    return this.size;
  }
  
  /* Template:
   * Fields:
   * kind -- string
   * size -- int
   * Methods on Fields:
   * Arguments:
   */
  // returns 1 since Shape always contains exactly 1 shape
  
  public int countShapes() {
    return 1;
  }
  
  /* Template:
   * Fields:
   * kind -- string
   * size -- int
   * Methods on Fields:
   * Arguments:
   */
  // returns 0 since Shape only contains 1 shape
  
  public int comboDepth() {
    return 0;
  }
  
  /* Template:
   * Fields:
   * kind -- string
   * size -- int
   * Methods on Fields:
   * Arguments:
   */
  // returns itself since the individual shape does not get mirrored
  
  public IPicture mirror() {
    return this;
  }
  
  /* Template:
   * Fields:
   * kind -- string
   * size -- int
   * Methods on Fields:
   * Arguments:
   * depth -- int
   */
  // returns the type of shape to be used in the description
  
  public String pictureRecipe(int depth) {
    return this.kind;
  }
}

// represents one or more shapes where name describes the the new picture
// and operation describes how the image is put together
class Combo implements IPicture {
  String name;
  IOperation operation;
  
  Combo(String name, IOperation operation) {
    this.name = name;
    this.operation = operation;
  }
  
  /* Template:
   * Fields:
   * name -- string
   * operation -- IOperation
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IOperation
   * pictureRecipe -- String
   * Arguments:
   */
  // returns the size of the modified shape/s
  
  public int getWidth() {
    return this.operation.getWidth();
  }
  
  /* Template:
   * Fields:
   * name -- string
   * operation -- IOperation
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IOperation
   * pictureRecipe -- String
   * Arguments:
   */
  // returns the number of shape/s in the combination
  
  public int countShapes() {
    return operation.countShapes();
  }
  
  /* Template:
   * Fields:
   * name -- string
   * operation -- IOperation
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IOperation
   * pictureRecipe -- String
   * Arguments:
   */
  // adds 1 to the current combination depth calculation
  
  public int comboDepth() {
    return 1 + operation.comboDepth();
  }
  
  /* Template:
   * Fields:
   * name -- string
   * operation -- IOperation
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IOperation
   * pictureRecipe -- String
   * Arguments:
   */
  // returns a new combo with the same name, but with the operation mirrored
  
  public IPicture mirror() {
    return new Combo(this.name,this.operation.mirror());
  }
  
  /* Template:
   * Fields:
   * name -- string
   * operation -- IOperation
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IOperation
   * pictureRecipe -- String
   * Arguments:
   * depth -- int
   */
  // returns the combo description if the combo depth is zero
  // or the operation picture recipe is greater than zero
  
  public String pictureRecipe(int depth) {
    if (depth <= 0) {
      return this.name;
    }
    return this.operation.pictureRecipe(depth - 1);
  }
}

// represents a change to picture 
interface IOperation {
  int getWidth();
  
  int countShapes();
  
  int comboDepth();
  
  IOperation mirror();
  
  String pictureRecipe(int depth);
}

// represents a picture that is twice as large
class Scale implements IOperation {
  IPicture picture;
  
  Scale(IPicture picture) {
    this.picture = picture;
  }
  
  /* Template:
   * Fields:
   * picture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the width of the scaled picture
  
  public int getWidth() {
    return this.picture.getWidth() * 2;
  }
  
  /* Template:
   * Fields:
   * picture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the number of picture in the scaled picture
  
  public int countShapes() {
    return this.picture.countShapes();
  }
  
  /* Template:
   * Fields:
   * picture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the combination depth in the scaled picture
  
  public int comboDepth() {
    return this.picture.comboDepth();
  }
  
  /* Template:
   * Fields:
   * picture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // creates a new Scale with the picture mirrored
  
  public IOperation mirror() {
    return new Scale(this.picture.mirror());
  }
  
  /* Template:
   * Fields:
   * picture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   * depth -- int
   */
  // returns the picture recipe of the scaled picture
  
  public String pictureRecipe(int depth) {
    return "scale(" + this.picture.pictureRecipe(depth) + ")";
  }
}

//takes two pictures, and draws picture1 to the left of picture2
class Beside implements IOperation {
  IPicture picture1;
  IPicture picture2;
  
  Beside(IPicture picture1, IPicture picture2) {
    this.picture1 = picture1;
    this.picture2 = picture2;
  }
  
  /* Template:
   * Fields:
   * picture1 -- IPicture
   * picture2 -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the total width of the images
  
  public int getWidth() {
    return this.picture1.getWidth() +  this.picture2.getWidth();
  }
  
  /* Template:
   * Fields:
   * picture1 -- IPicture
   * picture2 -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the total number of shapes in this image
  
  public int countShapes() {
    return this.picture1.countShapes() +  this.picture2.countShapes();
  }
  
  /* Template:
   * Fields:
   * picture1 -- IPicture
   * picture2 -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the deepest combination depth of this image
  
  public int comboDepth() {
    int picture1Depth = this.picture1.comboDepth();
    int picture2Depth = this.picture2.comboDepth();
    if (picture1Depth > picture2Depth) {
      return picture1Depth;
    }
    return picture2Depth;
  }
  
  /* Template:
   * Fields:
   * picture1 -- IPicture
   * picture2 -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // creates a new Beside with each picture mirrored and placed in reverse order
  
  public IOperation mirror() {
    return new Beside(this.picture2.mirror(), this.picture1.mirror());
  }
  
  /* Template:
   * Fields:
   * picture1 -- IPicture
   * picture2 -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   * depth - int
   */
  // gets the picture recipe of the image
  
  public String pictureRecipe(int depth) {
    return "beside(" + this.picture1.pictureRecipe(depth) + ", " 
      + this.picture2.pictureRecipe(depth) + ")";
  }

}

//takes two pictures, and draws top-picture on top of bottom-picture, with their centers aligned
class Overlay implements IOperation {
  IPicture topPicture;
  IPicture bottomPicture;
  
  Overlay(IPicture topPicture, IPicture bottomPicture) {
    this.topPicture = topPicture;
    this.bottomPicture = bottomPicture;
  }
  
  /* Template:
   * Fields:
   * topPicture -- IPicture
   * bottomPicture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the total width of the two pictures
  
  public int getWidth() {
    if (this.topPicture.getWidth() > this.bottomPicture.getWidth()) {
      return this.topPicture.getWidth();
    }
    return this.bottomPicture.getWidth();
  }
  /* Template:
   * Fields:
   * topPicture -- IPicture
   * bottomPicture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the total number of shapes of the two pictures
  
  public int countShapes() {
    return this.topPicture.countShapes() +  this.bottomPicture.countShapes();
  }
  
  /* Template:
   * Fields:
   * topPicture -- IPicture
   * bottomPicture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // gets the deepest combination depth of the two pictures
  
  public int comboDepth() {
    int bottomPictureDepth = this.bottomPicture.comboDepth();
    int topPictureDepth = this.topPicture.comboDepth();
    if (bottomPictureDepth > topPictureDepth) { 
      return bottomPictureDepth;
    }  
    return topPictureDepth;
  }
  
  /* Template:
   * Fields:
   * topPicture -- IPicture
   * bottomPicture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   */
  // creates a new Overlay with both pictures mirrored
  
  public IOperation mirror() {
    return new Overlay(this.topPicture.mirror(), this.bottomPicture.mirror());
  }
  
  /* Template:
   * Fields:
   * topPicture -- IPicture
   * bottomPicture -- IPicture
   * Methods on Fields:
   * getWidth -- int
   * countShapes -- int
   * comboDepth -- int
   * mirror -- IPicture
   * pictureRecipe -- String
   * Arguments:
   * depth -- int
   */
  // gets the picture recipe of the overlayed image
  
  public String pictureRecipe(int depth) { 
    return "overlay" + "(" + this.topPicture.pictureRecipe(depth) + ", "
        + this.bottomPicture.pictureRecipe(depth) + ")";

  }
}


class ExamplesPicture {
  IPicture circle = new Shape("circle", 20);
  IPicture square = new Shape("square", 30);
  IPicture bigCircle = new Combo("big circle", new Scale(circle));
  IPicture squareOnCircle = new Combo("square on circle", new Overlay(square, bigCircle));
  IPicture doubledSquareOnCircle = new Combo("doubled square on circle", 
      new Beside(squareOnCircle, squareOnCircle));
  
  IPicture circleThenSquare = new Combo("circle then square", new Beside(circle, square));
  
  boolean testcomboDepth1(Tester t) {
    return t.checkExpect(this.circle.comboDepth(), 0);
  }
  
  boolean testcomboDepth2(Tester t) {
    return t.checkExpect(this.squareOnCircle.comboDepth(), 2);
  }

  boolean testcomboDepth3(Tester t) {
    return t.checkExpect(this.doubledSquareOnCircle.comboDepth(), 3);
  }

  boolean testpictureRecipe(Tester t) {
    return t.checkExpect(this.squareOnCircle.pictureRecipe(0), "square on circle");
  }

  boolean testpictureRecipe2(Tester t) {
    return t.checkExpect(this.squareOnCircle.pictureRecipe(1), "overlay(square, big circle)");
  }

  boolean testpictureRecipe3(Tester t) {
    return t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(0), "doubled square on circle");
  }

  boolean testpictureRecipe4(Tester t) {
    return t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(2),
        "beside(overlay(square, big circle), overlay(square, big circle))");
  }

  boolean testpictureRecipe5(Tester t) {
    return t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(3),
        "beside(overlay(square, scale(circle)), overlay(square, scale(circle)))");
  }      
}
