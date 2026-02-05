# Spring Boot PDF Generation Demo

A comprehensive demonstration project showcasing different approaches to generate PDF reports from HTML templates using Spring Boot.

## 📋 Overview

This project demonstrates two distinct methods for generating PDF documents from HTML templates:

1. **Flying Saucer (IText Renderer)** - A lightweight, traditional approach for PDF generation
2. **Playwright** - A modern browser-based approach supporting advanced CSS and JavaScript

The project includes a genomic sequencing report template with charts, tables, and advanced styling to showcase the capabilities of each approach.

## 🎯 Features

- **Multiple PDF Generation Strategies**: Compare Flying Saucer vs Playwright implementations
- **Thymeleaf Template Engine**: Dynamic HTML template rendering
- **Chart Generation**: SVG chart generation with JFreeChart
- **Async PDF Generation**: Concurrent PDF generation with rate limiting
- **REST API Endpoints**: Simple API to trigger PDF generation
- **Spring Boot 4.0.2**: Latest Spring Boot framework
- **Professional Report Templates**: Real-world genomic sequencing report examples

## 🛠️ Technology Stack

- **Java 25**
- **Spring Boot 4.0.2**
- **Thymeleaf** - Template engine
- **Flying Saucer (IText Renderer) 10.0.6** - PDF generation
- **Playwright 1.49.0** - Browser-based PDF generation
- **JFreeChart 1.5.6** - Chart generation
- **Apache Batik** - SVG to PNG conversion
- **Lombok** - Code generation
- **Maven** - Build tool

## 📦 Prerequisites

- Java 25 or higher
- Maven 3.6+
- WSL/Linux environment (for Playwright browser installation)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-boot-pdf
```

### 2. Install Playwright Browsers

Before running the application, install Playwright's browser binaries:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

Or install all browsers:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

### 3. Build the Project

```bash
./mvnw clean install
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## 📖 Usage

### Generate PDF with Flying Saucer

```bash
curl "http://localhost:8080/default?template=demo"
```

Or for the full report:

```bash
curl "http://localhost:8080/default?template=full"
```

### Generate PDF with Playwright

```bash
curl "http://localhost:8080/playwright?template=demo"
```

Or for the full report:

```bash
curl "http://localhost:8080/playwright?template=full"
```

The generated PDFs will be saved to your home directory with filenames like:
- `demo-default-{uuid}.pdf` (Flying Saucer)
- `demo-playwright-{uuid}.pdf` (Playwright)

## 🏗️ Project Structure

```
spring-boot-pdf/
├── src/
│   ├── main/
│   │   ├── java/com/example/thymeleafpdfdemo/
│   │   │   ├── PdfDemoApplication.java          # Main application
│   │   │   ├── DemoController.java              # REST endpoints
│   │   │   ├── config/
│   │   │   │   ├── AsyncConfig.java             # Async configuration
│   │   │   │   └── TemplateEngineConfig.java    # Thymeleaf config
│   │   │   ├── pdf/
│   │   │   │   ├── PDFGenerator.java            # PDF generator interface
│   │   │   │   ├── DefaultPDFGenerator.java     # Flying Saucer implementation
│   │   │   │   └── PlaywrightPDFGenerator.java  # Playwright implementation
│   │   │   └── service/
│   │   │       ├── ChartService.java            # Chart generation
│   │   │       └── RendererService.java         # Template rendering
│   │   └── resources/
│   │       └── templates/
│   │           ├── demo.html                    # Simple report template
│   │           └── full.html                    # Full genomic report template
│   └── test/
│       └── java/com/example/thymeleafpdfdemo/
│           ├── pdf/
│           │   ├── DefaultPDFGeneratorTest.java
│           │   └── PlaywrightPDFGeneratorTest.java
│           └── service/
│               └── RendererServiceTest.java
├── pom.xml
└── README.md
```

## 🔍 Comparison: Flying Saucer vs Playwright

### Flying Saucer (Default)

**Pros:**
- ✅ Lightweight and fast
- ✅ No browser dependency
- ✅ Low memory footprint
- ✅ Predictable output
- ✅ Good for simple layouts

**Cons:**
- ❌ Limited CSS support (mostly CSS 2.1)
- ❌ No JavaScript execution
- ❌ No modern CSS features (Flexbox, Grid)
- ❌ Charts must be embedded as images

