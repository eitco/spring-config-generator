
[![License](https://img.shields.io/github/license/eitco/spring-config-generator.svg?style=for-the-badge)](https://opensource.org/license/mit)
[![Build status](https://img.shields.io/github/actions/workflow/status/eitco/spring-config-generator/deploy.yaml?branch=main&style=for-the-badge&logo=github)](https://github.com/eitco/spring-config-generator/actions/workflows/deploy.yaml)
[![Maven Central Version](https://img.shields.io/maven-central/v/de.eitco.cicd/spring-config-generator?style=for-the-badge&logo=apachemaven)](https://central.sonatype.com/artifact/de.eitco.cicd/spring-config-generator)

# spring-config-generator

This java annotation processor scans for spring annotations that mark a class as injectable and generates a spring configuration class containing imports for all classes marked this way.

It is integrated by adding the module as a dependency to a project:

```
<dependency>
    <groupId>de.eitco.commons</groupId>
    <artifactId>spring-config-generator</artifactId>
    <version>4.0.1</version>
</dependency> 
``` 

