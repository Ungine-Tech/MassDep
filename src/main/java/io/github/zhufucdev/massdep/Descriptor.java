package io.github.zhufucdev.massdep;

import org.gradle.api.Project;
import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * A descriptor is a set of information that defines a package target,
 * which can be parsed using a fixed pattern
 *
 * @see Descriptor#parse(String, Project) Parsing
 */
public record Descriptor(Project project, String configuration, String domain, String company, String product, String artifact, String version) {
    private MassDepExtension extension () {
        return Plugin.depExtensionMap.get(project);
    }
    public String getGradleNotation() {
        return String.format("%s.%s.%s:%s:%s", domain(), company().toLowerCase(Locale.ROOT), product(), artifact, version);
    }

    @Override
    public String company() {
        return company.isEmpty() ? extension().getDefaultCompany().get() : company;
    }

    @Override
    public String domain() {
        return domain.isEmpty() ? extension().getDefaultDomain().get() : domain;
    }

    @Override
    public String product() {
        return product.isEmpty() ? extension().getDefaultProduct().get() : product;
    }

    /**
     * Parse a descriptor out of the pattern
     * <br>
     * The standard pattern is as follows
     * <pre>
     * [ConfigurationName>][[{Domain}].{Company}.{Product}:]{Artifact}:{Version}
     * </pre>
     * @param description The pattern
     * @return A descriptor containing pattern's information
     * @see Descriptor
     */
    public static Descriptor parse(@NotNull String description, @NotNull Project project) {
        String processed = description;
        int indexOfConfMark = description.indexOf('>');
        String configuration = "";
        boolean shouldWarn = false;
        if (indexOfConfMark >= 0) {
            processed = description.substring(indexOfConfMark + 1);
            configuration = description.substring(0, indexOfConfMark).trim();
            if (configuration.isEmpty()) {
                shouldWarn = true;
            }
        }

        String[] blocks = processed.split(":");
        String domain, company, product, artifact, version;
        switch (blocks.length) {
            case 2 -> {
                // define artifact and version only
                artifact = blocks[0].trim();
                version = blocks[1].trim();
                domain = "";
                company = "";
                product = "";
            }
            case 3 -> {
                String[] cp = blocks[0].split("\\.");
                if (cp.length == 2) {
                    domain = "";
                    company = cp[0].trim();
                    product = cp[1].trim();
                } else if (cp.length == 3) {
                    domain = cp[0].trim();
                    company = cp[1].trim();
                    product = cp[2].trim();
                } else if (cp.length > 3) {
                    int n = cp.length;
                    // use the first n - 2 selections as domain
                    StringBuilder domainBuilder = new StringBuilder(cp[0].trim());
                    for (int i = 1; i < n - 2; i ++) {
                        domainBuilder.append(".").append(cp[i].trim());
                    }
                    domain = domainBuilder.toString();
                    company = cp[n - 2];
                    product = cp[n - 1];
                } else {
                    throw new IllegalArgumentException("Expecting {Company}.{Product}, but got " + blocks[0]);
                }

                artifact = blocks[1].trim();
                version = blocks[2].trim();
            }
            default -> throw new IllegalArgumentException("Not following [{Company}.{Product}:]{Artifact}:{Version}");
        }

        if (shouldWarn) {
            Plugin.logger.warn(
                    String.format(
                            "Dependency %s.%s.%s:%s defined an empty gradle configuration",
                            domain, company, product, artifact
                    )
            );
        }

        return new Descriptor(project, configuration, domain, company, product, artifact, version);
    }
}
