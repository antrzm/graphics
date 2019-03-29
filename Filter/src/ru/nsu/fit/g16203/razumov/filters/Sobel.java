package ru.nsu.fit.g16203.razumov.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Sobel implements Filter {
    private int threshold;

    public Sobel(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        for (int j = 2; j < srcHeight - 2; j++) {
            for (int i = 2; i < srcWidth - 2; i++) {
                Color color = new Color(src.getRGB(i - 1, j - 1));
                int Ya = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i - 1, j));
                int Yd = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i - 1, j));
                int Yg = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i, j - 1));
                int Yb = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i, j + 1));
                int Yh = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i + 1, j - 1));
                int Yc = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i + 1, j));
                int Yf = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i - 1, j + 1));
                int Yi = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                int C = abs(Yc + 2 * Yf + Yi - Ya - 2 * Yd - Yg) + abs(Yg + 2 * Yh + Yi - Ya - 2 * Yb - Yc);
                if (C <= threshold) {
                    dst.setRGB(i,j, Color.BLACK.getRGB());
                } else {
                    dst.setRGB(i,j, Color.WHITE.getRGB());
                }
            }
        }
        return dst;
    }
}
