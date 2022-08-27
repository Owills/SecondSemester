import java.util.*;
import tester.Tester;

/**
 * A class that defines a new permutation code, as well as methods for encoding
 * and decoding of the messages that use this code.
 */
class PermutationCode {
  // The original list of characters to be encoded
  ArrayList<Character> alphabet = new ArrayList<Character>(
      Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
          'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

  ArrayList<Character> code = new ArrayList<Character>(26);

  // A random number generator
  Random rand = new Random();

  // Create a new instance of the encoder/decoder with a new permutation code
  PermutationCode() {
    this.code = this.initEncoder();
  }

  // Create a new instance of the encoder/decoder with the given code
  PermutationCode(ArrayList<Character> code) {
    this.code = code;
  }

  // Initialize the encoding permutation of the characters
  ArrayList<Character> initEncoder() {
    ArrayList<Character> result = new ArrayList<Character>();
    ArrayList<Character> hash = new ArrayList<Character>();
    for (Character c : this.alphabet) {
      hash.add(c);
    }

    while (hash.size() > 0) {
      int r = this.rand.nextInt(hash.size());
      result.add(hash.remove(r));
    }
    return result;
  }

  // produce an encoded String from the given String
  String encode(String source) {
    int i;
    String result = "";
    for (i = 0; i < source.length(); i++) {
      result += this.code.get(this.alphabet.indexOf(source.charAt(i)));
    }
    return result;
  }

  // produce a decoded String from the given String
  String decode(String code) {
    int i;
    String result = "";
    for (i = 0; i < code.length(); i++) {
      result += this.alphabet.get(this.code.indexOf(code.charAt(i)));

    }
    return result;
  }
}

class ExamplesPermutation {
  ArrayList<Character> code = new ArrayList<Character>(
      Arrays.asList('k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'a', 'b', 'c', 'd', 'e', 'f',
          'g', 'h', 'i', 'j', 'v', 'u', 'x', 'w', 'z', 'y'));
  
  ArrayList<Character> code2 = new ArrayList<Character>(
      Arrays.asList('z', 'y', 'x', 'w', 'v', 'u', 't', 's', 'r', 'q', 'p', 'o', 'n', 'm', 'l', 'k',
          'j', 'i', 'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'));

  PermutationCode p1 = new PermutationCode(code);
  PermutationCode p2 = new PermutationCode(code2);
  PermutationCode p3 = new PermutationCode();

  //test encode method 
  boolean testEncode(Tester t) {
    return t.checkExpect(p1.encode("ahnaf"), "krdkp")
        && t.checkExpect(p1.encode("abcdefghijklmnopqrstuvwxyz"), "klmnopqrstabcdefghijvuxwzy")
        && t.checkExpect(p2.encode("ahnaf"), "zsmzu")
        && t.checkExpect(p2.encode("abcdefghijklmnopqrstuvwxyz"), "zyxwvutsrqponmlkjihgfedcba");
  }
  
  //test decode method 
  boolean testDecode(Tester t) {
    return t.checkExpect(p1.decode("krdkp"), "ahnaf")
        && t.checkExpect(p1.decode("klmnopqrstabcdefghijvuxwzy"), "abcdefghijklmnopqrstuvwxyz")
        && t.checkExpect(p2.decode("zsmzu"), "ahnaf")
        && t.checkExpect(p2.decode("zyxwvutsrqponmlkjihgfedcba"), "abcdefghijklmnopqrstuvwxyz");
  }
  
  //further tests for for random generated codes
  void testCode(Tester t) {
    t.checkExpect(p3.decode((p3.encode("ahnaf"))), "ahnaf");
    t.checkExpect(p3.encode((p3.decode("ahnaf"))), "ahnaf");
    t.checkExpect(p3.alphabet.size(), p3.initEncoder().size());
    
    t.checkExpect(p2.decode((p2.encode("ahnaf"))), "ahnaf");
    t.checkExpect(p2.alphabet.size(), p2.initEncoder().size());
    
    t.checkExpect(p1.decode((p1.encode("ahnaf"))), "ahnaf");
    t.checkExpect(p1.alphabet.size(), p1.initEncoder().size());
    
    PermutationCode p4 = new PermutationCode(p3.initEncoder());
    t.checkExpect(p4.decode((p4.encode("ahnaf"))), "ahnaf");
    t.checkExpect(p4.encode((p4.decode("ahnaf"))), "ahnaf");
  }
}