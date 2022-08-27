import tester.Tester;
import java.util.function.Function;
import java.util.function.BiFunction;

interface IList<T> {

  <R> IList<R> map(Function<T, R> f);

  <R> R foldr(BiFunction<T, R, R> func, R base);
}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  public <R> IList<R> map(Function<T, R> f) {
    return new ConsList<R>(f.apply(this.first), this.rest.map(f));
  }

  public <R> R foldr(BiFunction<T, R, R> func, R base) {
    return func.apply(this.first, this.rest.foldr(func, base));
  }
}

class MtList<T> implements IList<T> {

  public <R> IList<R> map(Function<T, R> f) {
    return new MtList<R>();
  }

  public <R> R foldr(BiFunction<T, R, R> func, R base) {
    return base;
  }
}

class Instructor {
  String name;
  IList<Course> courses;

  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  // produces a new list of courses by adding the given course
  void courseAdder(Course c) {
    this.courses = new ConsList<Course>(c, this.courses);
  }
  
  //checks if the given student is in two or more of this Instructor's classes
  boolean dejavu(Student s) {
    return this.courses.map(new CourseContiansFunc(s)).foldr(new AddTrue(), 0) > 1;
  }

}

class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<Student>();

    prof.courseAdder(this);
  }

  // produces a course roster with the student added
  void studentAdder(Student s) {
    this.students = new ConsList<Student>(s, this.students);
  }

}

class Student {
  String name;
  int id;
  IList<Course> courses;

  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }

  // produces a new list of courses for the student by adding the given course
  void enroll(Course c) {
    if (!new CourseContiansFunc(this).apply(c)) {
      this.courses = new ConsList<Course>(c, this.courses);
      c.studentAdder(this);
    }
  }
  
  //checks if this student shares any classes with that students
  boolean classmates(Student s) {
    return this.courses.map(new CourseContiansFunc(s)).foldr(new OrFunc(), false);
  }
}

//checks if course contains a student
class CourseContiansFunc implements Function<Course, Boolean> {
  Student student;

  CourseContiansFunc(Student student) {
    this.student = student;
  }

  public Boolean apply(Course course) {
    return course.students.map(new StudentEqualFunc(this.student)).foldr(new OrFunc(), false);
  }

}

//checks if a given student equals this student
class StudentEqualFunc implements Function<Student, Boolean> {
  Student student;

  StudentEqualFunc(Student student) {
    this.student = student;
  }

  public Boolean apply(Student student) {
    return this.student.name.equals(student.name) && this.student.id == student.id;
  }
}

//or functions
class OrFunc implements BiFunction<Boolean, Boolean, Boolean> {
  public Boolean apply(Boolean b1, Boolean b2) {
    return b1 || b2;
  }
}

//adds 1 to the given integer if the boolean is true
//used with foldr to count the number of true in a a list of booleans
class AddTrue implements BiFunction<Boolean, Integer, Integer> {
  public Integer apply(Boolean b, Integer i) {
    if (b) {
      return i + 1;
    }
    return i;
  }
}

class ExamplesRegistrar {
  Student shane;
  Student lee;
  Student lee2;
  Student jacob;
  Student pitbull;
  Student billie;

  Instructor mingle;
  Instructor nathan;
  Instructor joe;

  Course algo;
  Course datamodels;
  Course writing;
  Course AI;
  Course linear;

  IList<Course> mtCourse;
  IList<Course> loe1;
  IList<Course> loe2;
  IList<Course> loe3;

  IList<Student> mtStudent;
  IList<Student> los1;
  IList<Student> los2;
  IList<Student> los3;

  IList<Instructor> mtInstructor;
  IList<Instructor> loi1;
  IList<Instructor> loi2;
  IList<Instructor> loi3;

  void initConditions() {
    shane = new Student("Shane", 100);
    lee = new Student("Lee", 101);
    lee2 = new Student("Lee", 105);
    jacob = new Student("Jacob", 102);
    pitbull = new Student("Pitbull", 103);
    billie = new Student("Billie", 104);

    mingle = new Instructor("Mingle");
    nathan = new Instructor("Nathan");
    joe = new Instructor("Joe");

    algo = new Course("Algo", mingle);
    writing = new Course("Writing", mingle);
    
    
    datamodels = new Course("DataModels", nathan);
    AI = new Course("Artifical Intelligence", nathan);
    linear = new Course("Linear Algebra", nathan);

    mtCourse = new MtList<Course>();
    loe1 = new ConsList<Course>(AI, new ConsList<Course>(algo, new MtList<Course>()));
    loe2 = new ConsList<Course>(writing, new ConsList<Course>(datamodels, new MtList<Course>()));
    loe3 = new ConsList<Course>(datamodels, new ConsList<Course>(AI, new MtList<Course>()));

    mtStudent = new MtList<Student>();
    los1 = new ConsList<Student>(shane, new ConsList<Student>(lee, new MtList<Student>()));

    los2 = new ConsList<Student>(shane, new ConsList<Student>(billie, new MtList<Student>()));

    los3 = new ConsList<Student>(jacob, new ConsList<Student>(pitbull, new MtList<Student>()));

    mtInstructor = new MtList<Instructor>();
    loi1 = new ConsList<Instructor>(nathan, new MtList<Instructor>());
    loi2 = new ConsList<Instructor>(mingle, new MtList<Instructor>());
    loi3 = new ConsList<Instructor>(mingle,
        new ConsList<Instructor>(nathan, new MtList<Instructor>()));
  }
  
