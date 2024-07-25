
[![License](https://img.shields.io/github/license/eitco/spring-config-generator.svg?style=for-the-badge)](https://opensource.org/license/mit)
[![Build status](https://img.shields.io/github/actions/workflow/status/eitco/spring-config-generator/deploy.yaml?branch=main&style=for-the-badge&logo=github)](https://github.com/eitco/spring-config-generator/actions/workflows/deploy.yaml)
[![Maven Central Version](https://img.shields.io/maven-central/v/de.eitco.cicd/spring-config-generator?style=for-the-badge&logo=apachemaven)](https://central.sonatype.com/artifact/de.eitco.cicd/spring-config-generator)

# spring-config-generator

This java annotation processor scans for spring annotations that mark a class as a stereotype and generates a spring configuration class containing imports for all classes marked this way.

It is integrated by adding the module as a dependency to a project:

```xml
<dependency>
    <groupId>de.eitco.cicd</groupId>
    <artifactId>spring-config-generator</artifactId>
    <version>4.0.2</version>
    <optional>true</optional>
</dependency> 
``` 
The following annotations are recognized:

 * `@Component`
 * `@Service`
 * `@Repository`
 * `@Controller`
 * `@RestController`
 * `@ControllerAdvice`
 * `@RestControllerAdvice`
 * `@Configuration`
 * `@Endpoint`


The annotation processor will generate a java file that defines a spring configuration that imports all classes processed 
that are annotated with one of these annotations. The  code of the generated class will roughly look like this:

```java
package <your package name>;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import javax.annotation.processing.Generated;

@Generated("de.eitco.commons.build.essentials.config.generator.SpringConfigGenerationProcessor")
@Configuration
@Import({
        <list of annotated classes>
	})
public class <your class name> {}
```

The target package can be configured using the annotation processor parameter `generated.spring.config.package` which defaults to `de.eitco`.
The class name can be configured using the annotation processor parameter `generated.spring.config.class` defaulting to `ComponentConfiguration`.

The [integration tests](tree/main/src/it) pose a simple example.