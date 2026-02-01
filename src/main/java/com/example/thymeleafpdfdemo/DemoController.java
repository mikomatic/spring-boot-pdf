package com.example.thymeleafpdfdemo;

import com.example.thymeleafpdfdemo.pdf.DefaultPDFGenerator;
import com.example.thymeleafpdfdemo.pdf.PDFGenerator;
import com.example.thymeleafpdfdemo.pdf.PlaywrightPDFGenerator;
import com.example.thymeleafpdfdemo.service.RendererService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DemoController {

  private final Map<String, PDFGenerator> pdfGenerators;

  private final RendererService rendererService;

  @GetMapping("/default")
  public String createDefault(@RequestParam String template) throws IOException {
    log.info("Generating report using Flying Saucer...");
    String html = rendererService.parseThymeleafTemplate(template);
    byte[] bytes = pdfGenerators.get(DefaultPDFGenerator.NAME).generatePdf(html);
    String filename = "demo-default-" + UUID.randomUUID() + ".pdf";
    String outputFolder = System.getProperty("user.home") + File.separator + filename;

    Files.write(new File(outputFolder).toPath(), bytes);

    log.info("PDF generated at: " + outputFolder);
    return "PDF generated using Flying Saucer: " + outputFolder;
  }

  @GetMapping("/playwright")
  public String createPlaywright(@RequestParam String template) throws IOException {
    log.info("Generating report using Playwright...");
    String html = rendererService.parseThymeleafTemplate(template);
    byte[] bytes = pdfGenerators.get(PlaywrightPDFGenerator.NAME).generatePdf(html);
    String filename = "demo-playwright-" + UUID.randomUUID() + ".pdf";
    String outputFolder = System.getProperty("user.home") + File.separator + filename;

    Files.write(new File(outputFolder).toPath(), bytes);

    log.info("PDF generated at: " + outputFolder);
    return "PDF generated using Playwright Saucer: " + outputFolder;
  }
}
