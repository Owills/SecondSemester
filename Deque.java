import tester.Tester;
import java.util.function.Predicate;

//to represent a Deque List
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // returns the total number of nodes in this deque
  int size() {
    return this.header.next.size();
  }

  // modifies the deque by adding a new node with data t at the head of the deque
  T addAtHead(T t) {
    return header.addAtHead(t);
  }

  // modifies the deque by adding a new node with data t at the tail of the deque
  T addAtTail(T t) {
    return header.addAtTail(t);
  }

  // modifies the deque by removing a node with data t at the head of the deque
  T removeFromHead() {
    if (this.size() == 0) {
      throw new RuntimeException("Cannot remove from an empty list");
    }
    return header.removeFromHead();
  }

  // modifies the deque by removing a node with data t at the tail of the deque
  T removeFromTail() {
    if (this.size() == 0) {
      throw new RuntimeException("Cannot remove from an empty list");
    }
    return header.removeFromTail();
  }

  // finds a node that satisfies the predicate,
  // otherwise returns the header
  ANode<T> find(Predicate<T> func) {
    return this.header.next.find(func);
  }

  // removing a node from the deque
  void removeNode(ANode<T> n) {
    if (!n.equals(header)) {
      header.removeNodeFirst(n);
    }
  }
}


//representing an abstract class which denotes either a regular node or a sentinel node
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  //gets the size of the list
  public abstract int size(); 
  
  //find the first node which the predicate of the data gives true
  public abstract ANode<T> find(Predicate<T> func); 

  // removes this node from the deque
  T remove() {
    this.prev.next = this.next;
    this.next.prev = this.prev;
    return this.getData();
  }

  // supporting the removal of node n from the deque
  void removeNode(ANode<T> n) {
    if (this.equals(n)) {
      this.remove();
    }
    else {
      this.next.removeNode(n);
    }
  }

  //gets the data of node or returns null if sentinel
  public abstract T getData();

}

//representing Sentinel node
class Sentinel<T> extends ANode<T> {

  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  // supporting in returning the total number of nodes in the deque
  public int size() {
    return 0;
  }

  // adds a node with data t to the head
  T addAtHead(T t) {
    new Node<T>(t, this.next, this);
    return this.next.getData();
  }

  // adds a node with data t to the tail
  T addAtTail(T t) {
    new Node<T>(t, this, this.prev);
    return this.next.getData();
  }

  // removes a node from the head of the deque
  T removeFromHead() {
    return this.next.remove();
  }
  
  // removes a node from the tail of the deque
  T removeFromTail() {
    return this.prev.remove();
  }

  // supports to find a node that meets predicate
  // boolean b is true if this is the first time scrolling
  // through the sentinel searching for the required node
  public ANode<T> find(Predicate<T> p) {
    return this;
  }
  // supports in removing a node n from the deque
  // this is the 1st time searching for the node

  void removeNodeFirst(ANode<T> n) {
    this.next.removeNode(n);
  }

  // this is the second time searching for the node
  // so it should return a null because it doesn't exist anymore

  void removeNode(ANode<T> n) {
    return;
  }
  
  public T getData() {
    return null;
  }

}

class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    this.data = data;
    this.next = null;
    this.prev = null;
  }

  //places the node in deque after its initialization
  Node(T data, ANode<T> next, ANode<T> prev) {
    if ((prev == null) || (next == null)) {
      throw new IllegalArgumentException("Cannot accept null node");
    }
    this.data = data;
    this.next = next;
    this.prev = prev;
    prev.next = this;
    next.prev = this;
  }

  public int size() {
    return 1 + this.next.size();
  }

  public ANode<T> find(Predicate<T> func) {
    if (func.test(this.data)) {
      return this;
    }
    return this.next.find(func);
  }

  public T getData() {
    return this.data;
  }
}

class ExamplesDeque {

  Sentinel<String> s1 = new Sentinel<String>();
  Deque<String> deque1 = new Deque<String>(s1);

  Sentinel<String> s2 = new Sentinel<String>();
  Node<String> abc = new Node<String>("abc", s2, s2);
  Node<String> bcd = new Node<String>("bcd", s2, abc);
  Node<String> cde = new Node<String>("cde", s2, bcd);
  Node<String> def = new Node<String>("def", s2, cde);
  Deque<String> deque2 = new Deque<String>(s2);

  Sentinel<String> s3 = new Sentinel<String>();
  Node<String> str1 = new Node<String>("z", s3, s3);
  Node<String> str2 = new Node<String>("hello", s3, str1);
  Node<String> str3 = new Node<String>("what?", s3, str2);
  Node<String> str4 = new Node<String>("abc", s3, str3);
  Node<String> str5 = new Node<String>("abc", s3, str4);
  Deque<String> deque3 = new Deque<String>(s3);

  void initConditions() {
    s1 = new Sentinel<String>();
    deque1 = new Deque<String>(s1);

    s2 = new Sentinel<String>();
    abc = new Node<String>("abc", s2, s2);
    bcd = new Node<String>("bcd", s2, abc);
    cde = new Node<String>("cde", s2, bcd);
    def = new Node<String>("def", s2, cde);
    deque2 = new Deque<String>(s2);

    s3 = new Sentinel<String>();
    str1 = new Node<String>("z", s3, s3);
    str2 = new Node<String>("hello", s3, str1);
    str3 = new Node<String>("what?", s3, str2);
    str4 = new Node<String>("abc", s3, str3);
    str5 = new Node<String>("abc", s3, str4);
    deque3 = new Deque<String>(s3);

  }

