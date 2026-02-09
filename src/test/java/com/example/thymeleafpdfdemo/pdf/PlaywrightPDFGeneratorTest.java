package com.example.thymeleafpdfdemo.pdf;

import static org.junit.jupiter.api.Assertions.*;

import com.example.thymeleafpdfdemo.config.AsyncConfig;
import com.example.thymeleafpdfdemo.service.RendererService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.thymeleaf.context.Context;

@SpringBootTest
@Import(AsyncConfig.class)
@Slf4j
public class PlaywrightPDFGeneratorTest {

  @Autowired private PlaywrightPDFGenerator pdfGenerator;

  @Autowired private RendererService rendererService;

  @Autowired private Executor taskExecutor; // Inject your custom executor

  @Test
  void testRenderMultiplePDFsPerformance() throws Exception {
    int count = 10;
    String htmlContent = rendererService.parseThymeleafTemplate("full", new Context());
    long start = System.currentTimeMillis();
    for (int i = 0; i < count; i++) {
      byte[] pdf = pdfGenerator.generatePdf(htmlContent);
      assertNotNull(pdf);
      assertTrue(pdf.length > 1000, "Le PDF généré est trop petit");
      // Écriture du PDF dans le dossier target
      String filename = "test-playwright-" + i + "-" + UUID.randomUUID() + ".pdf";
      Path outputPath = new File("target", filename).toPath();
      Files.write(outputPath, pdf, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      assertTrue(Files.exists(outputPath), "Le fichier PDF n'a pas été créé");
      assertTrue(Files.size(outputPath) > 1000, "Le fichier PDF généré est trop petit");
    }
    long end = System.currentTimeMillis();
    long total = end - start;
    System.out.println("Temps total pour générer " + count + " PDF : " + total + " ms");
    System.out.println("Temps moyen par PDF : " + (total / (double) count) + " ms");
  }

  @Test
  void testRenderMultiplePDFsPerformanceAsync() {
    int count = 50;
    String htmlContent = rendererService.parseThymeleafTemplate("full", new Context());
    long start = System.currentTimeMillis();

    List<CompletableFuture<Void>> futures = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      String filename = "test-playwright-" + i + "-" + UUID.randomUUID() + ".pdf";
      Path outputPath = new File("target", filename).toPath();

      CompletableFuture<Void> future =
          pdfGenerator
              .generatePdfAsync(htmlContent)
              .thenAcceptAsync(
                  pdf -> {
                    try {
                      Files.write(
                          outputPath,
                          pdf,
                          StandardOpenOption.CREATE,
                          StandardOpenOption.TRUNCATE_EXISTING);
                      System.out.println(
                          "async PDF generation completed at: " + outputPath.toAbsolutePath());
                    } catch (IOException e) {
                      throw new RuntimeException(e);
                    }
                  },
                  taskExecutor); // Specify the executor

      futures.add(future);
    }

    // Wait for all futures to complete
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    long end = System.currentTimeMillis();
    long total = end - start;
    System.out.println("Temps total pour générer " + count + " PDF : " + total + " ms");
    System.out.println("Temps moyen par PDF : " + (total / (double) count) + " ms");
  }
}
