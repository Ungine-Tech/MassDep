package io.github.zhufucdev.massdep;

import org.gradle.api.provider.Property;

abstract public class MassDepExtension {
    abstract public Property<String> getPattern();
    abstract public Property<String> getDefaultConfiguration();
    abstract public Property<String> getDefaultCompany();
    abstract public Property<String> getDefaultDomain();
    abstract public Property<String> getDefaultProduct();
    abstract public Property<String> getUser();
    abstract public Property<String> getKey();
}
