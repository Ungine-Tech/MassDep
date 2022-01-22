package net.livingsky.massdep;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MassDep implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getDependencies().getExtensions().add("package", new PackageDependencyHandler(project));
    }
}
