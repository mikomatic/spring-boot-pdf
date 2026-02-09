package com.example.thymeleafpdfdemo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.context.Context;

@SpringBootTest
public class RendererServiceTest {

  @Autowired private RendererService rendererService;

  @Test
  void testRenderFullTemplateToTargetFolder() throws IOException {
    // Appel de la méthode à tester
    String content = rendererService.parseThymeleafTemplate("demo", new Context());

    String filename = "test-playwright-" + UUID.randomUUID() + ".html";

    Path outputPath = new File("target", filename).toPath();
    Files.writeString(
        outputPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }
}
