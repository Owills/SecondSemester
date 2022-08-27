import tester.*;
import java.util.function.BiFunction;
import java.util.function.Function;

interface IArith {
  <R> R accept(IArithVisitor<R> vistitor);
}

class Const implements IArith {
  double num;

  Const(double num) {
    this.num = num;
  }

  public <R> R accept(IArithVisitor<R> vistitor) {
    return vistitor.visitConst(this);
  }
}

class UnaryFormula implements IArith {
  Function<Double, Double> func;
  String name;
  IArith child;

  UnaryFormula(Function<Double, Double> func, String name, IArith child) {
    this.func = func;
    this.name = name;
    this.child = child;
  }

  public <R> R accept(IArithVisitor<R> vistitor) {
    return vistitor.visitUnaryFormula(this);
  }
}

//negates a number
class Neg implements Function<Double, Double> {
  public Double apply(Double input) {
    return input * -1;
  }
}

//takes the square root of a number
class Sqr implements Function<Double, Double> {
  public Double apply(Double input) {
    return Math.sqrt(input);
  }
}

class BinaryFormula implements IArith {
  BiFunction<Double, Double, Double> func;
  String name;
  IArith left;
  IArith right;

  BinaryFormula(BiFunction<Double, Double, Double> func, String name, IArith left, IArith right) {
    this.func = func;
    this.name = name;
    this.left = left;
    this.right = right;
  }

  public <R> R accept(IArithVisitor<R> vistitor) {
    return vistitor.visitBinaryFormula(this);
  }
}

//adds two numbers
class Plus implements BiFunction<Double, Double, Double> {
  public Double apply(Double input1, Double input2) {
    return input1 + input2;
  }
}

//subtracts two numbers
class Minus implements BiFunction<Double, Double, Double> {
  public Double apply(Double input1, Double input2) {
    return input1 - input2;
  }
}

//multiples two numbers
class Mul implements BiFunction<Double, Double, Double> {
  public Double apply(Double input1, Double input2) {
    return input1 * input2;
  }
}

//divides two numbers
class Div implements BiFunction<Double, Double, Double> {
  public Double apply(Double input1, Double input2) {
    return input1 / input2;
  }
}

interface IFunc<I, O> {
  O apply(I input);
}

interface IArithVisitor<R> extends IFunc<IArith, R> {
  R visitConst(Const c);

  R visitUnaryFormula(UnaryFormula u);

  R visitBinaryFormula(BinaryFormula b);
}

// Gets the number value of an IArith tree
class EvalVisitor implements IArithVisitor<Double> {
  public Double apply(IArith input) {
    return input.accept(this);
  }

  public Double visitConst(Const c) {
    return c.num;
  }

  public Double visitUnaryFormula(UnaryFormula u) {
    return u.func.apply(this.apply(u.child));
  }

  public Double visitBinaryFormula(BinaryFormula b) {
    return b.func.apply(this.apply(b.left), this.apply(b.right));
  }
}

// gets the fully parenthesized expression of an IArith tree
class PrintVisitor implements IArithVisitor<String> {
  public String apply(IArith input) {
    return input.accept(this);
  }

  public String visitConst(Const c) {
    return Double.toString(c.num);
  }

  public String visitUnaryFormula(UnaryFormula u) {
    return "(" + u.name + " " + this.apply(u.child) + ")";
  }

  public String visitBinaryFormula(BinaryFormula b) {
    return "(" + b.name + " " + this.apply(b.left) + " " + this.apply(b.right) + ")";
  }
}

//produces a new IArith where ever Const is doubled
class DoublerVisitor implements IArithVisitor<IArith> {
  public IArith apply(IArith input) {
    return input.accept(this);
  }

  public IArith visitConst(Const c) {
    return new Const(2 * c.num);
  }

  public IArith visitUnaryFormula(UnaryFormula u) {
    return new UnaryFormula(u.func, u.name, this.apply(u.child));
  }

  public IArith visitBinaryFormula(BinaryFormula b) {
    return new BinaryFormula(b.func, b.name, this.apply(b.left), this.apply(b.right));
  }
}

//returns true if at no point in the evaluation of an IArith there is a negative number
class NoNegativeResults implements IArithVisitor<Boolean> {
  public Boolean apply(IArith input) {
    return input.accept(this);
  }

