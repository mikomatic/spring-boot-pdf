package com.example.thymeleafpdfdemo.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service(DefaultPDFGenerator.NAME)
public class DefaultPDFGenerator implements PDFGenerator {

  public static final String NAME = "default";

  @Override
  public byte[] generatePdf(String html) throws IOException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocumentFromString(html);
      renderer.layout();
      renderer.createPDF(outputStream);
      renderer.finishPDF();
      return outputStream.toByteArray();
    }
  }

  @Override
  public void generatePdfAsync(String html, Path outputPath) {
    throw new UnsupportedOperationException();
  }
}
