package com.example.thymeleafpdfdemo.pdf;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
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
          "--single-process",
          // Prevent auto-playing media content
          "--autoplay-policy=user-gesture-required",
          // Disable network requests made in the background
          "--disable-background-networking",
          // Prevent throttling of timers in background tabs
          "--disable-background-timer-throttling",
          // Disable throttling for windows that are occluded/hidden
          "--disable-backgrounding-occluded-windows",
          // Disable crash reporting service
          "--disable-breakpad",
          // Disable client-side phishing detection
          "--disable-client-side-phishing-detection",
          // Disable automatic component updates
          "--disable-component-update",
          // Don't install default apps on first run
          "--disable-default-apps",
          // Disable domain reliability monitoring
          "--disable-domain-reliability",
          // Disable browser extensions
          "--disable-extensions",
          // Keep audio service in the main process
          "--disable-features=AudioServiceOutOfProcess",
          // Disable hang monitor for unresponsive pages
          "--disable-hang-monitor",
          // Disable IPC flooding protection
          "--disable-ipc-flooding-protection",
          // Disable desktop notifications
          "--disable-notifications",
          // Disable offering to store credit card data
          "--disable-offer-store-unmasked-wallet-cards",
          // Disable popup blocking
          "--disable-popup-blocking",
          // Disable print preview feature
          "--disable-print-preview",
          // Disable prompt when reposting form data
          "--disable-prompt-on-repost",
          // Prevent renderer process from running in background
          "--disable-renderer-backgrounding",
          // Disable setuid sandbox (alternative sandboxing method)
          "--disable-setuid-sandbox",
          // Disable speech synthesis API
          "--disable-speech-api",
          // Disable Chrome Sync service
          "--disable-sync",
          // Set disk cache size to 32MB
          "--disk-cache-size=33554432",
          // Hide scrollbars for cleaner rendering
          "--hide-scrollbars",
          // Ignore GPU blacklist to allow software rendering
          "--ignore-gpu-blacklist",
          // Only record metrics without sending them
          "--metrics-recording-only",
          // Mute all audio output
          "--mute-audio",
          // Skip default browser check
          "--no-default-browser-check",
          // Skip first run tasks and prompts
          "--no-first-run",
          // Disable hyperlink ping tracking
          "--no-pings",
          // Disable zygote process for forking renderers
          "--no-zygote",
          // Use basic password storage (not system keychain)
          "--password-store=basic",
          // Use SwiftShader software renderer instead of GPU
          "--use-gl=swiftshader",
          // Use mock keychain for testing
          "--use-mock-keychain");

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
  public void generatePdfAsync(String html, Path outputPath) {
    log.info("starting async PDF generation using Playwright");
    try (Playwright playwright = Playwright.create();
        Browser browser =
            playwright.chromium().launch(new BrowserType.LaunchOptions().setArgs(CHROME_FLAGS))) {

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
