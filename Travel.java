// represents any time of housing a player can live in
interface IHousing {
  
}

// represents a hut housing space where the population must be less than the capacity
class Hut implements IHousing {
  int capacity;
  int population;
  
  Hut(int capacity, int population) {
    this.capacity = capacity;
    this.population = population;  
  }
}

// represents an inn housing space where the population must be less than the capacity
// and where stalls is the number of available spots in its stable
class Inn implements IHousing {
  String name;
  int capacity;
  int population;
  int stalls;
  
  Inn(String name, int capacity, int population, int stalls) {
    this.name = name;
    this.capacity = capacity;
    this.population = population;
    this.stalls = stalls;
  }
}

//represents a castle housing space where family name is the name of its owners and
// carraigeHouse is the number of carriages it can hold
class Castle implements IHousing {
  String name;
  String familyName;
  int population;
  int carriageHouse;
  
  Castle(String name, String familyName, int population, int carriageHouse) {
    this.name = name;
    this.familyName = familyName;
    this.population = population;
    this.carriageHouse = carriageHouse;
  }
}

//represents types of transportation in the game
interface ITransportation {
  
}

// represents a trip be horse, from one housing space to another,
// can only travel to an inn if there is room in the stables
class Horse implements ITransportation {
  IHousing from;
  IHousing to;
  String name;
  String color;
  
  Horse(IHousing from, IHousing to, String name, String color) {
    this.from = from;
    this.to = to;
    this.name = name;
    this.color = color;
  }
}

// represents a trip by carriage where tonnage is the weight of the supplies being carried
// can only travel from inns to castles and vice versa and can only travel to a castle if there is
// room in the carriage house
class Carriage implements ITransportation {
  IHousing from;
  IHousing to;
  int tonnage;
  
  Carriage(IHousing from, IHousing to, int tonnage) {
    this.from = from;
    this.to = to;
    this.tonnage = tonnage;
  }
}

class ExamplesTravel {
  IHousing hovel = new Hut(5, 1);
  IHousing winterfell = new Castle("Winterfell", "Stark", 500, 6);
  IHousing crossroads = new Inn("Inn At The Crossroads", 40, 20, 12);
  
  IHousing bigHut = new Hut(15, 14);
  IHousing eyrie = new Castle("The Eyrie", "Arryn", 100, 1);
  IHousing holidayInn = new Inn("Holiday Inn", 30, 19, 8);
  
  ITransportation horse1 = new Horse(hovel, bigHut, "Steve", "White");
  ITransportation horse2 = new Horse(bigHut, winterfell, "Ryder", "Brown");
  
  ITransportation carriage1 = new Carriage(crossroads, eyrie, 20);
  ITransportation carriage2 = new Carriage(crossroads, winterfell, 100);
}
