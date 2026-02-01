package com.example.thymeleafpdfdemo.pdf;

import java.io.IOException;
import java.nio.file.Path;

public interface PDFGenerator {

  byte[] generatePdf(String html) throws IOException;

  void generatePdfAsync(String html, Path outputPath);
}
