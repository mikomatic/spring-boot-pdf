package com.example.thymeleafpdfdemo.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class RendererService {

  private final TemplateEngine templateEngine;
  private final ChartService chartService;

  public String parseThymeleafTemplate(String template, Context context) {

    // temporary dummy data to pass to the context, replace with actual data as needed
    context.setVariable("to", "Baeldung");
    String svgChart = chartService.generateReadLengthChartSvg(List.of(), true);
    String imgTag = convertSvgToPngDataUrl(svgChart);
    context.setVariable("svg", imgTag);
    //

    return templateEngine.process(template, context);
  }

  private String convertSvgToPngDataUrl(String svg) {
    try (ByteArrayInputStream inputStream =
            new ByteArrayInputStream(svg.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

      PNGTranscoder transcoder = new PNGTranscoder();
      TranscoderInput input = new TranscoderInput(inputStream);
      TranscoderOutput output = new TranscoderOutput(outputStream);

      transcoder.transcode(input, output);

      String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
      return "<img src=\"data:image/png;base64," + base64 + "\" />";

    } catch (IOException | TranscoderException e) {
      throw new RuntimeException("Failed to convert SVG to PNG", e);
    }
  }
}
