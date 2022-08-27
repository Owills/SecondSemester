import java.util.Comparator;

import tester.*;

//represents a book with a title, author and price
class Book {
  String title;
  String author;
  int price;

  Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }

}

// compares the titles of two books alphabetically
class BooksByTitle implements Comparator<Book> {
  public int compare(Book t1, Book t2) {
    return t1.title.compareTo(t2.title);
  }
}

//compares the authors of two books alphabetically
class BooksByAuthor implements Comparator<Book> {
  public int compare(Book t1, Book t2) {
    return t1.author.compareTo(t2.author);
  }
}

//compares the prices of two books
class BooksByPrice implements Comparator<Book> {
  public int compare(Book t1, Book t2) {
    if (t1.price > t2.price) {
      return +1;
    }
    if (t1.price < t2.price) {
      return -1;
    }
    return 0;
  }
}

// represents a Binary Search Tree of any type of item
abstract class ABST<T> {
  Comparator<T> order;

  ABST(Comparator<T> order) {
    this.order = order;
  }

  // adds a new piece of data to the tree in its correctly ordered position
  public abstract ABST<T> insert(T newData);

  // checks if a certain item is contained in a tree
  public abstract boolean present(T item);

  // returns the left most item in the tree
  // throws error if called on a leaf
  public abstract T getLeftmost();
  
  //returns true if this is the same as the other leaf
  //returns false if called on a node
  public abstract boolean sameLeaf(Leaf<T> otherLeaf);

  // returns a new ABST without the leftmost item in the tree
  // throws error if called on a leaf
  public abstract ABST<T> getRight();

  // returns true if the other tree has the same data and structure as this tree
  public abstract boolean sameTree(ABST<T> other);

  // checks if this tree has the same data in the current node as the given data
  public abstract boolean sameDataFirstNode(T data);

  // checks if this trees left branch has the same data and structure as the
  // inputted left branch
  // and if this trees right branch has the same data and structure as the
  // inputted right branch
  public abstract boolean sameTreeHelper(ABST<T> left, ABST<T> right);

  // returns true if this tree has the same data in the same order as the other
  // tree
  public abstract boolean sameData(ABST<T> other);

  // produces a list of items in the tree in the same sorted order
  public abstract IList<T> buildList();
}

class Leaf<T> extends ABST<T> {
  Leaf(Comparator<T> order) {
    super(order);
  }

  public ABST<T> insert(T newData) {
    return new Node<T>(this.order, newData, new Leaf<T>(this.order), new Leaf<T>(this.order));
  }

  public boolean present(T item) {
    return false;
  }

  public T getLeftmost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }
  
  public boolean sameLeaf(Leaf<T> otherLeaf) {
    return true;
  }

  public ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }

  public boolean sameTree(ABST<T> other) {
    return other.sameLeaf(this);
  }

  public boolean sameDataFirstNode(T data) {
    return false;
  }

  public boolean sameTreeHelper(ABST<T> left, ABST<T> right) {
    return false;
  }

  public boolean sameData(ABST<T> other) {
    return other.sameLeaf(this);
  }

  public IList<T> buildList() {
    return new MtList<T>();
  }

}

class Node<T> extends ABST<T> {
  T data;
  ABST<T> left;
  ABST<T> right;

  Node(Comparator<T> order, T data, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = data;
    this.left = left;
    this.right = right;
  }

  public ABST<T> insert(T newData) {
    int thisCompare = this.order.compare(newData, this.data);

    if (thisCompare < 0) {
      return new Node<T>(this.order, this.data, this.left.insert(newData), this.right);
    }
    return new Node<T>(this.order, this.data, this.left, this.right.insert(newData));
  }

  public boolean present(T item) {
    int thisCompare = this.order.compare(item, this.data);
    if (thisCompare == 0) {
      return true;
    }
    if (thisCompare < 0) {
      return this.left.present(item);
    }
    return this.right.present(item);

  }

  public T getLeftmost() {
    if (this.left.sameTree(new Leaf<T>(this.order))) {
      return this.data;
    }
    else {
      return this.left.getLeftmost();
    }
  }
  
  public boolean sameLeaf(Leaf<T> otherLeaf) {
    return false;
  }


  public ABST<T> getRight() {
    if (this.left.sameTree(new Leaf<T>(this.order))) {
      if (this.right.sameTree(new Leaf<T>(this.order))) {
        return new Leaf<T>(order);
      }
      else {
        return this.right;
      }
    }
    else {
      return new Node<T>(this.order, this.data, this.left.getRight(), this.right);
    }
  }

  public boolean sameTree(ABST<T> other) {
    if (other.sameDataFirstNode(this.data)) {
      return other.sameTreeHelper(this.left, this.right);
    }
    return false;
  }

