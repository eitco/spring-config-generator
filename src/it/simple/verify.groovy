import java.nio.file.Files

File baseDirectory = new File("$basedir");


assert new File(baseDirectory, "target/classes/de/eitco/ComponentConfiguration.class")

File generatedSourceFile = new File(baseDirectory, "target/generated-sources/annotations/de/eitco/ComponentConfiguration.java")
assert generatedSourceFile.isFile()

String content = Files.readString(generatedSourceFile.toPath())

assert content.contains("Component1.class")
assert content.contains("Component2.class")
assert content.contains("Component1_1.class")
assert content.contains("MyConfiguration.class")
assert content.contains("MyController.class")
assert content.contains("MyControllerAdvice.class")
assert content.contains("MyRestController.class")
assert content.contains("MyRestControllerAdvice.class")
assert content.contains("MyRepository.class")
assert content.contains("MyService.class")
assert content.contains("MyEndpoint.class")



