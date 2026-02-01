package com.example.thymeleafpdfdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class RendererService {

  private final TemplateEngine templateEngine;

 public String parseThymeleafTemplate(String template) {

    Context context = new Context();
    context.setVariable("to", "Baeldung");

    return templateEngine.process(template, context);
  }
}
