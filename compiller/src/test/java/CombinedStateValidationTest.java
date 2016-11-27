import com.google.testing.compile.JavaFileObjects;
import com.yheriatovych.reductor.processor.ReductorAnnotationProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class CombinedStateValidationTest {

    @Test
    public void testSimplePojoGeneration() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    int foo();\n" +
                "    String bar();\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
                "public final class FoobarImpl implements Foobar {\n" +
                "  private final int foo;\n" +
                "\n" +
                "  private final String bar;\n" +
                "\n" +
                "  public FoobarImpl(int foo, String bar) {\n" +
                "    this.foo = foo;\n" +
                "    this.bar = bar;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public int foo() {\n" +
                "    return foo;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public String bar() {\n" +
                "    return bar;\n" +
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
    public void testErrorOnVoidMethod() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    void foo();\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .processedWith(new ReductorAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("void is not allowed as return type for property method")
                .in(source)
                .onLine(7);
    }

    @Test
    public void testErrorOnClassAnnotated() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public class Foobar {\n" +
                "    int foo();\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .processedWith(new ReductorAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Only interfaces and @AutoValue classes are supported as @CombinedState")
                .in(source)
                .onLine(6);
    }

    @Test
    public void testErrorIfPropertyHasParameter() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    int foo(String a);\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .processedWith(new ReductorAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("state property accessor foo(java.lang.String) should not have any parameters")
                .in(source)
                .onLine(7);
    }
}
