/*
 * Copyright (c) 2019 EITCO GmbH
 * All rights reserved.
 *
 * Created on 29.08.2019
 *
 */
package de.eitco.commons.build.essentials.config.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

/**
 * This annotation processor scans for classes annotated with {@link Component} and generates a spring configuration
 * class that imports each component.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes(value = {
    "org.springframework.stereotype.Component",
    "org.springframework.web.bind.annotation.RestController",
    "org.springframework.web.bind.annotation.ControllerAdvice"
})
public class SpringConfigGenerationProcessor extends AbstractProcessor {

    private final Set<Element> elements = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        elements.addAll(roundEnv.getElementsAnnotatedWith(Component.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(RestController.class));
        elements.addAll(roundEnv.getElementsAnnotatedWith(ControllerAdvice.class));

        if (!annotations.isEmpty()) {

            return false;
        }

        if (elements.isEmpty()) {

            return false;
        }

        try {

            String targetPackage =
                processingEnv.getOptions().getOrDefault("generated.spring.config.package", "de.eitco");
            String className =
                processingEnv.getOptions().getOrDefault("generated.spring.config.class", "ComponentConfiguration");

            writeJavaFile(targetPackage, className);

        } catch (IOException e) {

            try (StringWriter out = new StringWriter()) {

                e.printStackTrace(new PrintWriter(out));

                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "unable to create source file." + e.getMessage() + "\n" + out.toString());

            } catch (IOException e1) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "unable to create source file." + e.getMessage());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "unable to log exception stack trace." + e1.getMessage());
            }
        } finally {

            elements.clear();
        }

        return false;
    }

    private void writeJavaFile(String targetPackage, String className) throws IOException {

        PrintWriter sourceWriter;
        String outDir = processingEnv.getOptions().get("generated.spring.config.dir");

        if (outDir != null) {

            List<String> parts = new ArrayList<>(Arrays.asList(targetPackage.split("\\.")));
            Path path = Paths.get(outDir, parts.toArray(new String[0]));
            parts.add(className + ".java");
            Path pathWithFile = Paths.get(outDir, parts.toArray(new String[0]));

            Files.deleteIfExists(pathWithFile);
            Files.createDirectories(path);
            Path file = Files.createFile(pathWithFile);
            BufferedWriter bufferedWriter = Files.newBufferedWriter(file, StandardOpenOption.WRITE);
            sourceWriter = new PrintWriter(bufferedWriter);
        } else {
            JavaFileObject sourceFile = processingEnv.getFiler()
                .createSourceFile(targetPackage + "." + className, elements.toArray(new Element[0]));
            sourceWriter = new PrintWriter(sourceFile.openWriter());
        }

        try {

            sourceWriter.print("package ");
            sourceWriter.print(targetPackage);
            sourceWriter.println(";");
            sourceWriter.println();

            sourceWriter.println("import org.springframework.context.annotation.Configuration;");
            sourceWriter.println("import org.springframework.context.annotation.Import;");
            sourceWriter.println("import de.eitco.commons.spring.context.GeneratedConfiguration;");
            sourceWriter.println();
            sourceWriter.println("@GeneratedConfiguration");
            sourceWriter.println("@Configuration");
            sourceWriter.println("@Import({");

            String imports = elements.stream().map(e -> "\t" + ((TypeElement) e).getQualifiedName() + ".class")
                .collect(Collectors.joining(",\n"));
            sourceWriter.print(imports);

            sourceWriter.println("})");

            sourceWriter.print("public class ");
            sourceWriter.print(className);
            sourceWriter.println(" {}");
        } finally {
            sourceWriter.close();
        }
    }
}
