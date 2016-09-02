import com.google.testing.compile.JavaFileObjects;
import com.yheriatovych.reductor.processor.CombinedStateProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class CombinedStateReducerTest {
    @Test
    public void testSimpleReducerGeneration() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    String foo();\n" +
                "    Date bar();\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import java.lang.IllegalStateException;\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "public final class FoobarReducer implements Reducer<Foobar> {\n" +
                "  private final Reducer<String> fooReducer;\n" +
                "\n" +
                "  private final Reducer<Date> barReducer;\n" +
                "\n" +
                "  private FoobarReducer(Reducer<String> fooReducer, Reducer<Date> barReducer) {\n" +
                "    this.fooReducer = fooReducer;\n" +
                "    this.barReducer = barReducer;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public Foobar reduce(Foobar state, Action action) {\n" +
                "    boolean areValuesTheSame = true;\n" +
                "    String foo = state.foo();\n" +
                "    Date bar = state.bar();\n" +
                "\n" +
                "    String fooNext = fooReducer.reduce(foo, action);\n" +
                "    areValuesTheSame &= foo == fooNext;\n" +
                "    foo = fooNext;\n" +
                "\n" +
                "    Date barNext = barReducer.reduce(bar, action);\n" +
                "    areValuesTheSame &= bar == barNext;\n" +
                "    bar = barNext;\n" +
                "    return areValuesTheSame ? state : new FoobarImpl(foo, bar);\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder() {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static class Builder {\n" +
                "    private Reducer<String> fooReducer;\n" +
                "\n" +
                "    private Reducer<Date> barReducer;\n" +
                "\n" +
                "    private Builder() {\n" +
                "    }\n" +
                "\n" +
                "    public Builder fooReducer(Reducer<String> fooReducer) {\n" +
                "      this.fooReducer = fooReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder barReducer(Reducer<Date> barReducer) {\n" +
                "      this.barReducer = barReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public FoobarReducer build() {\n" +
                "      if (fooReducer == null) {\n" +
                "        throw new IllegalStateException(\"fooReducer should not be null\");\n" +
                "      }\n" +
                "      if (barReducer == null) {\n" +
                "        throw new IllegalStateException(\"barReducer should not be null\");\n" +
                "      }\n" +
                "      return new FoobarReducer(fooReducer, barReducer);\n" +
                "    }\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .processedWith(new CombinedStateProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testBoxPrimitives() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    int foo();\n" +
                "    boolean bar();\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import java.lang.Boolean;\n" +
                "import java.lang.IllegalStateException;\n" +
                "import java.lang.Integer;\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "public final class FoobarReducer implements Reducer<Foobar> {\n" +
                "  private final Reducer<Integer> fooReducer;\n" +
                "\n" +
                "  private final Reducer<Boolean> barReducer;\n" +
                "\n" +
                "  private FoobarReducer(Reducer<Integer> fooReducer, Reducer<Boolean> barReducer) {\n" +
                "    this.fooReducer = fooReducer;\n" +
                "    this.barReducer = barReducer;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public Foobar reduce(Foobar state, Action action) {\n" +
                "    boolean areValuesTheSame = true;\n" +
                "    int foo = state.foo();\n" +
                "    boolean bar = state.bar();\n" +
                "\n" +
                "    int fooNext = fooReducer.reduce(foo, action);\n" +
                "    areValuesTheSame &= foo == fooNext;\n" +
                "    foo = fooNext;\n" +
                "\n" +
                "    boolean barNext = barReducer.reduce(bar, action);\n" +
                "    areValuesTheSame &= bar == barNext;\n" +
                "    bar = barNext;\n" +
                "    return areValuesTheSame ? state : new FoobarImpl(foo, bar);\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder() {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static class Builder {\n" +
                "    private Reducer<Integer> fooReducer;\n" +
                "\n" +
                "    private Reducer<Boolean> barReducer;\n" +
                "\n" +
                "    private Builder() {\n" +
                "    }\n" +
                "\n" +
                "    public Builder fooReducer(Reducer<Integer> fooReducer) {\n" +
                "      this.fooReducer = fooReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder barReducer(Reducer<Boolean> barReducer) {\n" +
                "      this.barReducer = barReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public FoobarReducer build() {\n" +
                "      if (fooReducer == null) {\n" +
                "        throw new IllegalStateException(\"fooReducer should not be null\");\n" +
                "      }\n" +
                "      if (barReducer == null) {\n" +
                "        throw new IllegalStateException(\"barReducer should not be null\");\n" +
                "      }\n" +
                "      return new FoobarReducer(fooReducer, barReducer);\n" +
                "    }\n" +
                "  }\n" +
                "}\n");

        assertAbout(javaSource()).that(source)
                .processedWith(new CombinedStateProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }
}
