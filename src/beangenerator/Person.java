package beangenerator;

@bean
public class Person {

    public String Name;
    private static final int Age = 30;
    boolean Married;

    public Person(String lastname, String child, char character) {
    }

    @Override
    public String toString() {
        return "Hello!";
    }

    public void method1(String lastname, int b) {
    }

    private int calc(int x, int y) {
        return 2;
    }

    public void method2() {
    }

    public void method3() {
        System.out.println("method3");
    }

    public void method4() {
    }
}