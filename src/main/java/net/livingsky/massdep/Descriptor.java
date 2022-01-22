package net.livingsky.massdep;

import org.gradle.internal.impldep.org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * A descriptor is a set of information that defines a package target,
 * which can be parsed using a fixed pattern
 *
 * @see Descriptor#parse(String) Parsing
 */
public record Descriptor(String domain, String company, String product, String artifact, String version) {
    public String getGradleNotation() {
        return String.format("%s.%s.%s:%s:%s", domain, company.toLowerCase(Locale.ROOT), product, artifact, version);
    }

    public static final String DEFAULT_DOMAIN = "net";
    public static final String DEFAULT_COMPANY = "LivingSky";
    public static final String DEFAULT_ARTIFACT = "api";

    /**
     * Parse a descriptor out of the pattern
     * <br>
     * The standard pattern is as follows
     * <pre>
     * [{Company}.{Product}:]{Artifact}:{Version}
     * </pre>
     * @param description The pattern
     * @return A descriptor containing pattern's information
     * @see Descriptor
     */
    public static Descriptor parse(@NotNull String description) {
        String[] blocks = description.split(":");
        String domain, company, product, artifact, version;
        switch (blocks.length) {
            case 2 -> {
                // define product and version only
                product = blocks[0];
                version = blocks[1];
                domain = DEFAULT_DOMAIN;
                company = DEFAULT_COMPANY;
                artifact = DEFAULT_ARTIFACT;
            }
            case 3 -> {
                String[] cp = blocks[0].split("\\.");
                if (cp.length == 2) {
                    domain = DEFAULT_DOMAIN;
                    company = cp[0];
                    product = cp[1];
                } else if (cp.length == 3) {
                    domain = cp[0];
                    company = cp[1];
                    product = cp[2];
                } else {
                    throw new IllegalArgumentException("Expecting {Company}.{Product}, but got " + blocks[0]);
                }

                artifact = blocks[1];
                version = blocks[2];
            }
            default -> throw new IllegalArgumentException("Not following [{Company}.{Product}:]{Artifact}:{Version}");
        }
        return new Descriptor(domain, company, product, artifact, version);
    }
}
