import tester.Tester;

// a IMotif represents the type of embroidery stitch pattern and can be one of
// CrossSwitchMotif, ChainStichMotif or Group Motif
interface IMotif {
  double getDifficulty();
  
  double getNumberOfMotifs(); 
  
  String getInfo();
}

// represents a stitch pattern where description represents what is looks like
// and difficulty is a number from 0 to 5 representing how difficult it is to make
class CrossStitchMotif implements IMotif {
  String description;
  double difficulty;

  CrossStitchMotif(String description, double difficulty) {
    this.description = description;
    this.difficulty = difficulty;
  }
  /* Template:
   * Fields:
   * description -- String
   * difficulty -- double
   * Methods on Fields:
   * Arguments:
   */
  //returns the difficulty of the stitch
  
  public double getDifficulty() {
    return this.difficulty;
  }
  /* Template:
   * Fields:
   * description -- String
   * difficulty -- double
   * Methods on Fields:
   * Arguments:
   */
  // returns the number of motifs in the stitch, which for a cross stitch is always one
  
  public double getNumberOfMotifs() {
    return 1.0;
  }
  /* Template:
   * Fields:
   * description -- String
   * difficulty -- double
   * Methods on Fields:
   * Arguments:
   */
  //returns of string of basic info about the stitch, the description and type of stitch
  
  public String getInfo() {
    
    return description + " (cross stitch)";
  }
}

//represents a stitch pattern where description represents what is looks like
//and difficulty is a number from 0 to 5 representing how difficult it is to make
class ChainStitchMotif implements IMotif {
  String description;
  double difficulty;
  
  ChainStitchMotif(String description, double difficulty) {
    this.description = description;
    this.difficulty = difficulty;
  }
  
  /* Template:
   * Fields:
   * description -- String
   * difficulty -- double
   * Methods on Fields:
   * Arguments:
   */
  // returns the difficulty of the stitch
  
  public double getDifficulty() {
    return this.difficulty;
  }
  
  /* Template:
   * Fields:
   * description -- String
   * difficulty -- double
   * Methods on Fields:
   * Arguments:
   */
  // returns the number of motifs in the stitch, which for a chain stitch is always one
  
  public double getNumberOfMotifs() {
    return 1.0;
  }
  
  /* Template:
   * Fields:
   * description -- String
   * difficulty -- double
   * Methods on Fields:
   * Arguments:
   */
  // returns of string of basic info about the stitch, the description and type of stitch
  
  public String getInfo() {
    return description + " (chain stitch)";
  }
}

//represents a group of stitch patterns where description represents what is looks like
// and motifs is a list of the all the IMotif require to make this GroupMotif
class GroupMotif implements IMotif {
  String description;
  ILoMotif motifs;
  
  GroupMotif(String description, ILoMotif motifs) {
    this.description = description;
    this.motifs = motifs;
  }
  /* Template:
   * Fields:
   * description -- String
   * motifs -- ILoMotif
   * Methods on Fields:
   * getTotalDiffiuclty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  //get the difficulty of each stitch in the list of motifs and adds them together
  
  public double getDifficulty() {
    return motifs.getTotalDifficulty();
  }
  /* Template:
   * Fields:
   * description -- String
   * motifs -- ILoMotif
   * Methods on Fields:
   * getTotalDiffiuclty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  // get the total number of motifs in the lists of motifs
  
  public double getNumberOfMotifs() {
    return motifs.getNumberOfMotifs();
  }
  /* Template:
   * Fields:
   * description -- String
   * motifs -- ILoMotif
   * Methods on Fields:
   * getTotalDiffiuclty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  // gets the basic info about each motif in the list of motifs
  
  public String getInfo() {
    return motifs.getInfo();
  }
}

// represents a list of motifs
interface ILoMotif {
  double getTotalDifficulty();
  
  double getNumberOfMotifs();
  
  String getInfo();
}

// represents and non empty list of motifs, where first is the first motif in the list,
// and rest represents the rest of the list
class ConsLoMotif implements ILoMotif {
  IMotif first;
  ILoMotif rest;
  
  ConsLoMotif(IMotif first, ILoMotif rest) {
    this.first = first;
    this.rest = rest;
  }
  /* Template:
   * Fields:
   * first -- IMotif
   * rest -- ILoMotif
   * Methods on Fields:
   * getDifficulty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * getTotalDiffiuclty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  // adds the difficulties of each motif in the list together
  
  public double getTotalDifficulty() { 
    return this.first.getDifficulty() + rest.getTotalDifficulty();
  }
  /* Template:
   * Fields:
   * first -- IMotif
   * rest -- ILoMotif
   * Methods on Fields:
   * getDifficulty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * getTotalDiffiuclty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  // gets the total number of motifs require to make the current motif
  
  public double getNumberOfMotifs() { 
    return this.first.getNumberOfMotifs() + this.rest.getNumberOfMotifs();
  }
  /* Template:
   * Fields:
   * first -- IMotif
   * rest -- ILoMotif
   * Methods on Fields:
   * getDifficulty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * getTotalDiffiuclty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  // gets the name and type of stitch of every motif in the list
  
  public String getInfo() {
    if (this.rest.getInfo().equals("")) {
      return this.first.getInfo() + this.rest.getInfo();
    }
    else {
      return this.first.getInfo() + ", " + this.rest.getInfo();
    }
  }
}

// represents the last element in a lost of motifs aka the empty elements
class MtLoMotif implements ILoMotif {
  MtLoMotif(){}
  /* Template:
   * Fields:
   * Methods on Fields:
   * Arguments:
   */
  