  public Boolean visitConst(Const c) {
    return c.num >= 0;
  }

  public Boolean visitUnaryFormula(UnaryFormula u) {
    return new EvalVisitor().apply(u) >= 0 && this.apply(u.child);
  }

  public Boolean visitBinaryFormula(BinaryFormula b) {
    return new EvalVisitor().apply(b) >= 0 && this.apply(b.left) && this.apply(b.right);
  }
}

class ExamplesVisitors {
  Const num0 = new Const(0.0);

  Const num1 = new Const(1.0);
  Const num2 = new Const(2.0);
  Const num3 = new Const(3.0);
  Const num4 = new Const(4.0);

  Const num_1 = new Const(-1.0);
  Const num_2 = new Const(-2.0);
  Const num_3 = new Const(-3.0);

  BinaryFormula add12 = new BinaryFormula(new Plus(), "plus", num1, num2);
  BinaryFormula add23 = new BinaryFormula(new Plus(), "plus", num2, num3);
  BinaryFormula minus12 = new BinaryFormula(new Minus(), "minus", num1, num2);
  BinaryFormula div12 = new BinaryFormula(new Div(), "div", num1, num2);
  BinaryFormula mul12 = new BinaryFormula(new Mul(), "mul", num1, num2);

  UnaryFormula sqrt1 = new UnaryFormula(new Sqr(), "sqr", num1);
  UnaryFormula sqrt2 = new UnaryFormula(new Sqr(), "sqr", num2);

  UnaryFormula neg1 = new UnaryFormula(new Neg(), "neg", num1);
  UnaryFormula neg2 = new UnaryFormula(new Neg(), "neg", num2);

  // (1 + 2) / (-1)
  BinaryFormula complex1 = new BinaryFormula(new Div(), "div", add12, neg1);
  // (1 + 2) / (1-2)
  BinaryFormula complex2 = new BinaryFormula(new Div(), "div", add12, minus12);
  // (2 + 3) - (2-3)
  BinaryFormula complex3 = new BinaryFormula(new Minus(), "minus", add23, add23);

  // test Neg Function
  boolean testNeg(Tester t) {
    return t.checkExpect(new Neg().apply(1.0), -1.0) && t.checkExpect(new Neg().apply(-1.0), 1.0)
        && t.checkExpect(new Neg().apply(0.0), 0.0);
  }

  // test Sqr Function
  boolean testSqr(Tester t) {
    return t.checkExpect(new Sqr().apply(1.0), 1.0) && t.checkExpect(new Sqr().apply(4.0), 2.0)
        && t.checkExpect(new Sqr().apply(0.0), 0.0);
  }

  // test Plus BiFunction
  boolean testPlus(Tester t) {
    return t.checkExpect(new Plus().apply(1.0, 1.0), 2.0)
        && t.checkExpect(new Plus().apply(0.0, 0.0), 0.0)
        && t.checkExpect(new Plus().apply(-1.0, -1.0), -2.0)
        && t.checkExpect(new Plus().apply(-1.0, 1.0), 0.0);
  }

  // test minus BiFunction
  boolean testMinus(Tester t) {
    return t.checkExpect(new Minus().apply(1.0, 1.0), 0.0)
        && t.checkExpect(new Minus().apply(0.0, 0.0), 0.0)
        && t.checkExpect(new Minus().apply(-1.0, -1.0), 0.0)
        && t.checkExpect(new Minus().apply(1.0, -1.0), 2.0);
  }

  // test Div BiFunction
  boolean testDiv(Tester t) {
    return t.checkExpect(new Div().apply(1.0, 1.0), 1.0)
        && t.checkExpect(new Div().apply(0.0, 1.0), 0.0)
        && t.checkExpect(new Div().apply(-1.0, -1.0), 1.0)
        && t.checkExpect(new Div().apply(1.0, -1.0), -1.0)
        && t.checkExpect(new Div().apply(1.0, 2.0), 0.5);
  }

