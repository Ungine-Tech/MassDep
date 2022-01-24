package io.github.zhufucdev.massdep;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.HashMap;
import java.util.Map;

public class Plugin implements org.gradle.api.Plugin<Project> {
    public static Logger logger = Logging.getLogger(Plugin.class);

    public static Map<Project, MassDepExtension> depExtensionMap = new HashMap<>();

    @Override
    public void apply(Project project) {
        project.getDependencies().getExtensions().add("pack", new PackageDependencyHandler(project));
        MassDepExtension massDep = project.getExtensions().create("massDep", MassDepExtension.class);
        depExtensionMap.put(project, massDep);
    }
}
