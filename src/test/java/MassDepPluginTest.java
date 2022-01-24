import groovy.lang.Closure;
import io.github.zhufucdev.massdep.Descriptor;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class MassDepPluginTest {
    public static Project project;

    @BeforeAll
    static void init() {
        project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("io.github.zhufucdev.massdep");

        Object closure = project.getDependencies().getExtensions().findByName("pack");
        Assertions.assertInstanceOf(Closure.class, closure);
    }

    @Test
    public void testDescriptor() {
        Descriptor descriptor = Descriptor.parse("test>net.example.test:testing:1.0.0", project);
        Assertions.assertEquals("net.example.test:testing:1.0.0", descriptor.getGradleNotation());
        Assertions.assertEquals("test", descriptor.configuration());
    }

    @Test
    public void addDependency() {
        ((Closure<?>) project.getDependencies().getExtensions().findByName("pack"))
                .call("LivingSky.api:base:1.0.4");
    }

    @Test
    public void addRepository() {
        Stream<ArtifactRepository> repositories =
                project.getRepositories().stream().filter(p -> p instanceof MavenArtifactRepository);
        Assertions.assertEquals(1, repositories.count());
        Assertions.assertEquals("https://maven.pkg.github.com/ungine-tech/LivingSky_base-api",
                ((MavenArtifactRepository) repositories.findFirst().get()).getUrl().toString());
    }
}
