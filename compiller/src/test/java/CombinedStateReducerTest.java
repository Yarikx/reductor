import com.google.auto.value.processor.AutoValueProcessor;
import com.google.testing.compile.JavaFileObjects;
import com.yheriatovych.reductor.processor.ReductorAnnotationProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class CombinedStateReducerTest {
    @Test
    public void testEmptyReducerGeneration() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
                "public final class FoobarReducer implements Reducer<Foobar> {\n" +
                "  private FoobarReducer() {\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public Foobar reduce(Foobar state, Action action) {\n" +
                "\n" +
                "    if (state != null) {\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    //If all values are the same there is no need to create an object\n" +
                "    if (state != null) {\n" +
                "      return state;\n" +
                "    } else {\n" +
                "      return new FoobarImpl();\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder() {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static class Builder {\n" +
                "    private Builder() {\n" +
                "    }\n" +
                "\n" +
                "    public FoobarReducer build() {\n" +
                "      return new FoobarReducer();\n" +
                "    }\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

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
                "import java.util.Date;\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
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
                "    String foo = null;\n" +
                "    Date bar = null;\n" +
                "\n" +
                "    if (state != null) {\n" +
                "      foo = state.foo();\n" +
                "      bar = state.bar();\n" +
                "    }\n" +
                "\n" +
                "    String fooNext = fooReducer.reduce(foo, action);\n" +
                "    Date barNext = barReducer.reduce(bar, action);\n" +
                "\n" +
                "    //If all values are the same there is no need to create an object\n" +
                "    if (state != null\n" +
                "     && foo == fooNext\n" +
                "     && bar == barNext) {\n" +
                "      return state;\n" +
                "    } else {\n" +
                "      return new FoobarImpl(fooNext, barNext);\n" +
                "    }\n" +
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
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
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
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
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
                "    Integer foo = null;\n" +
                "    Boolean bar = null;\n" +
                "\n" +
                "    if (state != null) {\n" +
                "      foo = state.foo();\n" +
                "      bar = state.bar();\n" +
                "    }\n" +
                "\n" +
                "    Integer fooNext = fooReducer.reduce(foo, action);\n" +
                "    Boolean barNext = barReducer.reduce(bar, action);\n" +
                "\n" +
                "    //If all values are the same there is no need to create an object\n" +
                "    if (state != null\n" +
                "     && foo == fooNext\n" +
                "     && bar == barNext) {\n" +
                "      return state;\n" +
                "    } else {\n" +
                "      return new FoobarImpl(fooNext, barNext);\n" +
                "    }\n" +
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
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testAutoValueReducerGeneration() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.google.auto.value.AutoValue;\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@CombinedState\n" +
                "@AutoValue\n" +
                "public abstract class Foobar {\n" +
                "    abstract String foo();\n" +
                "    abstract Date bar();\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import java.util.Date;\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
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
                "    String foo = null;\n" +
                "    Date bar = null;\n" +
                "\n" +
                "    if (state != null) {\n" +
                "      foo = state.foo();\n" +
                "      bar = state.bar();\n" +
                "    }\n" +
                "\n" +
                "    String fooNext = fooReducer.reduce(foo, action);\n" +
                "    Date barNext = barReducer.reduce(bar, action);\n" +
                "\n" +
                "    //If all values are the same there is no need to create an object\n" +
                "    if (state != null\n" +
                "     && foo == fooNext\n" +
                "     && bar == barNext) {\n" +
                "      return state;\n" +
                "    } else {\n" +
                "      return new AutoValue_Foobar(fooNext, barNext);\n" +
                "    }\n" +
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
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new AutoValueProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testAutoValueNoReducerGeneration() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.google.auto.value.AutoValue;\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@CombinedState\n" +
                "@AutoValue\n" +
                "public abstract class Foobar {\n" +
                "    abstract String foo();\n" +
                "    abstract Date bar();\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
                .compilesWithoutWarnings();
    }

    @Test
    public void testInitialValues() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    int intValue();\n" +
                "    double doubleValue();\n" +
                "    boolean booleanValue();\n" +
                "    char charValue();\n" +
                "    Object objectValue();\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarReducer", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.Reducer;\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
                "public final class FoobarReducer implements Reducer<Foobar> {\n" +
                "  private final Reducer<Integer> intValueReducer;\n" +
                "\n" +
                "  private final Reducer<Double> doubleValueReducer;\n" +
                "\n" +
                "  private final Reducer<Boolean> booleanValueReducer;\n" +
                "\n" +
                "  private final Reducer<Character> charValueReducer;\n" +
                "\n" +
                "  private final Reducer<Object> objectValueReducer;\n" +
                "\n" +
                "  private FoobarReducer(Reducer<Integer> intValueReducer, Reducer<Double> doubleValueReducer, Reducer<Boolean> booleanValueReducer, Reducer<Character> charValueReducer, Reducer<Object> objectValueReducer) {\n" +
                "    this.intValueReducer = intValueReducer;\n" +
                "    this.doubleValueReducer = doubleValueReducer;\n" +
                "    this.booleanValueReducer = booleanValueReducer;\n" +
                "    this.charValueReducer = charValueReducer;\n" +
                "    this.objectValueReducer = objectValueReducer;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public Foobar reduce(Foobar state, Action action) {\n" +
                "    Integer intValue = null;\n" +
                "    Double doubleValue = null;\n" +
                "    Boolean booleanValue = null;\n" +
                "    Character charValue = null;\n" +
                "    Object objectValue = null;\n" +
                "\n" +
                "    if (state != null) {\n" +
                "      intValue = state.intValue();\n" +
                "      doubleValue = state.doubleValue();\n" +
                "      booleanValue = state.booleanValue();\n" +
                "      charValue = state.charValue();\n" +
                "      objectValue = state.objectValue();\n" +
                "    }\n" +
                "\n" +
                "    Integer intValueNext = intValueReducer.reduce(intValue, action);\n" +
                "    Double doubleValueNext = doubleValueReducer.reduce(doubleValue, action);\n" +
                "    Boolean booleanValueNext = booleanValueReducer.reduce(booleanValue, action);\n" +
                "    Character charValueNext = charValueReducer.reduce(charValue, action);\n" +
                "    Object objectValueNext = objectValueReducer.reduce(objectValue, action);\n" +
                "\n" +
                "    //If all values are the same there is no need to create an object\n" +
                "    if (state != null\n" +
                "     && intValue == intValueNext\n" +
                "     && doubleValue == doubleValueNext\n" +
                "     && booleanValue == booleanValueNext\n" +
                "     && charValue == charValueNext\n" +
                "     && objectValue == objectValueNext) {\n" +
                "      return state;\n" +
                "    } else {\n" +
                "      return new FoobarImpl(intValueNext, doubleValueNext, booleanValueNext, charValueNext, objectValueNext);\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  public static Builder builder() {\n" +
                "    return new Builder();\n" +
                "  }\n" +
                "\n" +
                "  public static class Builder {\n" +
                "    private Reducer<Integer> intValueReducer;\n" +
                "\n" +
                "    private Reducer<Double> doubleValueReducer;\n" +
                "\n" +
                "    private Reducer<Boolean> booleanValueReducer;\n" +
                "\n" +
                "    private Reducer<Character> charValueReducer;\n" +
                "\n" +
                "    private Reducer<Object> objectValueReducer;\n" +
                "\n" +
                "    private Builder() {\n" +
                "    }\n" +
                "\n" +
                "    public Builder intValueReducer(Reducer<Integer> intValueReducer) {\n" +
                "      this.intValueReducer = intValueReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder doubleValueReducer(Reducer<Double> doubleValueReducer) {\n" +
                "      this.doubleValueReducer = doubleValueReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder booleanValueReducer(Reducer<Boolean> booleanValueReducer) {\n" +
                "      this.booleanValueReducer = booleanValueReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder charValueReducer(Reducer<Character> charValueReducer) {\n" +
                "      this.charValueReducer = charValueReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public Builder objectValueReducer(Reducer<Object> objectValueReducer) {\n" +
                "      this.objectValueReducer = objectValueReducer;\n" +
                "      return this;\n" +
                "    }\n" +
                "\n" +
                "    public FoobarReducer build() {\n" +
                "      if (intValueReducer == null) {\n" +
                "        throw new IllegalStateException(\"intValueReducer should not be null\");\n" +
                "      }\n" +
                "      if (doubleValueReducer == null) {\n" +
                "        throw new IllegalStateException(\"doubleValueReducer should not be null\");\n" +
                "      }\n" +
                "      if (booleanValueReducer == null) {\n" +
                "        throw new IllegalStateException(\"booleanValueReducer should not be null\");\n" +
                "      }\n" +
                "      if (charValueReducer == null) {\n" +
                "        throw new IllegalStateException(\"charValueReducer should not be null\");\n" +
                "      }\n" +
                "      if (objectValueReducer == null) {\n" +
                "        throw new IllegalStateException(\"objectValueReducer should not be null\");\n" +
                "      }\n" +
                "      return new FoobarReducer(intValueReducer, doubleValueReducer, booleanValueReducer, charValueReducer, objectValueReducer);\n" +
                "    }\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }
}
