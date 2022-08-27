import tester.*;

// to represent a list of Strings
interface ILoString {
  // combine all Strings in this list into one
  String combine();

  // inserts a string into a list a strings in the alphabetical order
  // (case-insensitive)
  // when the list is correctly sorted
  ILoString insert(String other);

  // returns a new list of Strings sorted into alphabetical order
  // (case-insensitive)
  ILoString sort();

  // determines with the list is sorted in alphabetical order (case-insensitive)
  boolean isSorted();

  // produces a list where the first, third, fifth... elements are from this list,
  // and the second, fourth, sixth... elements are from the given list
  ILoString interleave(ILoString otherList);

  // takes a sorted lists of strings and produces a new sorted list of string
  // with all the elements from the two lists
  ILoString merge(ILoString otherList);

  // produces a new list of strings with the same elements but in reverse order
  ILoString reverse();

  // produces a new list of strings with the string at the last element
  ILoString putLast(String first);

  // determines if this list contains pairs of identical strings
  boolean isDoubledList();

  // checks if the first String equals the given String
  boolean isFirstEqual(String other);

  // determines if the rest of the list contains pairs of identical strings
  boolean isDoubledListHelper();

  // determines whether this list contains the same words reading the list in
  // either order
  boolean isPalindromeList();

  // helps to remove the last element in a list
  ILoString palindromeHelper();

}

// to represent an empty list of Strings
class MtLoString implements ILoString {
  MtLoString() {
  }

  // combine all Strings in this list into one
  public String combine() {
    return "";
  }

  // inserts a string into a list a strings in the alphabetical order
  // when the list is correctly sorted
  public ILoString insert(String other) {
    return new ConsLoString(other, this);
  }

  // returns a new list of Strings sorted into alphabetical order
  public ILoString sort() {
    return new MtLoString();
  }

  // determines with the list is sorted in alphabetical order (case-insensitive)
  public boolean isSorted() {
    return true;
  }

  // produces a list where the first, third, fifth... elements are from this list,
  // and the second, fourth, sixth... elements are from the given list
  public ILoString interleave(ILoString otherList) {
    return otherList;
  }

  // takes a sorted lists of strings and produces a new sorted list of string
  // with all the elements from the two lists
  public ILoString merge(ILoString otherList) {
    return otherList;
  }

  // produces a new list of strings with the same elements but in reverse order
  public ILoString reverse() {
    return new MtLoString();
  }

  // produces a new list of strings with the string at the last element
  public ILoString putLast(String first) {
    return new ConsLoString(first, this);
  }

  // determines if this list contains pairs of identical strings
  public boolean isDoubledList() {
    return true;
  }

  // checks if the first String equals the given String
  public boolean isFirstEqual(String other) {
    return false;
  }

  // determines if the rest of the list contains pairs of identical strings
  public boolean isDoubledListHelper() {
    return true;
  }

  // determines whether this list contains the same words reading the list in
  // either order
  public boolean isPalindromeList() {
    return true;
  }

  // helps to remove the last element in a list
  public ILoString palindromeHelper() {
    return this;
  }
}

