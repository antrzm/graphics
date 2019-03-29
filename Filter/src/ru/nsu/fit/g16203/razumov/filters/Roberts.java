package ru.nsu.fit.g16203.razumov.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class Roberts implements Filter {

    private int threshold;

    public Roberts(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public BufferedImage Apply(BufferedImage src) {
        int srcHeight = src.getHeight();
        int srcWidth = src.getWidth();
        BufferedImage dst = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);

        for (int j = 1; j < srcHeight - 1; j++) {
            for (int i = 1; i < srcWidth - 1; i++) {
                Color color = new Color(src.getRGB(i, j));
                int Y00 = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i + 1, j + 1));
                int Y11 = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i + 1, j));
                int Y10 = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                color = new Color(src.getRGB(i, j + 1));
                int Y01 = (int) (0.3 * color.getRed() + 0.59 * color.getGreen() + 0.11 * color.getBlue());

                int F = abs(Y11 - Y00) + abs(Y10 - Y01);
                if (F <= threshold) {
                    dst.setRGB(i, j, Color.BLACK.getRGB());
                } else {
                    dst.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        }
        return dst;
    }
}
