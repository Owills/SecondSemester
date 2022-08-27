interface IIceCream {
  
}


class EmptyServing implements IIceCream {
  boolean cone;
  
  EmptyServing(boolean cone) {
    this.cone = cone; 
  }
}

class Scooped implements IIceCream {
  IIceCream more;
  String flavor;
  
  Scooped(IIceCream more, String flavor) {
    this.more = more;
    this.flavor = flavor;
  }
}

class ExamplesIceCream {
  IIceCream order1 = new Scooped(new Scooped(new Scooped(new Scooped(new EmptyServing(false)
      ,"caramel swirl"),"black raspberry"),"coffee"),"mint chip");
  
  IIceCream order2 = new Scooped(new Scooped(new Scooped(new EmptyServing(true)
      ,"strawberry"),"vanilla"),"chocolate");
}