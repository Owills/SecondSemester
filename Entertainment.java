import tester.*;

interface IEntertainment {

  // compute the total price of this IEntertainment
  double totalPrice();

  // computes the minutes of entertainment of this IEntertainment
  public int duration();

  // produce a String that shows the name and price of this IEntertainment
  public String format();

  // is this IEntertainment the same as that one?
  public abstract boolean sameEntertainment(IEntertainment that);

  // is this Magazine the same as that one?
  public boolean sameMagazine(Magazine that);

  // is this TVseries the same as that one?
  public boolean sameTVSeries(TVSeries that);

  // is this Podcast the same as that one?
  public boolean samePodcast(Podcast that);
}

abstract class AEntertainment {
  String name;
  double price;
  int installments;

  AEntertainment(String name, double price, int installments) {
    this.name = name;
    this.price = price; // price per episode of issues
    this.installments = installments; // number of issues or episodes
  }

  // compute the total price of this AEntertainment
  public double totalPrice() {
    return this.price * this.installments;
  }

  // computes the minutes of entertainment of this AEntertainment
  public int duration() {
    return this.installments * 50;
  }

  // produce a String that shows the name and price of this AEntertainment
  public String format() {
    return this.name + ", " + String.valueOf(this.price) + ".";
  }

  // is this AEntertainment the same as that IEntertainment?
  public abstract boolean sameEntertainment(IEntertainment that);

  // is this Magazine the same as that one?
  public boolean sameMagazine(Magazine that) {
    return false;
  }

  // is this TVseries the same as that one?
  public boolean sameTVSeries(TVSeries that) {
    return false;
  }

  // is this Podcast the same as that one?
  public boolean samePodcast(Podcast that) {
    return false;
  }
}

class Magazine extends AEntertainment implements IEntertainment {
  String genre;
  int pages;

  Magazine(String name, double price, String genre, int pages, int installments) {
    super(name, price, installments);
    this.genre = genre;
    this.pages = pages;
  }

  // computes the minutes of entertainment of this Magazine, (includes all
  // installments)
  @Override
  public int duration() {
    return this.installments * this.pages * 5;
  }

  /*
   * Template: Fields: ... this.name ... -- String ... this.price ... -- double
   * ... this.installments ... -- int ... this.genre ... -- String ... this.pages
   * ... -- int Methods: ... this.totalPrice() ... -- double ... this.duration()
   * ... -- int ... this.format() ... -- String ...
   * this.sameEntertainment(AEntertainment that) ... -- boolean ...
   * thissameMagazine(Magazine that) ... -- boolean ... this.sameTVSeries(TVSeries
   * that) ... -- boolean ... samePodcast(Podcast that) ... -- boolean Methods for
   * fields: Arguments: ... that ... -- IEntertainment
   */
  // is this Magazine the same as that IEntertainment?
  public boolean sameEntertainment(IEntertainment that) {
    return that.sameMagazine(this);
  }

  /*
   * Template: Fields: ... this.name ... -- String ... this.price ... -- double
   * ... this.installments ... -- int ... this.genre ... -- String ... this.pages
   * ... -- int Methods: ... this.totalPrice() ... -- double ... this.duration()
   * ... -- int ... this.format() ... -- String ...
   * this.sameEntertainment(AEntertainment that) ... -- boolean ...
   * thissameMagazine(Magazine that) ... -- boolean ... this.sameTVSeries(TVSeries
   * that) ... -- boolean ... samePodcast(Podcast that) ... -- boolean Methods for
   * fields: Arguments: ... that ... -- Magazine
   */
  // is this Magazine the same as that one?
  @Override
  public boolean sameMagazine(Magazine that) {
    if (!this.name.equals(that.name)) {
      return false;
    }
    if (this.price != that.price) {
      return false;
    }
    if (!this.genre.equals(that.genre)) {
      return false;
    }
    if (this.pages != that.pages) {
      return false;
    }
    return this.installments == that.installments;
  }
}

class TVSeries extends AEntertainment implements IEntertainment {
  String corporation;

  TVSeries(String name, double price, int installments, String corporation) {
    super(name, price, installments);
    this.corporation = corporation;
  }

  /*
   * Template: Fields: ... this.name ... -- String ... this.price ... -- double
   * ... this.installments ... -- int ... this.genre ... -- String ... this.pages
   * ... -- int Methods: ... this.totalPrice() ... -- double ... this.duration()
   * ... -- int ... this.format() ... -- String ...
   * this.sameEntertainment(AEntertainment that) ... -- boolean ...
   * thissameMagazine(Magazine that) ... -- boolean ... this.sameTVSeries(TVSeries
   * that) ... -- boolean ... samePodcast(Podcast that) ... -- boolean Methods for
   * fields: Arguments: ... that ... -- IEntertainment
   */
  // is this TVSeries the same as that IEntertainment?
  public boolean sameEntertainment(IEntertainment that) {
    return that.sameTVSeries(this);
  }

  /*
   * Template: Fields: ... this.name ... -- String ... this.price ... -- double
   * ... this.installments ... -- int ... this.genre ... -- String ... this.pages
   * ... -- int Methods: ... this.totalPrice() ... -- double ... this.duration()
   * ... -- int ... this.format() ... -- String ...
   * this.sameEntertainment(AEntertainment that) ... -- boolean ...
   * thissameMagazine(Magazine that) ... -- boolean ... this.sameTVSeries(TVSeries
   * that) ... -- boolean ... samePodcast(Podcast that) ... -- boolean Methods for
   * fields: Arguments: ... that ... -- TVSeries
   */
  // is this TVSeries the same as that one?
  @Override
  public boolean sameTVSeries(TVSeries that) {
    if (!this.name.equals(that.name)) {
      return false;
    }
    if (this.price != that.price) {
      return false;
    }
    if (this.installments != that.installments) {
      return false;
    }
    return this.corporation.equals(that.corporation);
  }
}

