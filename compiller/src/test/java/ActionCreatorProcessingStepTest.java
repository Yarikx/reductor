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
                "import java.lang.Override;\n" +
                "\n" +
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
}