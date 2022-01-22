package net.livingsky.massdep;

import org.gradle.api.Project;

public class Plugin implements org.gradle.api.Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getDependencies().getExtensions().add("pack", new PackageDependencyHandler(project));
    }
}
