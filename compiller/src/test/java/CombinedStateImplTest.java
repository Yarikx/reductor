import com.google.testing.compile.JavaFileObjects;
import com.yheriatovych.reductor.processor.CombinedStateProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class CombinedStateImplTest {

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

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarImpl", "package test;\n" +
                "\n" +
                "import java.lang.Override;\n" +
                "import java.lang.String;\n" +
                "\n" +
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
                .processedWith(new CombinedStateProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testDoNotProcessStaticMethods() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    int foo();\n" +
                "    static String bar() {\n" +
                "        return \"bar\";\n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarImpl", "package test;\n" +
                "\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "public final class FoobarImpl implements Foobar {\n" +
                "  private final int foo;\n" +
                "\n" +
                "  public FoobarImpl(int foo) {\n" +
                "    this.foo = foo;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public int foo() {\n" +
                "    return foo;\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .processedWith(new CombinedStateProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }

    @Test
    public void testDoNotProcessDefaultMethods() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.annotations.CombinedState;\n" +
                "\n" +
                "@CombinedState\n" +
                "public interface Foobar {\n" +
                "    int foo();\n" +
                "    default String bar() {\n" +
                "        return \"bar\" + foo();\n" +
                "    }\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.FoobarImpl", "package test;\n" +
                "\n" +
                "import java.lang.Override;\n" +
                "\n" +
                "public final class FoobarImpl implements Foobar {\n" +
                "  private final int foo;\n" +
                "\n" +
                "  public FoobarImpl(int foo) {\n" +
                "    this.foo = foo;\n" +
                "  }\n" +
                "\n" +
                "  @Override\n" +
                "  public int foo() {\n" +
                "    return foo;\n" +
                "  }\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .processedWith(new CombinedStateProcessor())
                .compilesWithoutWarnings()
                .and()
                .generatesSources(generatedPojo);
    }
}