  // test Mul BiFunction
  boolean testMul(Tester t) {
    return t.checkExpect(new Mul().apply(1.0, 1.0), 1.0)
        && t.checkExpect(new Mul().apply(0.0, 1.0), 0.0)
        && t.checkExpect(new Mul().apply(-1.0, -1.0), 1.0)
        && t.checkExpect(new Mul().apply(1.0, -1.0), -1.0)
        && t.checkExpect(new Mul().apply(1.0, 2.0), 2.0);
  }

  // test EvalVisitor apply method
  boolean testEvalVisitorApply(Tester t) {
    return t.checkExpect(new EvalVisitor().apply(num0), 0.0)
        && t.checkExpect(new EvalVisitor().apply(num1), 1.0)
        && t.checkExpect(new EvalVisitor().apply(add12), 3.0)
        && t.checkExpect(new EvalVisitor().apply(minus12), -1.0)
        && t.checkExpect(new EvalVisitor().apply(div12), 0.5)
        && t.checkExpect(new EvalVisitor().apply(mul12), 2.0)
        && t.checkExpect(new EvalVisitor().apply(sqrt1), 1.0)
        && t.checkExpect(new EvalVisitor().apply(neg1), -1.0)
        && t.checkExpect(new EvalVisitor().apply(complex1), -3.0)
        && t.checkExpect(new EvalVisitor().apply(complex2), -3.0)
        && t.checkExpect(new EvalVisitor().apply(complex3), 0.0)
        && t.checkExpect(new EvalVisitor().apply(add23), 5.0);
  }

  // test EvalVistior visitConst method
  boolean testEvalVisitorConst(Tester t) {
    return t.checkExpect(new EvalVisitor().visitConst(num0), 0.0)
        && t.checkExpect(new EvalVisitor().visitConst(num1), 1.0)
        && t.checkExpect(new EvalVisitor().visitConst(num_1), -1.0);
  }

  // test EvalVistior visitUnaryFormula method
  boolean testEvalVisitorUnary(Tester t) {
    return t.checkExpect(new EvalVisitor().visitUnaryFormula(neg2), -2.0)
        && t.checkExpect(new EvalVisitor().visitUnaryFormula(sqrt1), 1.0)
        && t.checkExpect(new EvalVisitor().visitUnaryFormula(neg1), -1.0);
  }

  // test EvalVistior visitBinaryFormula method
  boolean testEvalVisitorBinary(Tester t) {
    return t.checkExpect(new EvalVisitor().visitBinaryFormula(add12), 3.0)
        && t.checkExpect(new EvalVisitor().visitBinaryFormula(minus12), -1.0)
        && t.checkExpect(new EvalVisitor().visitBinaryFormula(div12), 0.5)
        && t.checkExpect(new EvalVisitor().visitBinaryFormula(mul12), 2.0)
        && t.checkExpect(new EvalVisitor().visitBinaryFormula(complex1), -3.0)
        && t.checkExpect(new EvalVisitor().visitBinaryFormula(complex2), -3.0)
        && t.checkExpect(new EvalVisitor().visitBinaryFormula(complex3), 0.0)
        && t.checkExpect(new EvalVisitor().visitBinaryFormula(add23), 5.0);
  }

  // test PrintVisitor apply method
  boolean testPrintVisitorApply(Tester t) {
    return t.checkExpect(new PrintVisitor().apply(num1), "1.0")
        && t.checkExpect(new PrintVisitor().apply(num_1), "-1.0")
        && t.checkExpect(new PrintVisitor().apply(neg1), "(neg 1.0)")
        && t.checkExpect(new PrintVisitor().apply(sqrt1), "(sqr 1.0)")
        && t.checkExpect(new PrintVisitor().apply(add12), "(plus 1.0 2.0)")
        && t.checkExpect(new PrintVisitor().apply(complex1), "(div (plus 1.0 2.0) (neg 1.0))")
        && t.checkExpect(new PrintVisitor().apply(complex2),
            "(div (plus 1.0 2.0) (minus 1.0 2.0))");
  }

  // test PrintVisitor visitConst method
  boolean testPrintVisitorConst(Tester t) {
    return t.checkExpect(new PrintVisitor().visitConst(num1), "1.0")
        && t.checkExpect(new PrintVisitor().visitConst(num_1), "-1.0");
  }

