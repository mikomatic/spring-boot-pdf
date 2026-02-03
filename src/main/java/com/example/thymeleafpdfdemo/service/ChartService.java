package com.example.thymeleafpdfdemo.service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;

@Service
public class ChartService {

    public String generateReadLengthChartSvg(List<Double> readLengths, boolean generateRandom) {
        List<Double> data = readLengths;
        if (generateRandom) {
            data = generateRandomReadLengths(100); // Générer 100 longueurs aléatoires
        }
        // Créer une série de données pour les longueurs de lectures
        XYSeries series = new XYSeries("Read Lengths");
        for (int i = 0; i < data.size(); i++) {
            series.add(i, data.get(i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        // Créer le graphique linéaire
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Read Lengths Chart", // Titre
            "Index", // Axe X
            "Length", // Axe Y
            dataset,
            PlotOrientation.VERTICAL,
            true, // Légende
            true, // Tooltips
            false // URLs
        );

        // Configurer l'apparence
        chart.setBackgroundPaint(Color.white);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);

        // Générer SVG
        org.jfree.svg.SVGGraphics2D g2 = new org.jfree.svg.SVGGraphics2D(600, 400);
        chart.draw(g2, new Rectangle(0, 0, 600, 400));
        return g2.getSVGElement();
    }

    private List<Double> generateRandomReadLengths(int count) {
        Random random = new Random();
        List<Double> lengths = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lengths.add(100 + random.nextDouble() * 900); // Longueurs entre 100 et 1000 bp
        }
        return lengths;
    }
}