class Podcast extends AEntertainment implements IEntertainment {

  Podcast(String name, double price, int installments) {
    super(name, price, installments);
  }

  /*
   * Template: Fields: ... this.name ... -- String ... this.price ... -- double
   * ... this.installments ... -- int ... this.genre ... -- String ... this.pages
   * ... -- int Methods: ... this.totalPrice() ... -- double ... this.duration()
   * ... -- int ... this.format() ... -- String ...
   * this.sameEntertainment(AEntertainment that) ... -- boolean ...
   * thissameMagazine(Magazine that) ... -- boolean ... this.sameTVSeries(TVSeries
   * that) ... -- boolean ... samePodcast(Podcast that) ... -- boolean Methods for
   * fields: Arguments: ... that ... -- IEntertainment
   */
  // is this Podcast the same as that IEntertainment?
  public boolean sameEntertainment(IEntertainment that) {
    return that.samePodcast(this);
  }

  /*
   * Template: Fields: ... this.name ... -- String ... this.price ... -- double
   * ... this.installments ... -- int ... this.genre ... -- String ... this.pages
   * ... -- int Methods: ... this.totalPrice() ... -- double ... this.duration()
   * ... -- int ... this.format() ... -- String ...
   * this.sameEntertainment(AEntertainment that) ... -- boolean ...
   * thissameMagazine(Magazine that) ... -- boolean ... this.sameTVSeries(TVSeries
   * that) ... -- boolean ... samePodcast(Podcast that) ... -- boolean Methods for
   * fields: Arguments: ... that ... -- Podcast
   */
  // is this Podcast the same as that one?
  @Override
  public boolean samePodcast(Podcast that) {
    if (!this.name.equals(that.name)) {
      return false;
    }
    if (this.price != that.price) {
      return false;
    }
    return this.installments == that.installments;
  }

}

class ExamplesEntertainment {
  IEntertainment rollingStone = new Magazine("Rolling Stone", 2.55, "Music", 60, 12);
  IEntertainment houseOfCards = new TVSeries("House of Cards", 5.25, 13, "Netflix");
  IEntertainment serial = new Podcast("Serial", 0.0, 8);

  IEntertainment vanityFair = new Magazine("Vanity Fair", 3.55, "Fashion", 100, 365);
  IEntertainment breakingBad = new TVSeries("Breaking Bad", 8.25, 40, "Netflix");
  IEntertainment history = new Podcast("History Podcast", 0.0, 80000);

  // testing total price method
  boolean testTotalPrice(Tester t) {
    return t.checkInexact(this.rollingStone.totalPrice(), 2.55 * 12, .0001)
        && t.checkInexact(this.houseOfCards.totalPrice(), 5.25 * 13, .0001)
        && t.checkInexact(this.serial.totalPrice(), 0.0, .0001)
        && t.checkInexact(this.vanityFair.totalPrice(), 3.55 * 365, .0001)
        && t.checkInexact(this.breakingBad.totalPrice(), 8.25 * 40, .0001)
        && t.checkInexact(this.history.totalPrice(), 0.0, .0001);
  }

  // testing duration method
  boolean testDuration(Tester t) {
    return t.checkExpect(this.rollingStone.duration(), 60 * 12 * 5)
        && t.checkExpect(this.houseOfCards.duration(), 13 * 50)
        && t.checkExpect(this.serial.duration(), 8 * 50)
        && t.checkExpect(this.vanityFair.duration(), 100 * 365 * 5)
        && t.checkExpect(this.breakingBad.duration(), 40 * 50)
        && t.checkExpect(this.history.duration(), 80000 * 50);
  }

  // testing format method
  boolean testFormat(Tester t) {
    return t.checkExpect(this.rollingStone.format(), "Rolling Stone, 2.55.")
        && t.checkExpect(this.houseOfCards.format(), "House of Cards, 5.25.")
        && t.checkExpect(this.serial.format(), "Serial, 0.0.")
        && t.checkExpect(this.vanityFair.format(), "Vanity Fair, 3.55.")
        && t.checkExpect(this.breakingBad.format(), "Breaking Bad, 8.25.")
        && t.checkExpect(this.history.format(), "History Podcast, 0.0.");
  }

  // testing sameEntertainment method
  boolean testSameEntertainment(Tester t) {
    return t.checkExpect(this.rollingStone.sameEntertainment(this.rollingStone), true)
        && t.checkExpect(this.houseOfCards.sameEntertainment(this.houseOfCards), true)
        && t.checkExpect(this.serial.sameEntertainment(this.serial), true)
        && t.checkExpect(this.vanityFair.sameEntertainment(this.rollingStone), false)
        && t.checkExpect(this.breakingBad.sameEntertainment(this.houseOfCards), false)
        && t.checkExpect(this.history.sameEntertainment(this.serial), false)
        && t.checkExpect(this.vanityFair.sameEntertainment(this.houseOfCards), false)
        && t.checkExpect(this.breakingBad.sameEntertainment(this.rollingStone), false)
        && t.checkExpect(this.history.sameEntertainment(this.houseOfCards), false);
  }

}