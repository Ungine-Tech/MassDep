package net.livingsky.massdep;

import groovy.lang.Closure;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.internal.impldep.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.rmi.AccessException;
import java.util.HashMap;
import java.util.Map;

class CredentialRepository extends Closure<Void> {
    private final Descriptor descriptor;

    public CredentialRepository(RepositoryHandler repositories, Descriptor descriptor) {
        super(repositories);
        this.descriptor = descriptor;
    }

    @Override
    public Void call(Object... args) {
        if (args.length != 1 || !(args[0] instanceof MavenArtifactRepository maven)) {
            throw new IllegalArgumentException();
        }
        maven.setUrl(getUri(descriptor));
        return null;
    }


    private static String repositoryPattern;

    private static synchronized String getRepositoryPattern() throws IOException {
        if (repositoryPattern != null)
            return repositoryPattern;

        InputStream is = CredentialRepository.class.getClassLoader().getResourceAsStream(REPOSITORY_PATTERN_FILENAME);
        if (is == null)
            throw new AccessException(REPOSITORY_PATTERN_FILENAME);

        repositoryPattern = IOUtils.toString(is, StandardCharsets.UTF_8);
        is.close();
        return repositoryPattern;
    }

    private static final Map<Descriptor, URI> cache = new HashMap<>();

    public static URI getUri(Descriptor descriptor) {
        if (cache.containsKey(descriptor))
            return cache.get(descriptor);
        String pattern;
        try {
            pattern = getRepositoryPattern();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to define patter: IOException");
        }
        String url =
                pattern.replaceAll("\\{company}", descriptor.company())
                        .replaceAll("\\{artifact}", descriptor.artifact())
                        .replaceAll("\\{product}", descriptor.product());
        URI uri = URI.create(url);
        cache.put(descriptor, uri);
        return uri;
    }

    private static final String REPOSITORY_PATTERN_FILENAME = "repository-pattern.txt";
}