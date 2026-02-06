package com.example.thymeleafpdfdemo.pdf;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service(PlaywrightPDFGenerator.NAME)
public class PlaywrightPDFGenerator implements PDFGenerator {

  public static final String NAME = "playwright";

  public static final List<String> CHROME_FLAGS =
      Arrays.asList(
          // Use /tmp instead of /dev/shm to bypass Docker's small shared memory limits
          "--disable-dev-shm-usage",
          // Remove sandboxing for containerized environments (reduces memory overhead)
          "--no-sandbox",
          // Disable GPU hardware acceleration (not available in headless/server environments)
          "--disable-gpu",
          // Run browser in single process mode to reduce memory footprint
          "--single-process");

  @Override
  public byte[] generatePdf(String html) {
    log.info("Starting PDF generation using Playwright");
    try (Playwright playwright = Playwright.create();
        Browser browser =
            playwright.chromium().launch(new BrowserType.LaunchOptions().setArgs(CHROME_FLAGS))) {

      BrowserContext context = browser.newContext();
      Page page = context.newPage();
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
  public CompletableFuture<byte[]> generatePdfAsync(String html) {
    log.info("starting async PDF generation using Playwright");
    try (Playwright playwright = Playwright.create();
        Browser browser =
            playwright.chromium().launch(new BrowserType.LaunchOptions().setArgs(CHROME_FLAGS))) {

      BrowserContext context = browser.newContext();

      Page page = context.newPage();
      page.setContent(html);
      page.waitForLoadState(LoadState.NETWORKIDLE);

      byte[] bytes = page.pdf(new Page.PdfOptions().setFormat("A4").setPrintBackground(true));
      return CompletableFuture.completedFuture(bytes);
    }
  }
}