  public boolean sameTreeHelper(ABST<T> left, ABST<T> right) {
    return this.left.sameTree(left) && this.right.sameTree(right);
  }

  public boolean sameDataFirstNode(T data) {
    return this.data == data;
  }

  public boolean sameData(ABST<T> other) {
    if (this.getLeftmost() == other.getLeftmost()) {
      return this.getRight().sameData(other.getRight());
    }
    return false;
  }

  public IList<T> buildList() {
    return new ConsList<T>(this.getLeftmost(), this.getRight().buildList());
  }
}


//represents of a list of items
interface IList<T> {

}

class MtList<T> implements IList<T> {

}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }
}

class ExamplesABST {
  Book Peaky = new Book("Peaky", "Blinders", 120);
  Book Walter = new Book("Breaking", "Mad", 350);
  Book Uncharted = new Book("Uncharted", "Drake", 2000);
  Book Crossfire = new Book("Hunger Games", "Gorilla", 500);
  Book Garbage = new Book("Gross", "Nuts", 7);
  Book Kuroko = new Book("Basketo", "Ace", 90);
  Book Pubg = new Book("Autoplay", "Campaign", 100);
  Book Xenxia = new Book("Snake", "Zenzia", 300);

  BooksByTitle titlematch = new BooksByTitle();
  BooksByAuthor authormatch = new BooksByAuthor();
  BooksByPrice pricematch = new BooksByPrice();

  Leaf<Book> emptyTitleMatch = new Leaf<Book>(titlematch);
  Leaf<Book> emptyAuthorMatch = new Leaf<Book>(authormatch);
  Leaf<Book> emptyPriceMatch = new Leaf<Book>(pricematch);

  // ordered by price
  ABST<Book> balancedTree1 = new Node<Book>(pricematch, Crossfire,
      new Node<Book>(pricematch, Peaky, emptyPriceMatch, emptyPriceMatch),
      new Node<Book>(pricematch, Uncharted, emptyPriceMatch, emptyPriceMatch));

  // ordered by author
  ABST<Book> balancedTree2 = new Node<Book>(authormatch, Uncharted,
      new Node<Book>(authormatch, Peaky, emptyAuthorMatch, emptyAuthorMatch),
      new Node<Book>(authormatch, Crossfire, emptyAuthorMatch, emptyAuthorMatch));

  // ordered by title
  ABST<Book> balancedTree3 = new Node<Book>(titlematch, Peaky,
      new Node<Book>(titlematch, Crossfire, emptyTitleMatch, emptyTitleMatch),
      new Node<Book>(titlematch, Uncharted, emptyTitleMatch, emptyTitleMatch));

  ABST<Book> balancedTree1InsertGarabage = new Node<Book>(pricematch, Crossfire,
      new Node<Book>(pricematch, Peaky,
          new Node<Book>(pricematch, Garbage, emptyPriceMatch, emptyPriceMatch), emptyPriceMatch),
      new Node<Book>(pricematch, Uncharted, emptyPriceMatch, emptyPriceMatch));

  ABST<Book> leftLeaningTree = new Node<Book>(pricematch, Crossfire,
      new Node<Book>(pricematch, Peaky, new Node<Book>(pricematch, Kuroko,
          new Node<Book>(pricematch, Garbage, emptyPriceMatch, emptyPriceMatch), emptyPriceMatch),
          emptyPriceMatch),
      emptyPriceMatch);

  ABST<Book> rightLeaningTree = new Node<Book>(pricematch, Garbage, emptyPriceMatch,
      new Node<Book>(pricematch, Kuroko, emptyPriceMatch,
          new Node<Book>(pricematch, Peaky, emptyPriceMatch,
              new Node<Book>(pricematch, Crossfire, emptyPriceMatch, emptyPriceMatch))));

  // same order as blancedTree1
  IList<Book> balancedList1 = new ConsList<Book>(Peaky,
      new ConsList<Book>(Crossfire, new ConsList<Book>(Uncharted, new MtList<Book>())));

  // same order as blancedTree2
  IList<Book> balancedList2 = new ConsList<Book>(Peaky,
      new ConsList<Book>(Uncharted, new ConsList<Book>(Crossfire, new MtList<Book>())));

  // same order as blancedTree2
  IList<Book> balancedList3 = new ConsList<Book>(Crossfire,
      new ConsList<Book>(Peaky, new ConsList<Book>(Uncharted, new MtList<Book>())));

  // different order from anything
  IList<Book> list1 = new ConsList<Book>(Garbage,
      new ConsList<Book>(Crossfire,
          new ConsList<Book>(Uncharted, new ConsList<Book>(Kuroko, new ConsList<Book>(Peaky,
              new ConsList<Book>(Walter, new ConsList<Book>(Xenxia, new MtList<Book>())))))));

