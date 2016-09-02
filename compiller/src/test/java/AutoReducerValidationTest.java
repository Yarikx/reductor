import com.google.testing.compile.JavaFileObjects;
import com.yheriatovych.reductor.processor.AutoReducerProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class AutoReducerValidationTest {
    @Test
    public void testFailIfReducerIsInterace() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public interface FoobarReducer extends Reducer<String> {\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .failsToCompile()
                .withErrorContaining("You can apply AutoReducer only to classes")
                .in(source).onLine(7);
    }

    @Test
    public void testFailIfReducerIsInnerClass() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "public class Test {\n" +
                "    @AutoReducer\n" +
                "    public abstract class FoobarReducer implements Reducer<String> {\n" +
                "    }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .failsToCompile()
                .withErrorContaining("AutoReducer annotated reducers should not be inner classes. Probably 'static' modifier missing")
                .in(source).onLine(8);
    }

    @Test
    public void testCompilesIfReducerIsNestedClass() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "public class Test {\n" +
                "    @AutoReducer\n" +
                "    public static abstract class FoobarReducer implements Reducer<String> {\n" +
                "    }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .compilesWithoutWarnings();
    }

    @Test
    public void testFailIfReducerDoNotImplementReducer() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer {\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .failsToCompile()
                .withErrorContaining("test.FoobarReducer should implement Reducer interface")
                .in(source).onLine(6);
    }

    @Test
    public void testFailIfReturnTypeIsNotTheSameAsReducerTypeParameter() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    @AutoReducer.Action(\"ACTION_1\")\n" +
                "    int handleAction(String state, int number) {\n" +
                "        return number;\n" +
                "    }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .failsToCompile()
                .withErrorContaining("Method handleAction(java.lang.String,int) should return the same type as state (java.lang.String)")
                .in(source).onLine(9);
    }

    @Test
    public void testFailIfHandlerHasNoArguments() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    @AutoReducer.Action(\"ACTION_1\")\n" +
                "    String handleAction() {\n" +
                "        return \"\";\n" +
                "    }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .failsToCompile()
                .withErrorContaining("method handleAction() should have at least 1 arguments: state of type java.lang.String")
                .in(source).onLine(9);
    }

    @Test
    public void testFailIfFirstArgumentIsNotStateType() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    @AutoReducer.Action(\"ACTION_1\")\n" +
                "    String handleAction(int action) {\n" +
                "        return \"\";\n" +
                "    }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .failsToCompile()
                .withErrorContaining("First parameter action of method handleAction(int) should have the same type as state (java.lang.String)")
                .in(source).onLine(9);
    }
}
