package beangenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.System.exit;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Scanner;

/**
 * .
 * @author Daniele
 */
public class BeanGenerator {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        System.out.println("Type the name of the .java file containing a POJO that you want to convert into a JavaBean (package.class):");
        Scanner in = new Scanner(System.in);
        String nameFile = in.next();

        //beangenerator.MysteryClass
        Class pojo = Class.forName(nameFile);

        PrintWriter writer = new PrintWriter(pojo.getSimpleName() + "Bean.java", "UTF-8");

        writeClass(pojo, writer);
        writeFields(pojo, writer);
        writeConstructors(pojo, writer);
        writeMethods(pojo, writer);

        writer.println("}");
        writer.close();
        System.out.println("The POJO has been successfully converted into a JavaBean.");    
    }

    public static void writeClass(Class c, PrintWriter writer) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        writer.println(c.getPackage().toString() + ";");
        writer.println();
        if (getAnnotationInfo(c, c, writer)) {
            writer.println("import java.io.Serializable;\n");
            writer.println("public class " + c.getSimpleName() + "Bean implements Serializable{\n");
        } else {
            writer.println("public class " + c.getSimpleName() + "Bean {\n");

        }
    }

    static boolean getAnnotationInfo(AnnotatedElement o, Class c, PrintWriter writer) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Annotation[] notes = o.getAnnotations();
        if (notes.length != 0) {
            for (Annotation note : notes) {
                if (note.annotationType().getSimpleName().equals("bean")) {
                    Method[] ms = note.annotationType().getMethods();
                    for (Method m : ms) {
                        String methodName = m.getName();
                        if (methodName.equals("serializable")) {
                            if (m.invoke(note).toString().equals("true")) {
                                return true;
                            } else {
                                return false;
                            }

                        }
                    }
                }
            }
        }
        //the class don't have any annotation or don't have the @bean annotation
        writer.close();
        new File(c.getSimpleName() + "Bean.java").delete();
        System.out.println("The class needs the @bean annotation to be converted into a JavaBean!");
        exit(0);
        return false;
    }

    public static void writeFields(Class c, PrintWriter writer) {
        for (Field field : c.getDeclaredFields()) {
            writer.println("//Field with its getter and setter");
            writer.println("private " + field.getType().getName() + " " + field.getName() + ";");
            writer.println("public void set" + field.getName() + "(" + field.getType().getName() + " " + field.getName() + ") {\n"
                    + "this." + field.getName() + " = " + field.getName() + ";\n}");
            if (field.getType().getName().equals("boolean")) {
                writer.println("public " + field.getType().getName() + " is" + field.getName() + "() {\n"
                        + "return " + field.getName() + ";\n}\n");
            } else {
                writer.println("public " + field.getType().getName() + " get" + field.getName() + "() {\n"
                        + "return " + field.getName() + ";\n}\n");
            }
        }
    }

    public static void writeConstructors(Class c, PrintWriter writer) {
        writer.println("//Constructors");
        writer.println("public " + c.getSimpleName() + "Bean() {}");
        for (Constructor constructor : c.getDeclaredConstructors()) {

            String arguments = "";
            char argumentName = 'a';
            for (Class type : constructor.getParameterTypes()) {
                if (!arguments.equals("")) {
                    arguments += ", ";
                }
                arguments += type.getName() + " " + argumentName;
                argumentName++;
            }
            if (constructor.getParameterTypes().length != 0) {
                writer.println(Modifier.toString(constructor.getModifiers()) + " " + c.getSimpleName() + "Bean(" + arguments + ") {}");
            }
        }
    }

    public static void writeMethods(Class c, PrintWriter writer) {
        writer.println();
        writer.println("//Methods");
        for (Method method : c.getDeclaredMethods()) {
            String arguments = "";
            char argumentName = 'a';
            for (Class type : method.getParameterTypes()) {
                if (!arguments.equals("")) {
                    arguments += ", ";
                }
                arguments += type.getName() + " " + argumentName;
                argumentName++;
            }
            writer.println(Modifier.toString(method.getModifiers()) + " " + method.getReturnType().getName() + " " + method.getName() + "(" + arguments + ") {}");
        }
    }
}