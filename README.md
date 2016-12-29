# Writing an annotation preprocessor

## 1. INTRODUCTION

The goal of this assignment is to develop a program (called **BeanGenerator**) which transforms a suitably
annotated POJO into a JavaBean.

The program takes as input java file containing a POJO and if the class contains the **@bean** annotation, a
java file is generated as output, which contains the same class transformed into a JavaBean.

In order to function as a JavaBean, a class must follow certain conventions about method naming,
construction, and behavior:

* The class instance variables should be private.
* The class must have a public default constructor without arguments.
* The class properties must be accessible using get, set and is (which can be used for boolean properties instead of get).
* The class should be serializable (it is not mandatory).

## 2. IMPLEMENTATION

The main class **BeanGenerator** works through five methods: **writeClass**, **getAnnotationInfo**, **writeFields**,
**writeConstructors** and **writeMethods**.

Initially, the program asks user the name of the java file the he/she want to convert into a JavaBean. If this
file exists, the program create a new file using a PrintWriter:

```java
Class pojo = Class.forName(nameFile);

PrintWriter writer = new PrintWriter(pojo.getSimpleName() + "Bean.java", "UTF-8");
```

After that, the program starts exploring the POJO using Java Reflecion and converting it into a JavaBean. All
the methods work almost in the same way, except for **getAnnotationInfo**. The method checks if the class
has the **@bean** annotation and returns the value of the annotation type element **serializable** (which is a
boolan). If the **@bean** annotation is not present, the programs ends and deletes the file just created. The
value of the annotation type element **serializable** determines whether the JavaBean implements
Serializable or not.

The **@bean** annotation has been implemented in the following way:
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface bean {
  boolean serializable() default true;
}
```
The other methods inspect the POJO and writes its methods, fields and constructors into the generated file
following the JavaBean conventions.

For example, the **writeFields** method writes to the generated file all the fields of the POJO, converting them
into private. Moreover, the method generates the set and the get (the is if the field is a Boolean) for each
field. This is the implementation of the writeFields method:

```java
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
```
The **writeConstructors** method writes to the generated file a public constructor without arguments.
Moreover, it writes the other constructors and their arguments present in the POJO, if there are any.
```java
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
```
Finally, the **writeMethods** method writes all the other methods and their arguments present in the POJO to
the generated java file.

## 3. DEPLOYMENT
The program can be started typing this in the command line:

```sh
java -jar BeanGenerator.jar
```

The program demands to type the name of the file that should be converted into a JavaBean, for example:

![image](https://cloud.githubusercontent.com/assets/24565161/21271295/b554f4ea-c3ba-11e6-9554-a8599977bc02.png)

As can be seen, the name of the file must be preceded by the **beangenerator** package name.

However, the input java file must be compiled and added to the jar file containing **BeanGenerator.class** and
**bean.class** to work correctly. Although this part could be tricky, the simplest way to generate the jar file
including the input desired class, the **BeanGenerator** class and the **bean** class is through an IDE.

However, this represents an example of the transformation from a POJO to a JavaBean using the
**BeanGenerator**:

![image](https://cloud.githubusercontent.com/assets/24565161/21271353/faf233c8-c3ba-11e6-96b4-353e53e89729.png)