**Best for:** Simple reports, invoices, certificates with basic styling

### Playwright

**Pros:**
- ✅ Full modern CSS support (Flexbox, Grid, etc.)
- ✅ JavaScript execution (ECharts, D3.js work natively)
- ✅ Perfect rendering accuracy
- ✅ Supports web fonts and modern features
- ✅ Print media queries support

**Cons:**
- ❌ Requires browser installation (~170MB for Chromium)
- ❌ Higher memory usage
- ❌ Slower generation time
- ❌ More complex deployment

**Best for:** Complex reports with charts, dashboards, modern layouts

## 📊 Template Examples

### Demo Template (`demo.html`)
A simple genomic sequencing report with:
- Patient information
- Quality metrics table
- Variants table
- Embedded SVG chart converted to PNG

### Full Template (`full.html`)
A comprehensive genomic report featuring:
- Professional header and styling
- Metrics grid with cards
- Interactive ECharts visualizations:
  - Quality score distribution (bar chart)
  - Coverage by chromosome (line chart)
- Responsive design
- Print-optimized CSS
- Multi-page support

## ⚙️ Configuration

### Async Configuration

The project includes concurrency limiting for Playwright PDF generation:

```java
@Async
@ConcurrencyLimit(value = 4)
public void generatePdfAsync(String html, Path outputPath)
```

This prevents resource exhaustion when generating multiple PDFs concurrently.

### Playwright Options

Playwright is configured with browser launch options for server environments:

```java
.setArgs(Arrays.asList(
    "--disable-dev-shm-usage",  // Use /tmp instead of /dev/shm
    "--no-sandbox",             // Remove extra isolation layers
    "--disable-gpu",            // No GPU rendering needed
    "--single-process"          // Single process tree
))
```

## 🧪 Testing

Run all tests:

```bash
./mvnw test
```

Run specific test class:

```bash
./mvnw test -Dtest=PlaywrightPDFGeneratorTest
```

The tests generate sample PDFs in the `target/` directory for visual inspection.

## 🐳 Docker Considerations

When deploying with Docker, ensure:

1. Chromium dependencies are installed:
```dockerfile
RUN apt-get update && apt-get install -y \
    libnss3 libnspr4 libatk1.0-0 libatk-bridge2.0-0 \
    libcups2 libdrm2 libxkbcommon0 libxcomposite1 \
    libxdamage1 libxfixes3 libxrandr2 libgbm1 \
    libpango-1.0-0 libcairo2 libasound2
```

2. Install Playwright browsers in the image:
```dockerfile
RUN mvn exec:java -D exec.mainClass=com.microsoft.playwright.CLI \
    -D exec.args="install chromium"
```

3. Configure shared memory properly or use `--disable-dev-shm-usage`

## 📝 API Documentation

### `GET /default`

Generate PDF using Flying Saucer.

**Parameters:**
- `template` (required): Template name (`demo` or `full`)

**Response:** String with file path

**Example:**
```bash
curl "http://localhost:8080/default?template=full"
```

### `GET /playwright`

Generate PDF using Playwright.

**Parameters:**
- `template` (required): Template name (`demo` or `full`)

**Response:** String with file path

**Example:**
```bash
curl "http://localhost:8080/playwright?template=full"
```

## 🤝 Contributing

Feel free to submit issues and enhancement requests!

## 📄 License

This is a demonstration project for educational purposes.

## 🔗 Related Resources

- [Flying Saucer Documentation](https://github.com/flyingsaucerproject/flyingsaucer)
- [Playwright Java Documentation](https://playwright.dev/java/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [ECharts Documentation](https://echarts.apache.org/en/index.html)
- [JFreeChart Documentation](https://www.jfree.org/jfreechart/)

## 🐛 Troubleshooting

### Playwright Browser Not Found

If you get "Executable doesn't exist" error:

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

### Out of Memory Errors

Increase JVM heap size:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2048m"
```

### PDF Not Generated

Check logs for errors and ensure:
1. Template exists in `src/main/resources/templates/`
2. Template name matches exactly (without `.html` extension)
3. Write permissions to home directory

## 👥 Author

Created as a demo project for comparing PDF generation approaches in Spring Boot.

---

**Note:** This project uses Java 25 and Spring Boot 4.0.2. Ensure your development environment is properly configured.
