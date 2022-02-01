package io.github.zhufucdev.massdep;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

class CredentialRepository extends Closure<Void> {
    private final Descriptor descriptor;
    private final Project project;

    public CredentialRepository(Object owner, Project project, Descriptor descriptor) {
        super(owner);
        this.project = project;
        this.descriptor = descriptor;
    }

    @Override
    public Void call(Object... args) {
        if (!(getDelegate() instanceof MavenArtifactRepository)) {
            throw new IllegalArgumentException();
        }
        MavenArtifactRepository maven = (MavenArtifactRepository) getDelegate();
        maven.setUrl(getUri(descriptor, project));

        String username = "", key = "";
        MassDepExtension extension = Plugin.depExtensionMap.get(project);
        if (extension.getKey().isPresent() && extension.getUser().isPresent()) {
            username = extension.getUser().get();
            key = extension.getKey().get();
        } else if (project.findProperty("gpr.user") != null && project.findProperty("gpr.key") != null) {
            username = (String) project.property("gpr.user");
            key = (String) project.property("gpr.key");
        }

        if (!username.isEmpty()) {
            String finalUsername = username;
            String finalKey = key;
            maven.credentials(c -> {
                c.setUsername(finalUsername);
                c.setPassword(finalKey);
            });
        }
        return null;
    }

    private static synchronized String getRepositoryPattern(Project project) {
        MassDepExtension extension = Plugin.depExtensionMap.get(project);
        String pattern = extension.getPattern().getOrNull();
        if (pattern == null) {
            throw new IllegalStateException("massDep->pattern isn't defined");
        }
        return pattern;
    }

    private static final Map<Descriptor, URI> cache = new HashMap<>();

    public static URI getUri(Descriptor descriptor, Project project) {
        if (cache.containsKey(descriptor))
            return cache.get(descriptor);
        String pattern = getRepositoryPattern(project);
        String url =
                pattern.replaceAll("\\{domain}", descriptor.getDomain())
                        .replaceAll("\\{company}", descriptor.getCompany())
                        .replaceAll("\\{artifact}", descriptor.getArtifact())
                        .replaceAll("\\{product}", descriptor.getProduct());
        URI uri = URI.create(url);
        cache.put(descriptor, uri);
        return uri;
    }
}