  // test PrintVisitor visitUnaryFormula method
  boolean testPrintVisitorUnaryFormula(Tester t) {
    return t.checkExpect(new PrintVisitor().visitUnaryFormula(neg1), "(neg 1.0)")
        && t.checkExpect(new PrintVisitor().visitUnaryFormula(sqrt1), "(sqr 1.0)");
  }

  // test PrintVisitor visitBinaryFormula method
  boolean testPrintVisitorBinaryFormula(Tester t) {
    return t.checkExpect(new PrintVisitor().visitBinaryFormula(add12), "(plus 1.0 2.0)")
        && t.checkExpect(new PrintVisitor().visitBinaryFormula(complex1),
            "(div (plus 1.0 2.0) (neg 1.0))")
        && t.checkExpect(new PrintVisitor().visitBinaryFormula(complex2),
            "(div (plus 1.0 2.0) (minus 1.0 2.0))");
  }

  // test DoubleVisitor apply method
  boolean testDoubleVisitorApply(Tester t) {
    return t.checkExpect(new DoublerVisitor().apply(num0), num0)
        && t.checkExpect(new DoublerVisitor().apply(num1), num2)
        && t.checkExpect(new DoublerVisitor().apply(num_1), num_2)
        && t.checkExpect(new DoublerVisitor().apply(neg1), neg2)
        && t.checkExpect(new DoublerVisitor().apply(sqrt1), sqrt2)
        && t.checkExpect(new DoublerVisitor().apply(add12),
            new BinaryFormula(new Plus(), "plus", num2, num4))
        && t.checkExpect(new DoublerVisitor().apply(complex1),
            new BinaryFormula(new Div(), "div", new BinaryFormula(new Plus(), "plus", num2, num4),
                new UnaryFormula(new Neg(), "neg", num2)));
  }

  // test DoubleVisitor visitConst method
  boolean testDoubleVisitorConst(Tester t) {
    return t.checkExpect(new DoublerVisitor().visitConst(num0), num0)
        && t.checkExpect(new DoublerVisitor().visitConst(num1), num2)
        && t.checkExpect(new DoublerVisitor().visitConst(num_1), num_2);
  }

  // test DoubleVisitor visitUnaryFormula method
  boolean testDoubleVisitorUnary(Tester t) {
    return t.checkExpect(new DoublerVisitor().visitUnaryFormula(neg1), neg2)
        && t.checkExpect(new DoublerVisitor().visitUnaryFormula(sqrt1), sqrt2);
  }

  // test DoubleVisitor visitBinaryFormula method
  boolean testDoubleVisitorBinaryFormula(Tester t) {
    return t.checkExpect(new DoublerVisitor().visitBinaryFormula(add12),
        new BinaryFormula(new Plus(), "plus", num2, num4))
        && t.checkExpect(new DoublerVisitor().visitBinaryFormula(complex1),
            new BinaryFormula(new Div(), "div", new BinaryFormula(new Plus(), "plus", num2, num4),
                new UnaryFormula(new Neg(), "neg", num2)));
  }

  // test NoNegativeResults apply method
  boolean testnoNegativeResultsApply(Tester t) {
    return t.checkExpect(new NoNegativeResults().apply(num0), true)
        && t.checkExpect(new NoNegativeResults().apply(num1), true)
        && t.checkExpect(new NoNegativeResults().apply(num_1), false)
        && t.checkExpect(new NoNegativeResults().apply(neg1), false)
        && t.checkExpect(new NoNegativeResults().apply(sqrt1), true)
        && t.checkExpect(new NoNegativeResults().apply(minus12), false)
        && t.checkExpect(new NoNegativeResults().apply(complex1), false)
        && t.checkExpect(new NoNegativeResults().apply(complex2), false)
        && t.checkExpect(new NoNegativeResults().apply(complex3), true)
        && t.checkExpect(new NoNegativeResults().apply(add23), true);
  }

  // test NoNegativeResults visitConst method
  boolean testnoNegativeResultsConst(Tester t) {
    return t.checkExpect(new NoNegativeResults().visitConst(num0), true)
        && t.checkExpect(new NoNegativeResults().visitConst(num1), true)
        && t.checkExpect(new NoNegativeResults().visitConst(num_1), false);
  }