// to represent a nonempty list of Strings
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  // combine all Strings in this list into one
  public String combine() {
    return this.first.concat(this.rest.combine());
  }

  // inserts a string into a list a strings in the alphabetical order
  // when the list is correctly sorted
  public ILoString insert(String other) {
    if (this.first.toLowerCase().compareTo(other.toLowerCase()) < 0) {
      return new ConsLoString(this.first, this.rest.insert(other));
    }
    return new ConsLoString(other, this);
  }

  // returns a new list of Strings sorted into alphabetical order
  public ILoString sort() {
    return this.rest.sort().insert(this.first);
  }

  // determines with the list is sorted in alphabetical order (case-insensitive)
  public boolean isSorted() {
    String restCombine = this.rest.combine().toLowerCase();
    if (this.first.toLowerCase().compareTo(restCombine) <= 0 || restCombine.equals("")) {
      return rest.isSorted();
    }
    return false;
  }
  
  /*
  TEMPLATE
  FIELDS:
  ... this.first ...         -- String
  ... this.rest ...          -- ILoString
  
  METHODS
  ... this.combine() ...     -- String
  ... this.insert() ... -- ILoString
  ... this.sort() ... -- ILoString
  ... this.isSorted() ... -- Boolean
  ... this.interleave(ILoString otherList) ... -- ILoString
  ... this.merge(ILoString otherList) ... -- ILoString
  ... this.reverse() ... -- ILoString
  ... this.putLast(String first) ... -- ILoString
  ... this.isDoubledList() ... -- Boolean
  ... this.isFirstEqual(String other) ... -- Boolean
  ... this.isDoubledListHelper() ... -- Boolean
  ... this.isPalindromeList() ... -- Boolean
  ... this.palindromeHelper() ... -- ILoString
  
  METHODS FOR FIELDS
  ... this.first.concat(String) ...        -- String
  ... this.first.compareTo(String) ...     -- int
  ... this.rest.combine() ...              -- String
  ... this.rest.insert() ... -- ILoString
  ... this.rest.sort() ... -- ILoString
  ... this.rest.isSorted() ... -- Boolean
  ... this.rest.interleave(ILoString otherList) ... -- ILoString
  ... this.rest.merge(ILoString otherList) ... -- ILoString
  ... this.rest.reverse() ... -- ILoString
  ... this.rest.putLast(String first) ... -- ILoString
  ... this.rest.isDoubledList() ... -- Boolean
  ... this.rest.isFirstEqual(String other) ... -- Boolean
  ... this.rest.isDoubledListHelper() ... -- Boolean
  ... this.rest.isPalindromeList() ... -- Boolean
  ... this.rest.palindromeHelper() ... -- ILoString
  Arguments
  ... otherList ... -- ILoString
  */
  // produces a list where the first, third, fifth... elements are from this list,
  // and the second, fourth, sixth... elements are from the given list
  public ILoString interleave(ILoString otherList) {
    return new ConsLoString(this.first, otherList.interleave(this.rest));
  }

  /*
  TEMPLATE
  FIELDS:
  ... this.first ...         -- String
  ... this.rest ...          -- ILoString
  
  METHODS
  ... this.combine() ...     -- String
  ... this.insert() ... -- ILoString
  ... this.sort() ... -- ILoString
  ... this.isSorted() ... -- Boolean
  ... this.interleave(ILoString otherList) ... -- ILoString
  ... this.merge(ILoString otherList) ... -- ILoString
  ... this.reverse() ... -- ILoString
  ... this.putLast(String first) ... -- ILoString
  ... this.isDoubledList() ... -- Boolean
  ... this.isFirstEqual(String other) ... -- Boolean
  ... this.isDoubledListHelper() ... -- Boolean
  ... this.isPalindromeList() ... -- Boolean
  ... this.palindromeHelper() ... -- ILoString
  
  METHODS FOR FIELDS
  ... this.first.concat(String) ...        -- String
  ... this.first.compareTo(String) ...     -- int
  ... this.rest.combine() ...              -- String
  ... this.rest.insert() ... -- ILoString
  ... this.rest.sort() ... -- ILoString
  ... this.rest.isSorted() ... -- Boolean
  ... this.rest.interleave(ILoString otherList) ... -- ILoString
  ... this.rest.merge(ILoString otherList) ... -- ILoString
  ... this.rest.reverse() ... -- ILoString
  ... this.rest.putLast(String first) ... -- ILoString
  ... this.rest.isDoubledList() ... -- Boolean
  ... this.rest.isFirstEqual(String other) ... -- Boolean
  ... this.rest.isDoubledListHelper() ... -- Boolean
  ... this.rest.isPalindromeList() ... -- Boolean
  ... this.rest.palindromeHelper() ... -- ILoString
  Arguments
  ... otherList ... -- ILoString
  */
  // takes a sorted lists of strings and produces a new sorted list of string
  // with all the elements from the two lists
  public ILoString merge(ILoString otherList) {
    ILoString newList = otherList.insert(this.first);
    return this.rest.merge(newList);
  }

  // produces a new list of strings with the same elements but in reverse order
  public ILoString reverse() {
    return this.rest.reverse().putLast(this.first);
  }

  // produces a new list of strings with the string at the last element
  public ILoString putLast(String first) {
    return new ConsLoString(this.first, rest.putLast(first));
  }

  // determines if this list contains pairs of identical strings
  public boolean isDoubledList() {
    if (this.rest.isFirstEqual(this.first)) {
      return this.rest.isDoubledListHelper();
    }
    return false;
  }

  // checks if the first String equals the given String
  public boolean isFirstEqual(String other) {
    return this.first.equals(other);
  }

  // determines if the rest of the list contains pairs of identical strings
  public boolean isDoubledListHelper() {
    return this.rest.isDoubledList();
  }

  // determines whether this list contains the same words reading the list in
  // either order
  public boolean isPalindromeList() {
    if (this.reverse().isFirstEqual(this.first)) {
      return this.rest.reverse().palindromeHelper().isPalindromeList();
    }
    return false;
  }

  // helps to remove the last element in a list
  public ILoString palindromeHelper() {
    return this.rest.reverse();
  }

}

// to represent examples for lists of strings
class ExamplesStrings {