  // test insert method for ABST
  boolean testInsert(Tester t) {
    return t.checkExpect(emptyTitleMatch.insert(Peaky),
        new Node<Book>(titlematch, Peaky, emptyTitleMatch, emptyTitleMatch))
        && t.checkExpect(emptyAuthorMatch.insert(Peaky),
            new Node<Book>(authormatch, Peaky, emptyAuthorMatch, emptyAuthorMatch))
        && t.checkExpect(emptyPriceMatch.insert(Peaky),
            new Node<Book>(pricematch, Peaky, emptyPriceMatch, emptyPriceMatch))
        && t.checkExpect(balancedTree1.insert(Garbage), balancedTree1InsertGarabage)
        && t.checkExpect(balancedTree1InsertGarabage.insert(Kuroko),
            new Node<Book>(pricematch, Crossfire,
                new Node<Book>(pricematch, Peaky,
                    new Node<Book>(pricematch, Garbage, emptyPriceMatch,
                        new Node<Book>(pricematch, Kuroko, emptyPriceMatch, emptyPriceMatch)),
                    emptyPriceMatch),
                new Node<Book>(pricematch, Uncharted, emptyPriceMatch, emptyPriceMatch)))
        && t.checkExpect(balancedTree1.insert(Crossfire),
            new Node<Book>(pricematch, Crossfire,
                new Node<Book>(pricematch, Peaky, emptyPriceMatch, emptyPriceMatch),
                new Node<Book>(pricematch, Uncharted,
                    new Node<Book>(pricematch, Crossfire, emptyPriceMatch, emptyPriceMatch),
                    emptyPriceMatch)))
        && t.checkExpect(balancedTree1.insert(Uncharted),
            new Node<Book>(pricematch, Crossfire,
                new Node<Book>(pricematch, Peaky, emptyPriceMatch, emptyPriceMatch),
                new Node<Book>(pricematch, Uncharted, emptyPriceMatch,
                    new Node<Book>(pricematch, Uncharted, emptyPriceMatch, emptyPriceMatch))))
        && t.checkExpect(emptyPriceMatch.insert(Crossfire).insert(Peaky).insert(Uncharted),
            balancedTree1)
        && t.checkExpect(emptyAuthorMatch.insert(Uncharted).insert(Crossfire).insert(Peaky),
            balancedTree2)
        && t.checkExpect(emptyTitleMatch.insert(Peaky).insert(Crossfire).insert(Uncharted),
            balancedTree3);

  }

  // test present method for ABST
  boolean testPresent(Tester t) {
    return t.checkExpect(emptyTitleMatch.present(Crossfire), false)
        && t.checkExpect(balancedTree1.present(Crossfire), true)
        && t.checkExpect(balancedTree1.present(Garbage), false)
        && t.checkExpect(balancedTree2.present(Crossfire), true)
        && t.checkExpect(balancedTree1InsertGarabage.present(Garbage), true);
  }

  // test getLeftmost method for ABST
  boolean testGetLeftmost(Tester t) {
    return t.checkException(new RuntimeException("No leftmost item of an empty tree"),
        emptyTitleMatch, "getLeftmost")
        && t.checkException(new RuntimeException("No leftmost item of an empty tree"),
            emptyAuthorMatch, "getLeftmost")
        && t.checkException(new RuntimeException("No leftmost item of an empty tree"),
            emptyPriceMatch, "getLeftmost")
        && t.checkExpect(balancedTree1InsertGarabage.getLeftmost(), Garbage)
        && t.checkExpect(leftLeaningTree.getLeftmost(), Garbage)
        && t.checkExpect(rightLeaningTree.getLeftmost(), Garbage)
        && t.checkExpect(balancedTree1.getLeftmost(), Peaky);
  }


  // test getRight method for ABST
  boolean testGetRight(Tester t) {
    return t.checkException(new RuntimeException("No right of an empty tree"), emptyTitleMatch,
        "getRight")
        && t.checkException(new RuntimeException("No right of an empty tree"), emptyPriceMatch,
            "getRight")
        && t.checkException(new RuntimeException("No right of an empty tree"), emptyAuthorMatch,
            "getRight")
        && t.checkExpect(balancedTree1.getRight(),
            new Node<Book>(pricematch, Crossfire, emptyPriceMatch,
                new Node<Book>(pricematch, Uncharted, emptyPriceMatch, emptyPriceMatch)))
        && t.checkExpect(balancedTree1InsertGarabage.getRight(),balancedTree1)
        && t.checkExpect(leftLeaningTree.getRight(),
            new Node<Book>(pricematch, Crossfire,
                new Node<Book>(pricematch, Peaky,
                    new Node<Book>(pricematch, Kuroko, emptyPriceMatch, emptyPriceMatch),
                    emptyPriceMatch),
                emptyPriceMatch))
        && t.checkExpect(rightLeaningTree.getRight(),
            new Node<Book>(pricematch, Kuroko, emptyPriceMatch,
                new Node<Book>(pricematch, Peaky, emptyPriceMatch,
                    new Node<Book>(pricematch, Crossfire, emptyPriceMatch, emptyPriceMatch))));
  }
  
