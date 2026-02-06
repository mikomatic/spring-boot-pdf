package com.example.thymeleafpdfdemo.pdf;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface PDFGenerator {

  byte[] generatePdf(String html) throws IOException;

  CompletableFuture<byte[]> generatePdfAsync(String html);
}
