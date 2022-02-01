# MassDep
Deploy mass depositories easily and consistently across projects
# Scenario
Before knowing how to use this plugin, you should have an understanding of
its scenes to be used.

That's, imagine you are manging a grand project with multiple modules deployed
on a private maven repository, each of which may be dependent on others.
You may declare the maven repositories one by one, or better, write a small function, ex.
```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/example/Example_base-api")
        credentials {
            username = project.findProperty("gpr.user") ?: GIT_USERNAME
            password = project.findProperty("gpr.key") ?: GIT_TOKEN
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/example/Example_ui-api")
        credentials {
            username = project.findProperty("gpr.user") ?: GIT_USERNAME
            password = project.findProperty("gpr.key") ?: GIT_TOKEN
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/example/Example_io-sdk")
        credentials {
            username = project.findProperty("gpr.user") ?: GIT_USERNAME
            password = project.findProperty("gpr.key") ?: GIT_TOKEN
        }
    }
    // ...
}
// otherwise
def addRepository(String name) {
    respositories {
        maven {
            url = uri("https://maven.pkg.github.com/example/Example_${name}")
            credentials {
                username = project.findProperty("gpr.user") ?: GIT_USERNAME
                password = project.findProperty("gpr.key") ?: GIT_TOKEN
            }
        }
    }
}
addRepository("base-api")
// ...

// then add corresponding dependencies
dependencies {
    implementation "com.example.api:base:1.0.0"
    implementation "com.example.sdk:io:1.0.0"
    // ...
}
```

Perhaps you see the `addRepository` method is already convenient, but it can be tiring
to copy it to other repositories, in this example, `base-api`, `io-sdk` and `ui-api`, let
alone of consistence and maintaining afterwards.

# Usage
In the previous example, it's easy to see that, the gradle dependency notation, such as `com.example.api:base:1.0.0`,
shares the same pattern as its maven repository, `https://maven.pkg.github.com/example/Example_base-api`.
## Basics
Let's sort out this similarity first.
```groovy
plugins {
    id 'io.github.zhufucdev.massdep' version '1.2'
}

massDep {
    pattern = "https://maven.pkg.github.com/example/{company}_{artifact}-{product}"
}
```
Next, it's so easy to define the desired dependencies.
```groovy
dependencies {
    pack 'com.Example.api:base:1.0.0'
    pack 'com.Example.api:ui:1.0.0'
    pack 'com.Example.sdk:io:1.0.0'
}
```
The company name is automatically lowercased.

## Simplification
You can take a step further by defining the **default** company, domain and product name.
```groovy
massDep {
    pattern = "https://maven.pkg.github.com/example/{company}_{artifact}-{product}"
    defaultCompany = "Example"
    defaultDomain = "com"
    defaultProduct = "api"
}

dependencies {
    pack 'base:1.0.0'
    pack 'ui:1.0.0'
    pack '.sdk:io:1.0.0'
}
```

If you don't need to distinguish the product name, you can **merge** the other selections into the pattern.
```groovy
massDep {
    pattern = "https://maven.pkg.github.com/example/Example_{artifact}-api"
}

dependencies {
    pack 'com.example.api:base:1.0.0'
    pack 'com.example.api:ui:1.0.0'
}
```

## Credentials
MassDep will look for `key` and `user` properties in the `massDep` extension first.
```groovy
massDep {
    pattern = "https://maven.pkg.github.com/example/Example_{artifact}-api"
    user = "some string"
    key = "some string"
}
```
If not found, properties under the root project will be combed through for `gpr.user` and `gpr.key`, meaning
you can write them in the **gradle.properties** file for convenience in VC.
```properties
gpr.user=some string
gpr.key=some string
```

## Dependency Configurations
The default configuration is `implementation`. If you want to declare a dependency with a
different config, use the `>` symbol.
```groovy
pack 'compileOnly>com.example.api:base:1.0.0'
```
In addition, you can change the default configuration by changing the plugin configuration.
```groovy
massDep {
    pattern = "https://maven.pkg.github.com/example/.."
    defaultConfiguration = 'classpath'
}
```