  //test sameLeaf method for ABST
  boolean testSameLeaf(Tester t) {
    return t.checkExpect(balancedTree1.sameLeaf(emptyPriceMatch), false)
        && t.checkExpect(balancedTree1.sameLeaf(emptyAuthorMatch), false)
        && t.checkExpect(balancedTree1.sameLeaf(emptyTitleMatch), false)
        && t.checkExpect(emptyPriceMatch.sameLeaf(emptyAuthorMatch), true)
        && t.checkExpect(emptyPriceMatch.sameLeaf(emptyTitleMatch), true)
        && t.checkExpect(emptyPriceMatch.sameLeaf(emptyPriceMatch), true);
  }

  // test sameTree method for ABST
  boolean testSameTree(Tester t) {
    return t.checkExpect(emptyPriceMatch.sameTree(balancedTree1), false)
        && t.checkExpect(emptyPriceMatch.sameTree(emptyPriceMatch), true)
        && t.checkExpect(emptyPriceMatch.sameTree(emptyTitleMatch), true)
        && t.checkExpect(balancedTree1.sameTree(balancedTree1), true)
        && t.checkExpect(balancedTree1.sameTree(balancedTree2), false)
        && t.checkExpect(balancedTree1.sameTree(balancedTree3), false)
        && t.checkExpect(balancedTree1.sameTree(leftLeaningTree), false)
        && t.checkExpect(rightLeaningTree.sameTree(leftLeaningTree), false)
        && t.checkExpect(balancedTree1
            .sameTree(emptyPriceMatch.insert(Uncharted).insert(Crossfire).insert(Peaky)), false);
  }

  // test sameDataFirstNode for ABST
  boolean testSameDataFirstNode(Tester t) {
    return t.checkExpect(emptyPriceMatch.sameDataFirstNode(Crossfire), false)
        && t.checkExpect(balancedTree1.sameDataFirstNode(Crossfire), true)
        && t.checkExpect(balancedTree2.sameDataFirstNode(Crossfire), false)
        && t.checkExpect(balancedTree2.sameDataFirstNode(Uncharted), true);
  }

  // test sameTreeHelper for ABST
  boolean testSameTreeHelper(Tester t) {
    return t.checkExpect(emptyPriceMatch.sameTreeHelper(balancedTree1, balancedTree1), false)
        && t.checkExpect(balancedTree1.sameTreeHelper(
            new Node<Book>(pricematch, Peaky, emptyPriceMatch, emptyPriceMatch),
            new Node<Book>(pricematch, Uncharted, emptyPriceMatch, emptyPriceMatch)), true)
        && t.checkExpect(balancedTree2.sameTreeHelper(
            new Node<Book>(pricematch, Peaky, emptyPriceMatch, emptyPriceMatch),
            new Node<Book>(pricematch, Uncharted, emptyPriceMatch, emptyPriceMatch)), false)
        && t.checkExpect(balancedTree1.sameTreeHelper(balancedTree1, balancedTree1), false);
  }

  // test sameData method for ABST
  boolean testSameData(Tester t) {
    return t.checkExpect(emptyPriceMatch.sameData(balancedTree1), false)
        && t.checkExpect(emptyPriceMatch.sameData(emptyPriceMatch), true)
        && t.checkExpect(emptyPriceMatch.sameData(emptyTitleMatch), true)
        && t.checkExpect(balancedTree1.sameData(balancedTree1), true)
        && t.checkExpect(balancedTree1.sameData(balancedTree2), false)
        && t.checkExpect(balancedTree1.sameData(balancedTree3), false)
        && t.checkExpect(balancedTree1.sameData(leftLeaningTree), false)
        && t.checkExpect(rightLeaningTree.sameData(leftLeaningTree), true)
        && t.checkExpect(balancedTree1
            .sameData(emptyPriceMatch.insert(Uncharted).insert(Crossfire).insert(Peaky)), true);
  }

  // test buildList method for ABST
  boolean testBuildList(Tester t) {
    return t.checkExpect(balancedTree1.buildList(), balancedList1)
        && t.checkExpect(balancedTree2.buildList(), balancedList2)
        && t.checkExpect(balancedTree3.buildList(), balancedList3)
        && t.checkExpect(emptyPriceMatch.buildList(), new MtList<Book>())
        && t.checkExpect(emptyTitleMatch.buildList(), new MtList<Book>())
        && t.checkExpect(emptyAuthorMatch.buildList(), new MtList<Book>());
  }

}
