package com.example.thymeleafpdfdemo.pdf;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service(PlaywrightPDFGenerator.NAME)
public class PlaywrightPDFGenerator implements PDFGenerator {

  public static final String NAME = "playwright";
  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;

  @PostConstruct
  public void init() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    context = browser.newContext();
  }

  @PreDestroy
  public void cleanup() {
    if (context != null) {
      context.close();
    }
    if (browser != null) {
      browser.close();
    }
    if (playwright != null) {
      playwright.close();
    }
  }

  @Override
  public byte[] generatePdf(String html) throws IOException {
    log.info("Starting PDF generation using Playwright");
    try (Page page = context.newPage()) {
      page.setContent(html);
      page.waitForLoadState(LoadState.NETWORKIDLE);

      byte[] bytes = page.pdf(new Page.PdfOptions().setFormat("A4").setPrintBackground(true));
      log.info("PDF generation completed");
      return bytes;
    }
  }

  @Async
  @ConcurrencyLimit(value = 4)
  @Override
  public void generatePdfAsync(String html, Path outputPath) {
    log.info("starting async PDF generation using Playwright");
    try (Playwright playwright = Playwright.create();
        Browser browser =
            playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(java.util.Arrays.asList(
                    "--disable-dev-shm-usage", // Use /tmp instead of /dev/shm to bypass Docker limits
                    "--no-sandbox",            // Gain RAM by removing extra isolation layers inside the container
                    "--disable-gpu",           // Don't add unnecessary render load since there's no GPU on the server
                    "--single-process"         // Consolidate process management into a single process tree
                )))) {

      BrowserContext context = browser.newContext();

      Page page = context.newPage();
      page.setContent(html);
      page.waitForLoadState(LoadState.NETWORKIDLE);

      byte[] bytes = page.pdf(new Page.PdfOptions().setFormat("A4").setPrintBackground(true));
      Files.write(outputPath, bytes);
      log.info("async PDF generation completed at: " + outputPath.toAbsolutePath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
