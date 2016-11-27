import com.google.testing.compile.JavaFileObjects;
import com.yheriatovych.reductor.processor.ReductorAnnotationProcessor;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class ActionCreatorProcessingStepTest {
    @Test
    public void testGeneratedReducerForNoPayloadActionHandler() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.annotations.ActionCreator;\n" +
                "\n" +
                "@ActionCreator\n" +
                "public interface Foobar {\n" +
                "    @ActionCreator.Action(\"foobar\")\n" +
                "    Action foobar();\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.Foobar_AutoImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
                "public final class Foobar_AutoImpl implements Foobar {\n" +
                "  @Override\n" +
                "  public Action foobar() {\n" +
                "    return Action.create(\"foobar\");\n" +
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
    public void testGeneratedReducerForSinglePayloadActionHandler() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.annotations.ActionCreator;\n" +
                "\n" +
                "@ActionCreator\n" +
                "public interface Foobar {\n" +
                "    @ActionCreator.Action(\"foobar\")\n" +
                "    Action foobar(int v);\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.Foobar_AutoImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
                "public final class Foobar_AutoImpl implements Foobar {\n" +
                "  @Override\n" +
                "  public Action foobar(int v) {\n" +
                "    return Action.create(\"foobar\", v);\n" +
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
    public void testGeneratedReducerForMultiplePayloadsActionHandler() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.annotations.ActionCreator;\n" +
                "\n" +
                "@ActionCreator\n" +
                "public interface Foobar {\n" +
                "    @ActionCreator.Action(\"foobar\")\n" +
                "    Action foobar(int v, String v2);\n" +
                "}");

        JavaFileObject generatedPojo = JavaFileObjects.forSourceString("test.Foobar_AutoImpl", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import javax.annotation.Generated;\n" +
                "\n" +
                "@Generated(\"com.yheriatovych.reductor.processor.ReductorAnnotationProcessor\")\n" +
                "public final class Foobar_AutoImpl implements Foobar {\n" +
                "  @Override\n" +
                "  public Action foobar(int v, String v2) {\n" +
                "    return Action.create(\"foobar\", v, v2);\n" +
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
    public void testValidateCreatorIsInterface() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.annotations.ActionCreator;\n" +
                "\n" +
                "@ActionCreator\n" +
                "public abstract class Foobar {\n" +
                "    @ActionCreator.Action(\"foobar\")\n" +
                "    Action foobar();\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("test.Foobar annotated with @ActionCreator should be interface")
                .in(source)
                .onLine(7);
    }

    @Test
    public void testActionShouldReturnAction() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.annotations.ActionCreator;\n" +
                "\n" +
                "@ActionCreator\n" +
                "public interface Foobar {\n" +
                "    @ActionCreator.Action(\"foobar\")\n" +
                "    void foobar();\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Action creator foobar() should return Action")
                .in(source)
                .onLine(9);
    }

    @Test
    public void testMethodsShouldBeAnnotated() {
        JavaFileObject source = JavaFileObjects.forSourceString("test.Foobar", "package test;\n" +
                "\n" +
                "import com.yheriatovych.reductor.Action;\n" +
                "import com.yheriatovych.reductor.annotations.ActionCreator;\n" +
                "\n" +
                "@ActionCreator\n" +
                "public interface Foobar {\n" +
                "    Action foobar();\n" +
                "}");

        assertAbout(javaSource()).that(source)
                .withCompilerOptions("-Xlint:-processing")
                .processedWith(new ReductorAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Action creator foobar() should be annotated with @com.yheriatovych.reductor.annotations.ActionCreator.Action")
                .in(source)
                .onLine(8);
    }
}