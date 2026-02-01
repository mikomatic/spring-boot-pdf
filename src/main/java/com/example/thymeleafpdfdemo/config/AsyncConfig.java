package com.example.thymeleafpdfdemo.config;

import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableResilientMethods
public class AsyncConfig {}
