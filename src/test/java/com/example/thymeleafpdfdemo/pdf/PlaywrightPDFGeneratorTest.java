package com.example.thymeleafpdfdemo.pdf;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

import com.example.thymeleafpdfdemo.config.AsyncConfig;
import com.example.thymeleafpdfdemo.service.RendererService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(AsyncConfig.class)
public class PlaywrightPDFGeneratorTest {

  @Autowired private PlaywrightPDFGenerator pdfGenerator;

  @Autowired private RendererService rendererService;

  @Test
  void testRenderMultiplePDFsPerformance() throws Exception {
    int count = 10;
    String htmlContent = rendererService.parseThymeleafTemplate("full");
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
  void testRenderMultiplePDFsPerformanceAsync() throws Exception {
    int count = 100;
    String htmlContent = rendererService.parseThymeleafTemplate("full");
    long start = System.currentTimeMillis();
    Path outputPath = null;
    for (int i = 0; i < count; i++) {
      String filename = "test-playwright-" + i + "-" + UUID.randomUUID() + ".pdf";
      outputPath = new File("target", filename).toPath();
      pdfGenerator.generatePdfAsync(htmlContent, outputPath);
    }
    Path finalOutputPath = outputPath;
    await().atMost(2, TimeUnit.MINUTES).until(() -> finalOutputPath.toFile().exists());
    long end = System.currentTimeMillis();
    long total = end - start;
    System.out.println("Temps total pour générer " + count + " PDF : " + total + " ms");
    System.out.println("Temps moyen par PDF : " + (total / (double) count) + " ms");
  }
}