  //test enroll method in Student
  void testEnroll(Tester t) {
    initConditions();
    t.checkExpect(shane.courses, new MtList<Course>());
    
    shane.enroll(algo);
    t.checkExpect(shane.courses, new ConsList<Course>(algo, new MtList<Course>()));
    
    shane.enroll(algo);
    t.checkExpect(shane.courses, new ConsList<Course>(algo, new MtList<Course>()));
    
    shane.enroll(AI);
    t.checkExpect(shane.courses, new ConsList<Course>(AI, 
        new ConsList<Course>(algo, new MtList<Course>())));

    lee.enroll(algo);
    lee.enroll(AI);
    t.checkExpect(lee.courses,
        new ConsList<Course>(AI, new ConsList<Course>(algo, new MtList<Course>())));

  }
  
  //test classmates method in Student
  boolean testClassmates(Tester t) {
    initConditions();
    shane.enroll(writing);
    lee.enroll(writing);
    lee.enroll(AI);
    lee2.enroll(writing);
    lee2.enroll(AI);
    jacob.enroll(AI);
    return t.checkExpect(shane.classmates(lee), true)
        && t.checkExpect(shane.classmates(jacob), false)
        && t.checkExpect(pitbull.classmates(billie), false)
        && t.checkExpect(pitbull.classmates(shane), false)
        && t.checkExpect(shane.classmates(pitbull), false)
        && t.checkExpect(lee.classmates(lee), true)
        && t.checkExpect(lee.classmates(lee2), true)
        && t.checkExpect(lee.classmates(jacob), true);
  }
  
  //test dejavu method in Instructor
  boolean testDejavu(Tester t) {
    initConditions();
    shane.enroll(algo);
    
    jacob.enroll(AI);
    jacob.enroll(datamodels);
    
    billie.enroll(AI);
    billie.enroll(datamodels);
    billie.enroll(linear);
    
    lee.enroll(AI);
    lee2.enroll(datamodels);
    lee2.enroll(algo);
    
    return t.checkExpect(nathan.dejavu(jacob), true)
        && t.checkExpect(nathan.dejavu(shane), false)
        && t.checkExpect(mingle.dejavu(shane), false)
        && t.checkExpect(mingle.dejavu(pitbull), false)
        && t.checkExpect(nathan.dejavu(billie), true)
        && t.checkExpect(nathan.dejavu(lee), false)
        && t.checkExpect(nathan.dejavu(lee2), false)
        && t.checkExpect(mingle.dejavu(lee2), false);
  }
  
  //test courseAdder in Instructor
  void testCourseAdder(Tester t) {
    initConditions();
    t.checkExpect(mingle.courses, new ConsList<Course>(writing, 
        new ConsList<Course>(algo, 
            new MtList<Course>())));
    mingle.courseAdder(AI);
    t.checkExpect(mingle.courses, new ConsList<Course>(AI, 
        new ConsList<Course>(writing, 
            new ConsList<Course>(algo, 
                new MtList<Course>()))));
    t.checkExpect(joe.courses, new MtList<Course>());
    joe.courseAdder(AI);
    t.checkExpect(joe.courses, new ConsList<Course>(AI,  new MtList<Course>()));
  }
  
  //test studentAdder in Course
  void testStudentAdder(Tester t) {
    initConditions();
    t.checkExpect(algo.students, new MtList<Student>());
    t.checkExpect(writing.students, new MtList<Student>());
    
    shane.enroll(algo);
    t.checkExpect(algo.students, new ConsList<Student>(shane,new MtList<Student>()));
    shane.enroll(algo);
    t.checkExpect(algo.students, new ConsList<Student>(shane,new MtList<Student>()));
    
    lee2.enroll(datamodels);
    lee2.enroll(algo);
    
    t.checkExpect(datamodels.students, new ConsList<Student>(lee2,new MtList<Student>()));
    t.checkExpect(algo.students, new ConsList<Student>(lee2,
        new ConsList<Student>(shane,new MtList<Student>())));
  }
  
  //tests CourseContiansFunc
  void testCourseContiansFunc(Tester t) {
    initConditions();
    t.checkExpect(new CourseContiansFunc(shane).apply(algo), false);
    t.checkExpect(new CourseContiansFunc(shane).apply(AI), false);
    shane.enroll(algo);
    t.checkExpect(new CourseContiansFunc(shane).apply(algo), true);
    t.checkExpect(new CourseContiansFunc(lee).apply(algo), false);
    t.checkExpect(new CourseContiansFunc(shane).apply(AI), false);
    
    lee.enroll(AI);
    lee2.enroll(algo);
    t.checkExpect(new CourseContiansFunc(lee).apply(algo), false);
    t.checkExpect(new CourseContiansFunc(lee).apply(AI), true);
    t.checkExpect(new CourseContiansFunc(lee2).apply(algo), true);
    t.checkExpect(new CourseContiansFunc(lee2).apply(AI), false);
  }
  
  //tests StudentEqualFunc
  boolean testStudentEqualFunc(Tester t) {
    return t.checkExpect(new StudentEqualFunc(shane).apply(jacob), false)
        && t.checkExpect(new StudentEqualFunc(shane).apply(shane), true)
        && t.checkExpect(new StudentEqualFunc(lee).apply(lee2), false);
  }
  
  //tests OrFunc
  boolean testOrFunc(Tester t) {
    return t.checkExpect(new OrFunc().apply(true, true), true)
        && t.checkExpect(new OrFunc().apply(false, true), true)
        && t.checkExpect(new OrFunc().apply(true, false), true)
        && t.checkExpect(new OrFunc().apply(false, false), false);
  }
  
  //tests AddTrue
  boolean testAddTrue(Tester t) {
    return t.checkExpect(new AddTrue().apply(true, 0), 1)
        && t.checkExpect(new AddTrue().apply(true, 1), 2)
        && t.checkExpect(new AddTrue().apply(false, 0), 0)
        && t.checkExpect(new AddTrue().apply(false, 1), 1);
  }
}
