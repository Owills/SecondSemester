import tester.*;

class BagelRecipe {
  // stores the weight of each ingredient
  double flour;
  double water;
  double yeast;
  double salt;
  double malt;

  // main constructor that takes in each in ingredients and
  // enforces the following constraints:
  // 1. the weight of the flour must be equal to the weight of the water
  // 2. the weight of the yeast should be equal the weight of the malt
  // 3. the weight of the salt + yeast should be 1/20th the weight of the flour
  BagelRecipe(double flour, double water, double yeast, double salt, double malt) {

    this.flour = this.checkEqual(flour, water, "Invalid flour or water, they should be equal");

    this.water = water;

    this.malt = this.checkEqual(malt, yeast, "Invalid yeast or malt, they should be equal");

    this.yeast = yeast;

    this.salt = this.checkEqual(salt, (this.flour / 20) - this.yeast,
        "Invalid salt, yeast or flour, their sum should be equal to 1/20th of flour");

  }

  // constructor takes in the flour and yeasts and produces the correct values
  // for the other ingredients given the following constraints:
  // 1. the weight of the flour must be equal to the weight of the water
  // 2. the weight of the yeast should be equal the weight of the malt
  // 3. the weight of the salt + yeast should be 1/20th the weight of the flour
  BagelRecipe(double flour, double yeast) {
    this.flour = flour;
    this.water = this.flour;
    this.yeast = yeast;
    this.salt = this.flour / 20 - this.yeast;
    this.malt = this.yeast;

  }

  // constructor takes in the volumes of flour, yeasts, salt and converts
  // to the correct weights, whiles produces the correct values
  // for the other ingredients given the following constraints
  // and checks that the constraints are met:
  // 1. the weight of the flour must be equal to the weight of the water
  // 2. the weight of the yeast should be equal the weight of the malt
  // 3. the weight of the salt + yeast should be 1/20th the weight of the flour
  BagelRecipe(double flour, double yeast, double salt) {
    this.flour = flour * 4.25;
    this.water = this.flour;
    this.salt = this.checkEqual((salt / 48) * 10, (this.flour / 20) - (yeast / 48) * 5,
        "Invalid salt, yeast or flour, their sum should be equal to 1/20th of flour");
    this.yeast = (yeast / 48) * 5;
    this.malt = this.yeast;

  }

  // checks if the weights of two ingredients are equal
  // otherwise throws an IllegalArgumentException with the given message
  double checkEqual(double item1, double item2, String msg) {
    double epsilon = 0.000001d;
    if (Math.abs(item1 - item2) < epsilon) {
      return item1;
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }

  // checks if this BagelRecipe is the same and the other BagelRecipe
  // by comparing the values with an error of 0.001
  boolean sameRecipe(BagelRecipe other) {
    double epsilon = 0.001;
    if (Math.abs(this.flour - other.flour) > epsilon) {
      return false;
    }
    if (Math.abs(this.water - other.water) > epsilon) {
      return false;
    }
    if (Math.abs(this.yeast - other.yeast) > epsilon) {
      return false;
    }
    if (Math.abs(this.salt - other.salt) > epsilon) {
      return false;
    }
    return Math.abs(this.malt - other.malt) <= epsilon;
  }
}

class ExamplesBagel {

  BagelRecipe validBagel1 = new BagelRecipe(20.0, 20.0, 0.5, 0.5, 0.5);
  BagelRecipe validBagel12 = new BagelRecipe(34.0, 34.0, 1.0, 0.7, 1.0);

  BagelRecipe validBagel2 = new BagelRecipe(20.0, 0.5);

  BagelRecipe validBagel3 = new BagelRecipe(8.0, 9.6, 3.36);
  BagelRecipe validBagel32 = new BagelRecipe(5.0, 2.0, 4.1);

  // checks that the Constructors don't let non perfect BagelRecipes through
  boolean testCheckConstructorException(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException("Invalid flour or water, they should be equal"), "BagelRecipe",
        50.0, 100.0, 100.0, 100.0, 100.0)
        && t.checkConstructorException(
            new IllegalArgumentException("Invalid yeast or malt, they should be equal"),
            "BagelRecipe", 50.0, 50.0, 100.0, 50.0, 101.0)
        && t.checkConstructorException(
            new IllegalArgumentException(
                "Invalid salt, yeast or flour, their sum should be equal to 1/20th of flour"),
            "BagelRecipe", 50.0, 50.0, 100.0, 50.0, 100.0)
        && t.checkConstructorException(
            new IllegalArgumentException(
                "Invalid salt, yeast or flour, their sum should be equal to 1/20th of flour"),
            "BagelRecipe", 4.0, 0.08, 0.02);
  }

  // checks the sameRecipe method
  boolean testSameRecipe(Tester t) {
    return t.checkExpect(validBagel1.sameRecipe(validBagel2), true)
        && t.checkExpect(validBagel1.sameRecipe(validBagel12), false)
        && t.checkExpect(validBagel1.sameRecipe(validBagel32), false)
        && t.checkExpect(validBagel12.sameRecipe(validBagel3), true);
  }

}