  // test size method for Deque
  boolean testSizeDeque(Tester t) {
    initConditions();
    return t.checkExpect(deque1.size(), 0) && t.checkExpect(deque2.size(), 4)
        && t.checkExpect(deque3.size(), 5);
  }

  // test size method for ANode
  boolean testSizeANode(Tester t) {
    initConditions();
    return t.checkExpect(s1.size(), 0) && t.checkExpect(s2.size(), 0) && t.checkExpect(s3.size(), 0)
        && t.checkExpect(str1.size(), 5) && t.checkExpect(str2.size(), 4)
        && t.checkExpect(def.size(), 1);
  }

  // test addAtHead for Deque
  void testAddAtHead(Tester t) {
    initConditions();
    t.checkExpect(deque1.header.next, s1);
    deque1.addAtHead("a");
    t.checkExpect(deque1.size(), 1);
    t.checkExpect(deque1.header.next, new Node<String>("a", s1, s1));
    t.checkExpect(deque1.header.prev, new Node<String>("a", s1, s1));

    t.checkExpect(deque2.size(), 4);
    deque2.addAtHead("a");
    t.checkExpect(deque2.size(), 5);
    t.checkExpect(deque2.header.next, new Node<String>("a", abc, s2));
    t.checkExpect(s2.next.next, abc);
    t.checkExpect(deque2.header.prev, def);
  }

  // test addAtTail for Deque
  void testAddAtTail(Tester t) {
    initConditions();
    t.checkExpect(deque1.header.next, s1);
    deque1.addAtTail("a");
    t.checkExpect(deque1.size(), 1);
    t.checkExpect(s1.next, new Node<String>("a", s1, s1));
    t.checkExpect(s1.next.next, s1);
    t.checkExpect(deque1.header.prev, new Node<String>("a", s1, s1));

    t.checkExpect(deque2.size(), 4);
    deque2.addAtTail("a");
    t.checkExpect(deque2.size(), 5);
    t.checkExpect(deque2.header.next, abc);
    t.checkExpect(deque2.header.prev, new Node<String>("a", s2, def));
  }

  // test removeAtHead for Deque
  void testRemoveAtHeadDeque(Tester t) {
    initConditions();
    t.checkException(new RuntimeException("Cannot remove from an empty list"), deque1,
        "removeFromHead");

    t.checkExpect(deque2.size(), 4);
    t.checkExpect(deque2.removeFromHead(), "abc");
    t.checkExpect(deque2.size(), 3);
    t.checkExpect(deque2.header.next, bcd);
    t.checkExpect(deque2.header.prev, def);
    t.checkExpect(deque2.removeFromHead(), "bcd");
    t.checkExpect(deque2.size(), 2);
    t.checkExpect(deque2.header.next, cde);
    t.checkExpect(deque2.header.prev, def);

    t.checkExpect(deque2.removeFromHead(), "cde");
    t.checkExpect(deque2.header.next, def);
    t.checkExpect(deque2.header.prev, def);
    t.checkExpect(deque2.removeFromHead(), "def");
    t.checkExpect(deque2.header.next, deque2.header);
    t.checkExpect(deque2.header.prev, deque2.header);

    t.checkExpect(deque3.size(), 5);
    t.checkExpect(deque3.removeFromHead(), "z");
    t.checkExpect(deque3.size(), 4);
    t.checkExpect(deque3.header.next, str2);
    t.checkExpect(deque3.header.prev, str5);

  }

  // test removeAtTail for Deque
  void testRemoveAtTailDeque(Tester t) {
    initConditions();
    t.checkException(new RuntimeException("Cannot remove from an empty list"), deque1,
        "removeFromTail");

    t.checkExpect(deque2.size(), 4);
    t.checkExpect(deque2.removeFromTail(), "def");
    t.checkExpect(deque2.size(), 3);
    t.checkExpect(deque2.header.next, abc);
    t.checkExpect(deque2.header.prev, cde);

    t.checkExpect(deque3.size(), 5);
    t.checkExpect(deque3.removeFromTail(), "abc");
    t.checkExpect(deque3.size(), 4);
    t.checkExpect(deque3.header.next, str1);
    t.checkExpect(deque3.header.prev, str4);
  }

  // test find method for Deque
  boolean testFindDeque(Tester t) {
    initConditions();
    return t.checkExpect(deque1.find(x -> x.length() > 5), s1)
        && t.checkExpect(deque1.find(x -> x.startsWith("a")), s1)
        && t.checkExpect(deque2.find(x -> x.length() > 2), abc)
        && t.checkExpect(deque2.find(x -> x.length() > 3), s2)
        && t.checkExpect(deque2.find(x -> x.startsWith("a")), abc)
        && t.checkExpect(deque3.find(x -> x.length() > 2), str2)
        && t.checkExpect(deque3.find(x -> x.length() > 3), str2)
        && t.checkExpect(deque3.find(x -> x.startsWith("a")), str4);
  }

  // test removeNode method for Deque
  void testRemoveNodeDeque(Tester t) {
    initConditions();
    t.checkExpect(deque1.size(), 0);
    deque1.removeNode(abc);
    t.checkExpect(deque1.size(), 0);

    t.checkExpect(deque2.size(), 4);
    deque2.removeNode(abc);
    t.checkExpect(deque2.size(), 3);
    t.checkExpect(deque2.header.next, bcd);
    t.checkExpect(deque2.header.prev, def);

    initConditions();
    t.checkExpect(deque2.find(x -> x.startsWith("b")), bcd);
    deque2.removeNode(bcd);
    t.checkExpect(deque2.find(x -> x.startsWith("b")), s2);

    initConditions();
    t.checkExpect(deque2.size(), 4);
    deque2.removeNode(s2);
    t.checkExpect(deque2.size(), 4);
  }

}