  public double getTotalDifficulty() { 
    return 0.0;
  }
  /* Template:
   * Fields:
   * Methods on Fields:
   * Arguments:
   */
  
  public double getNumberOfMotifs() { 
    return 0.0;
  }
  /* Template:
   * Fields:
   * Methods on Fields:
   * Arguments:
   */
  
  public String getInfo() {
    return "";
  }
}
// represents a complete embroidery piece and its motifs, where
// name is the name of the piece and motif represents the complete stitch

class EmbroideryPiece {
  String name;
  IMotif motif;
  
  EmbroideryPiece(String name, IMotif motif) {
    this.name = name;
    this.motif = motif;
  }
  
  /* Template:
   * Fields:
   * name -- String
   * IMotif -- motif
   * Methods on Fields:
   * getDifficulty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  // gets the average difficulty of each motif in the piece
  
  double averageDifficulty() {
    if (motif.getNumberOfMotifs() == 0) {
      return 0.0;
    }
    return motif.getDifficulty() / motif.getNumberOfMotifs();
  }
  /* Template:
   * Fields:
   * name -- String
   * IMotif -- motif
   * Methods on Fields:
   * getDifficulty -- double
   * getNumberOfMotifs -- double
   * getInfo -- String
   * Arguments:
   */
  //gets information about each motif in the piece
  
  String embroideryInfo() {
    return name + ": " + motif.getInfo() + ".";
  }
}  
  

class ExamplesEmbroidery {
  IMotif rose = new CrossStitchMotif("rose", 5.0);
  IMotif poppy = new ChainStitchMotif("poppy", 4.75);
  IMotif daisy = new CrossStitchMotif("daisy", 3.2);
  IMotif flowers = new GroupMotif("flowers", new ConsLoMotif(this.rose, new ConsLoMotif(
      this.poppy,new ConsLoMotif(this.daisy, new MtLoMotif()))));
  IMotif bird = new CrossStitchMotif("bird", 4.5);
  IMotif tree = new ChainStitchMotif("tree", 3.0);
  IMotif nature = new GroupMotif("nature", new ConsLoMotif(this.bird, new ConsLoMotif(
      this.tree,new ConsLoMotif(this.flowers, new MtLoMotif()))));
  
  EmbroideryPiece pillowCover = new EmbroideryPiece("Pillow Cover", this.nature);
  EmbroideryPiece flower = new EmbroideryPiece("Flower", this.flowers);

  boolean testEmbroideryInfo(Tester t) { 
    return
      t.checkExpect(this.pillowCover.embroideryInfo(),
      "Pillow Cover: bird (cross stitch), tree (chain stitch), rose (cross stitch)"
      + ", poppy (chain stitch), daisy (cross stitch).") 
      && t.checkExpect(this.flower.embroideryInfo(),
          "Flower: rose (cross stitch), poppy (chain stitch), daisy (cross stitch).");
  }
  
  boolean testAverageDifficulty(Tester t) {
    return t.checkExpect(this.pillowCover.averageDifficulty(), 4.09) 
        && t.checkInexact(this.flower.averageDifficulty(), 4.316, 0.001);
  }
  
  
  boolean testGetNumberofMotifs(Tester t) {
    return t.checkExpect(this.nature.getNumberOfMotifs(), 5.0) 
        && t.checkExpect(this.rose.getNumberOfMotifs(), 1.0);
  }
  
  boolean testGetDifficulty(Tester t) {
    return  t.checkExpect(this.flowers.getDifficulty(), 12.95) 
        && t.checkExpect(this.nature.getDifficulty(), 20.45);
  }
  
  boolean testGetInfo(Tester t) {
    return t.checkExpect(this.nature.getInfo(), 
        "bird (cross stitch), tree (chain stitch), rose (cross stitch), "
        + "poppy (chain stitch), daisy (cross stitch)") 
        && t.checkExpect(this.rose.getInfo(), "rose (cross stitch)");
  }
}