  // test NoNegativeResults visitUnaryFormula method
  boolean testNoNegativeResultsUnary(Tester t) {
    return t.checkExpect(new NoNegativeResults().visitUnaryFormula(neg1), false)
        && t.checkExpect(new NoNegativeResults().visitUnaryFormula(sqrt1), true);
  }

  // test NoNegativeResults visitBinaryFormula method
  boolean testnoNegativeResultsBinary(Tester t) {
    return t.checkExpect(new NoNegativeResults().visitBinaryFormula(minus12), false)
        && t.checkExpect(new NoNegativeResults().visitBinaryFormula(complex1), false)
        && t.checkExpect(new NoNegativeResults().visitBinaryFormula(complex2), false)
        && t.checkExpect(new NoNegativeResults().visitBinaryFormula(complex3), true)
        && t.checkExpect(new NoNegativeResults().visitBinaryFormula(add23), true);
  }
  
  //test IArith accept method for Const
  boolean testAcceptConst(Tester t) {
    return t.checkExpect(num0.accept(new EvalVisitor()), 0.0)
        && t.checkExpect(num1.accept(new EvalVisitor()), 1.0)
        && t.checkExpect(num_1.accept(new EvalVisitor()), -1.0)
        && t.checkExpect(num0.accept(new PrintVisitor()), "0.0")
        && t.checkExpect(num1.accept(new PrintVisitor()), "1.0")
        && t.checkExpect(num_1.accept(new PrintVisitor()), "-1.0")
        && t.checkExpect(num0.accept(new DoublerVisitor()), num0)
        && t.checkExpect(num1.accept(new DoublerVisitor()), num2)
        && t.checkExpect(num_1.accept(new DoublerVisitor()), num_2)
        && t.checkExpect(num0.accept(new NoNegativeResults()), true)
        && t.checkExpect(num1.accept(new NoNegativeResults()), true)
        && t.checkExpect(num_1.accept(new NoNegativeResults()), false);
  }
  
  //test IArith accept method for UnaryFormula
  boolean testAcceptUnary(Tester t) {
    return t.checkExpect(sqrt1.accept(new EvalVisitor()), 1.0)
        && t.checkExpect(neg1.accept(new EvalVisitor()), -1.0)
        && t.checkExpect(sqrt1.accept(new PrintVisitor()), "(sqr 1.0)")
        && t.checkExpect(neg1.accept(new PrintVisitor()), "(neg 1.0)")
        && t.checkExpect(sqrt1.accept(new DoublerVisitor()), sqrt2)
        && t.checkExpect(neg1.accept(new DoublerVisitor()), neg2)
        && t.checkExpect(sqrt1.accept(new NoNegativeResults()), true)
        && t.checkExpect(neg1.accept(new NoNegativeResults()), false);
  }
  
  //test IArith accept method for BinaryFormula
  boolean testAcceptBinary(Tester t) {
    return t.checkExpect(mul12.accept(new EvalVisitor()), 2.0)
        && t.checkExpect(div12.accept(new EvalVisitor()), 0.5)
        && t.checkExpect(add12.accept(new EvalVisitor()), 3.0)
        && t.checkExpect(minus12.accept(new EvalVisitor()), -1.0)
        && t.checkExpect(complex2.accept(new EvalVisitor()), -3.0)
        && t.checkExpect(add12.accept(new PrintVisitor()), "(plus 1.0 2.0)")
        && t.checkExpect(complex2.accept(new PrintVisitor()), 
            "(div (plus 1.0 2.0) (minus 1.0 2.0))")
        && t.checkExpect(add12.accept(new DoublerVisitor()), 
            new BinaryFormula(new Plus(), "plus", num2, num4))
        && t.checkExpect(complex1.accept(new DoublerVisitor()), 
            new BinaryFormula(new Div(), "div", new BinaryFormula(new Plus(), "plus", num2, num4),
                new UnaryFormula(new Neg(), "neg", num2)))
        && t.checkExpect(add12.accept(new NoNegativeResults()), true)
        && t.checkExpect(minus12.accept(new NoNegativeResults()), false)
        && t.checkExpect(complex2.accept(new NoNegativeResults()), false)
        && t.checkExpect(complex3.accept(new NoNegativeResults()), true);
  }
}
