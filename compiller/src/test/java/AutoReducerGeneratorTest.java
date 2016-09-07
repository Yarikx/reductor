import com.google.testing.compile.JavaFileObjects;
import com.yheriatovych.reductor.processor.AutoReducerProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class AutoReducerGeneratorTest {
    @Test
    public void testGeneratedReducerForNoPayloadActionHandler() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    @AutoReducer.Action(\"ACTION_1\")\n" +
                "    String uppercase(String state) {\n" +
                "        return state.toUpperCase();\n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducerImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public class FoobarReducerImpl extends FoobarReducer {\n" +
                "  @Override\n" +
                "  public String reduce(String state, Action action) {\n" +
                "    switch (action.type) {\n" +
                "      case \"ACTION_1\":\n" +
                "        return uppercase(state);\n" +
                "      default: return state;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static class ActionCreator {\n" +
                "    public static Action uppercase() {\n" +
                "      return new Action(\"ACTION_1\", null);\n" +
                "    }\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testGeneratedReducerForSinglePayloadActionHandler() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    @AutoReducer.Action(\"ACTION_1\")\n" +
                "    String append(String state, int number) {\n" +
                "        return state + number;\n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducerImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public class FoobarReducerImpl extends FoobarReducer {\n" +
                "  @Override\n" +
                "  public String reduce(String state, Action action) {\n" +
                "    switch (action.type) {\n" +
                "      case \"ACTION_1\":\n" +
                "        return append(state, (int) action.value);\n" +
                "      default: return state;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static class ActionCreator {\n" +
                "    public static Action append(int number) {\n" +
                "      return new Action(\"ACTION_1\", number);\n" +
                "    }\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testGeneratedReducerForMultiPayloadActionHandler() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    @AutoReducer.Action(\"ACTION_1\")\n" +
                "    String append(String state, int number, String suffix) {\n" +
                "        return state + number + suffix;\n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducerImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public class FoobarReducerImpl extends FoobarReducer {\n" +
                "  @Override\n" +
                "  public String reduce(String state, Action action) {\n" +
                "    switch (action.type) {\n" +
                "      case \"ACTION_1\": {\n" +
                "        Object[] args = (Object[]) action.value;\n" +
                "        return append(state, (int) args[0], (String) args[1]);\n" +
                "      }\n" +
                "      default: return state;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static class ActionCreator {\n" +
                "    public static Action append(int number, String suffix) {\n" +
                "      Object[] args = new Object[]{number, suffix};\n" +
                "      return new Action(\"ACTION_1\", args);\n" +
                "    }\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testGeneratedReducerWithMatchingConstructor() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    public FoobarReducer(int foo, String bar) {\n" +
                "        \n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducerImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public class FoobarReducerImpl extends FoobarReducer {\n" +
                "  public FoobarReducerImpl(int foo, String bar) {\n" +
                "    super(foo, bar);\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public String reduce(String state, Action action) {\n" +
                "    switch (action.type) {\n" +
                "      default: return state;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static class ActionCreator {\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testGeneratedReducerWithOnlyDefaultConstructor() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    public FoobarReducer() {\n" +
                "        \n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducerImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public class FoobarReducerImpl extends FoobarReducer {\n" +
                "  @Override\n" +
                "  public String reduce(String state, Action action) {\n" +
                "    switch (action.type) {\n" +
                "      default: return state;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static class ActionCreator {\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testGenerateReducerWithOnlyAccessibleConstructor() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import com.yheriatovych.reductor.annotations.AutoReducer;\n" +
                "\n" +
                "@AutoReducer\n" +
                "public abstract class FoobarReducer implements Reducer<String>{\n" +
                "    public FoobarReducer(int a) {\n" +
                "        \n" +
                "    }\n" +
                "\n" +
                "    private FoobarReducer(String b) {\n" +
                "        \n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducerImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "\n" +
                "public class FoobarReducerImpl extends FoobarReducer {\n" +
                "  public FoobarReducer(int a) {\n" +
                "      super(a);\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public String reduce(String state, Action action) {\n" +
                "    switch (action.type) {\n" +
                "      default: return state;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static class ActionCreator {\n" +
                " ");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoReducerProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }
}