  ILoString mary = new ConsLoString("Mary ", new ConsLoString("had ", new ConsLoString("a ",
      new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString())))));

  ILoString john = new ConsLoString("John ", new ConsLoString("is ", new ConsLoString("a ",
      new ConsLoString("weird ", new ConsLoString("dude.", new MtLoString())))));

  // test the method combine for the lists of Strings
  boolean testCombine(Tester t) {
    return t.checkExpect(this.mary.combine(), "Mary had a little lamb.")
        && t.checkExpect(this.john.combine(), "John is a weird dude.");
  }

  // test the method insert for the list of Strings
  boolean testInsert(Tester t) {
    return t.checkExpect(this.mary.insert("z"),
        new ConsLoString("Mary ",
            new ConsLoString("had ",
                new ConsLoString("a ",
                    new ConsLoString("little ",
                        new ConsLoString("lamb.", new ConsLoString("z", new MtLoString())))))))
        && t.checkExpect(this.john.insert("z"),
            new ConsLoString("John ",
                new ConsLoString("is ", new ConsLoString("a ", new ConsLoString("weird ",
                    new ConsLoString("dude.", new ConsLoString("z", new MtLoString())))))));
  }

  // test the method sort for the list of Strings
  boolean testSort(Tester t) {
    return t.checkExpect(this.mary.sort(),
        new ConsLoString("a ",
            new ConsLoString("had ",
                new ConsLoString("lamb.",
                    new ConsLoString("little ", new ConsLoString("Mary ", new MtLoString()))))))
        && t.checkExpect(this.john.sort(),
            new ConsLoString("a ", new ConsLoString("dude.", new ConsLoString("is ",
                new ConsLoString("John ", new ConsLoString("weird ", new MtLoString()))))));
  }

  // test the method isSorted for the list of Strings
  boolean testIsSorted(Tester t) {
    return t.checkExpect(this.mary.isSorted(), false)
        && t.checkExpect(this.mary.sort().isSorted(), true);
  }

  // test the method interleave for the list of Strings
  boolean testInterleave(Tester t) {
    return t.checkExpect(this.mary.interleave(this.john),
        new ConsLoString("Mary ",
            new ConsLoString("John ",
                new ConsLoString("had ",
                    new ConsLoString("is ", new ConsLoString("a ",
                        new ConsLoString("a ",
                            new ConsLoString("little ", 
                                new ConsLoString("weird ", 
                                    new ConsLoString("lamb.",
                                        new ConsLoString("dude.",new MtLoString())))))))))));
  }

  // test the method merge for the list of Strings
  boolean testMerge(Tester t) {
    return t.checkExpect(this.mary.sort().merge(this.john.sort()), new ConsLoString("a ",
        new ConsLoString("a ", new ConsLoString("dude.", new ConsLoString("had ", new ConsLoString(
            "is ", new ConsLoString("John ", new ConsLoString("lamb.", new ConsLoString("little ",
                new ConsLoString("Mary ", new ConsLoString("weird ", new MtLoString())))))))))));
  }

  // test the method reverse for the list of Strings
  boolean testReverse(Tester t) {
    return t.checkExpect(this.mary.reverse(),
        new ConsLoString("lamb.", new ConsLoString("little ", new ConsLoString("a ",
            new ConsLoString("had ", new ConsLoString("Mary ", new MtLoString()))))));
  }

  // test the method putLast for the list of Strings
  boolean testPutLast(Tester t) {
    return t.checkExpect(this.mary.putLast("last"),
        new ConsLoString("Mary ",
            new ConsLoString("had ", new ConsLoString("a ", new ConsLoString("little ",
                new ConsLoString("lamb.", new ConsLoString("last", new MtLoString())))))));
  }

  // test the method isDoubledList for the list of Strings
  boolean testIsDoubledList(Tester t) {
    return t.checkExpect(this.mary.sort().merge(this.mary.sort()).isDoubledList(), true)
        && t.checkExpect(this.mary.sort().merge(this.john.sort()).isDoubledList(), false);
  }

  // test the method isFirstEqual for the list of Strings
  boolean testIsFirstEqual(Tester t) {
    return t.checkExpect(this.mary.isFirstEqual("Mary "), true)
        && t.checkExpect(this.mary.isFirstEqual("John "), false);
  }

  // test the method isDoubledListHelper for the list of Strings
  boolean testIsDoubledListHelper(Tester t) {
    return t.checkExpect(
        (new ConsLoString("Mary", new ConsLoString("B", new ConsLoString("B", new MtLoString()))))
            .isDoubledListHelper(),
        true);
  }

  // test the method isPalindromeList for the list of Strings
  boolean testPalindromeList(Tester t) {
    return t.checkExpect(this.mary.isPalindromeList(), false) && t.checkExpect(
        new ConsLoString("B", new ConsLoString("a", new ConsLoString("B", new MtLoString())))
            .isPalindromeList(),
        true);
  }

  // test the method palidromeHelper for the list of String
  boolean testPalindromHelper(Tester t) {
    return t.checkExpect(this.mary.palindromeHelper(),
        new ConsLoString("lamb.", new ConsLoString("little ",
            new ConsLoString("a ", new ConsLoString("had ", new MtLoString())))));
  }

}
